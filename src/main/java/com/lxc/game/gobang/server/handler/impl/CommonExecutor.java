package com.lxc.game.gobang.server.handler.impl;

import com.lxc.game.gobang.server.GobangServerApplication;
import com.lxc.game.gobang.server.constant.Response;
import com.lxc.game.gobang.server.dao.GobangGameDao;
import com.lxc.game.gobang.server.dao.GobangUserDao;
import com.lxc.game.gobang.server.dao.UserDao;
import com.lxc.game.gobang.server.dao.pojo.GobangGame;
import com.lxc.game.gobang.server.dao.pojo.GobangUser;
import com.lxc.game.gobang.server.dao.pojo.User;
import com.lxc.game.gobang.server.exception.IllegalException;
import com.lxc.game.gobang.server.handler.Executor;
import com.lxc.game.gobang.server.handler.Judgment;
import com.lxc.game.gobang.server.handler.Matcher;
import com.lxc.game.gobang.server.object.ActiveGame;
import com.lxc.game.gobang.server.object.ActiveUser;
import com.lxc.game.gobang.server.object.KeyAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class CommonExecutor implements Executor {

    private final Logger executorLog = LoggerFactory.getLogger("Executor");

    private UserDao userDao;
    private GobangUserDao gobangUserDao;
    private GobangGameDao gobangGameDao;

    public CommonExecutor(){
        this.userDao = GobangServerApplication.getDao(UserDao.class);
        this.gobangUserDao = GobangServerApplication.getDao(GobangUserDao.class);
        this.gobangGameDao = GobangServerApplication.getDao(GobangGameDao.class);
    }

    private String analyzeAuth(List<String> params, int mode) throws IllegalException {
        String auth;
        try {
            auth = params.get(mode);
        } catch (IndexOutOfBoundsException e){
            throw new IllegalException();
        }
        if (!Pattern.matches("[a-z0-9A-Z]+", auth)){
            executorLog.warn("恶意连接试图植入: " + auth);
            throw new IllegalException();
        }
        return auth;
    }

    @Override
    public GobangUser login(List<String> params, SelectionKey key) throws IllegalException, IOException {
        String username, password;
        User user;
        GobangUser gobangUser = null;
        username = analyzeAuth(params, 0);
        password = analyzeAuth(params, 1);
        user = userDao.findByUsernameAndPassword(username, password);
        if (user != null){
            gobangUser = gobangUserDao.findByBase(user);
            if (gobangUser == null){
                gobangUser = gobangUserDao.save(new GobangUser(user));
            }
            inform(key, Response.LoginSuccess);
        } else {
            inform(key, Response.WrongAuth);
        }
        return gobangUser;
    }

    @Override
    public GobangUser register(List<String> params, SelectionKey key) throws IllegalException, IOException {
        String username, password;
        User user;
        GobangUser gobangUser;
        username = analyzeAuth(params, 0);
        password = analyzeAuth(params, 1);
        try {
            user = userDao.save(new User(username, password));
        } catch (Exception e){
            inform(key, Response.ExistUsername);
            return null;
        }
        gobangUser = new GobangUser(user);
        gobangUser = gobangUserDao.save(gobangUser);
        inform(key, Response.LoginSuccess);
        return gobangUser;
    }

    @Override
    public GobangUser guest(List<String> params, SelectionKey key) throws IllegalException, IOException {
        User user;
        GobangUser gobangUser;
        String mac = analyzeAuth(params, 0);
        user = userDao.findByUsernameAndPassword(mac, mac);
        if (user != null){
            gobangUser = gobangUserDao.findByBase(user);
            if (gobangUser == null){
                gobangUser = gobangUserDao.save(new GobangUser(user));
            }
        } else {
            user = userDao.save(new User(mac, mac));
            gobangUser = new GobangUser(user);
            gobangUser = gobangUserDao.save(gobangUser);
        }
        inform(key, Response.LoginSuccess);
        return gobangUser;
    }

    @Override
    public void match(List<String> params, ActiveUser user, Matcher matcher) throws IOException {
        ActiveUser opponent = matcher.match(user);
        if (opponent != null){
            inform(user, Response.MatchedSecond);
            inform(opponent, Response.MatchedFirst);
        }
    }

    @Override
    public void room(List<String> params, ActiveUser user, Matcher matcher) throws IllegalException, IOException {
        int room;
        try {
            room = Integer.parseInt(params.get(0));
        } catch (Exception e){
            throw new IllegalException();
        }
        ActiveUser creator = matcher.match(user, room);
        if (creator != null){
            inform(user, Response.MatchedSecond);
            inform(creator, Response.MatchedFirst);
        }
    }

    @Override
    public void logout(List<String> params, ActiveUser user) throws IOException {
        SelectionKey key = user.getKey();
        key.attach(((KeyAttachment) key.attachment()).getBuffer());
        executorLog.info(user.getUsername() + " 登出");
        inform(key, Response.Logout);
    }

    @Override
    public void step(List<String> params, ActiveUser user) throws IllegalException, IOException {
        Judgment judgment = user.getJudgment();
        if (judgment != null){
            int code;
            try {
                code = Integer.parseInt(params.get(0));
            } catch (Exception e){
                throw new IllegalException();
            }
            int x = code / 100;
            int y = code % 100;
            int stepResult = judgment.step(user, x, y);
            if (stepResult == ActiveGame.ILLEGAL){
                throw new IllegalException();
            }
            inform(judgment.getOpponent(user), x, y);
            switch (stepResult){
                case ActiveGame.LEFT:
                    inform(judgment.getGame().getLeft(), Response.Win);
                    inform(judgment.getGame().getRight(), Response.Lose);
                    gobangGameDao.save(new GobangGame(judgment.getGame()));
                    break;
                case ActiveGame.RIGHT:
                    inform(judgment.getGame().getLeft(), Response.Lose);
                    inform(judgment.getGame().getRight(), Response.Win);
                    gobangGameDao.save(new GobangGame(judgment.getGame()));
                    break;
                case ActiveGame.DRAW:
                    inform(judgment.getGame().getLeft(), Response.Draw);
                    inform(judgment.getGame().getRight(), Response.Draw);
                    gobangGameDao.save(new GobangGame(judgment.getGame()));
                    break;
            }
        }
    }

    @Override
    public void surrender(List<String> params, ActiveUser user) throws IOException {
        Judgment judgment = user.getJudgment();
        if (judgment != null){
            judgment.surrender(user);
            gobangGameDao.save(new GobangGame(judgment.getGame()));
            inform(user, Response.Lose);
            inform(judgment.getOpponent(user), Response.SurrenderWin);
        }
    }

    private void inform(SelectionKey key, Response response) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer.clear().rewind();
        buffer.put(response.toBytes());
        buffer.flip();
        sc.write(buffer);
    }

    private void inform(ActiveUser user, Response response) throws IOException {
        SocketChannel sc = (SocketChannel) user.getKey().channel();
        ByteBuffer buffer = ((KeyAttachment) user.getKey().attachment()).getBuffer();
        buffer.clear().rewind();
        buffer.put(response.toBytes());
        buffer.flip();
        sc.write(buffer);
    }

    private void inform(ActiveUser user, int x, int y) throws IOException {
        SocketChannel sc = (SocketChannel) user.getKey().channel();
        ByteBuffer buffer = ((KeyAttachment) user.getKey().attachment()).getBuffer();
        buffer.clear().rewind();
        String code = String.valueOf(x * 100 + y);
        buffer.put(code.getBytes());
        buffer.flip();
        sc.write(buffer);
    }
}
