package com.lxc.game.gobang.server;

import com.lxc.game.gobang.server.constant.Const;
import com.lxc.game.gobang.server.dao.pojo.GobangUser;
import com.lxc.game.gobang.server.exception.ClientClosedException;
import com.lxc.game.gobang.server.exception.IllegalException;
import com.lxc.game.gobang.server.handler.Executor;
import com.lxc.game.gobang.server.handler.Matcher;
import com.lxc.game.gobang.server.handler.impl.CommonExecutor;
import com.lxc.game.gobang.server.handler.impl.SimpleMatcher;
import com.lxc.game.gobang.server.object.ActiveUser;
import com.lxc.game.gobang.server.object.KeyAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

import static com.lxc.game.gobang.server.constant.Const.BUFFER_SIZE;

public class Server implements Runnable{

    private static final int PORT = 4567;

    private final Logger coreLog = LoggerFactory.getLogger("Server Core");

    private Executor executor;

    private Matcher matcher;

    private static boolean running = false;

    private Map<String, Method> requestMap;

    private Server() {
        coreLog.info("进行初始化");
        requestMap = new HashMap<>();
        Class<Executor> aClass = Executor.class;
        try {
            requestMap.put("al", aClass.getMethod("login", List.class, SelectionKey.class));
            requestMap.put("ar", aClass.getMethod("register", List.class, SelectionKey.class));
            requestMap.put("ag", aClass.getMethod("guest", List.class, SelectionKey.class));
            requestMap.put("m", aClass.getMethod("match", List.class, ActiveUser.class, Matcher.class));
            requestMap.put("mr", aClass.getMethod("room", List.class, ActiveUser.class, Matcher.class));
            requestMap.put("ae", aClass.getMethod("logout", List.class, ActiveUser.class));
            requestMap.put("bs", aClass.getMethod("step", List.class, ActiveUser.class));
            requestMap.put("be", aClass.getMethod("surrender", List.class, ActiveUser.class));
        } catch (NoSuchMethodException ignored){

        }
        executor = new CommonExecutor();
        matcher = new SimpleMatcher();
    }

    public static boolean end = false;

    public synchronized static Server start(){
        if (!running){
            return new Server();
        } else {
            return null;
        }
    }

    @Override
    public void run() {
        running = true;
        coreLog.info("配置选择器和服务器通道");
        Selector selector = null;
        ServerSocketChannel serverChannel = null;
        boolean initSuccess = false;
        do {
            try {
                selector = Selector.open();
                serverChannel = ServerSocketChannel.open();
                serverChannel.socket().bind(new InetSocketAddress(PORT));
                serverChannel.configureBlocking(false);
                serverChannel.register(selector, SelectionKey.OP_ACCEPT);
                initSuccess = true;
            } catch (IOException e){
                e.printStackTrace();
                coreLog.error("配置选择器和服务器通道时发生IO错误 60秒后重试");
                try {
                    Thread.sleep(60000);
                } catch (Exception ex) {
                    coreLog.error("终止进程");
                    close(selector, serverChannel);
                    return;
                }
            } catch (Exception e){
                e.printStackTrace();
                coreLog.error("配置选择器和服务器通道时发生位置错误 终止进程");
                close(selector, serverChannel);
                return;
            }
        } while (!initSuccess);
        coreLog.info("服务器准备就绪 已开放端口: " + PORT);
        assert selector != null;
        while (true){
            try {
                if (selector.select(3000) == 0) {
                    if (end){
                        coreLog.info("执行终止命令");
                        close(selector, serverChannel);
                        return;
                    }
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
                coreLog.error("等待请求时发生IO错误 60秒后重试");
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException ex) {
                    coreLog.error("终止进程");
                    close(selector, serverChannel);
                    return;
                }
                continue;
            }
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()){
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()){
                    coreLog.info("建立新的连接");
                    accept(key);
                } else if (key.isReadable()){
                    coreLog.info("处理请求");
                    request(key);
                }
                keyIterator.remove();
            }
        }
    }

    private void accept(SelectionKey key) {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        try {
            SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);
            sc.register(key.selector(), SelectionKey.OP_READ,
                    ByteBuffer.allocateDirect(BUFFER_SIZE));
        } catch (IOException ignored){
            coreLog.warn("建立新连接时发生IO错误");
        }

    }

    private void request(SelectionKey key){
        SocketChannel sc = (SocketChannel) key.channel();
        Object attachment = key.attachment();
        ByteBuffer buffer;
        try {
            if (attachment instanceof ByteBuffer){
                buffer = (ByteBuffer) attachment;
                GobangUser user;
                List<String> params = new ArrayList<>();
                Method method = analysis(buffer, sc, params);
                user = (GobangUser) method.invoke(executor, params, key);
                if (user != null){
                    key.attach(new KeyAttachment(user, key));
                    coreLog.info(user.getBase().getUsername()+ " 登录了");
                }
            } else {
                buffer = ((KeyAttachment) attachment).getBuffer();
                ActiveUser user =  ((KeyAttachment) attachment).getUser();
                List<String> params = new ArrayList<>();
                Method method = analysis(buffer, sc, params);
                try {
                    if (method.getParameterTypes().length >= 3 &&
                            method.getParameterTypes()[2].getName().equals(Matcher.class.getName())){
                        method.invoke(executor, params, user, matcher);
                    } else {
                        method.invoke(executor, params, user);
                    }
                } catch (InvocationTargetException e){
                    throw e.getCause();
                }
            }
        } catch (ClientClosedException e){
            coreLog.info("连接已断开");
            try {
                sc.close();
            } catch (IOException ignored) {
                coreLog.warn("有一项连接断开时出现了IO错误");
            }
        } catch (IllegalArgumentException e){
            coreLog.error("已登录用户发送了登录请求 检查客户端");
            try {
                sc.close();
            } catch (IOException ex) {
                coreLog.error("断开连接时出现了IO错误");
            }
        } catch (IllegalException e){
            coreLog.warn("非法访问 断开连接");
            try {
                sc.close();
            } catch (IOException ex) {
                coreLog.warn("断开非法连接时出现了IO错误");
            }
        } catch (IOException e){
            e.printStackTrace();
            coreLog.error("尝试执行请求时出现IO错误");
            try {
                sc.close();
            } catch (IOException ignored) {
                coreLog.warn("断开出现IO错误的连接时出现错误");
            }
        } catch (Throwable e){
            e.printStackTrace();
            coreLog.error("出现未被捕捉的错误 断开连接");
            try {
                sc.close();
            } catch (IOException ex) {
                coreLog.error("断开出现未知错误的连接时出现了IO错误");
            }
        }
    }

    private Method analysis(ByteBuffer buffer, SocketChannel sc, List<String> params)
            throws IOException, IllegalException, ClientClosedException {
        buffer.clear().rewind();
        int length = sc.read(buffer);
        if (length == -1){
            throw new ClientClosedException();
        }
        buffer.flip();
        byte[] bytes = new byte[length];
        buffer.get(bytes, 0, length);
        String result = new String(bytes, 0, length);
        String[] split = result.split(Const.SPLIT);
        if (split.length < 2){
            throw new IllegalException();
        }
        Method method = requestMap.get(split[0]);
        params.addAll(Arrays.asList(split).subList(1, split.length));
        if (method == null){
            throw new IllegalException();
        }
        return method;
    }

    private void close(Selector selector, ServerSocketChannel ssc) {
        boolean closeSuccess = false;
        do {
            try {
                if (selector != null){
                    selector.close();
                }
                if (ssc != null){
                    ssc.close();
                }
            } catch (IOException ignored) {
                continue;
            }
            closeSuccess = true;
        } while (!closeSuccess);
        end = false;
        running = false;
    }

}
