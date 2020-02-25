package com.lxc.game.gobang.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;

@SpringBootApplication
public class GobangServerApplication {

    private static ConfigurableApplicationContext applicationContext;

    public static <T> T getDao(Class<T> daoType){
        return applicationContext.getBean(daoType);
    }

    public static void main(String[] args) throws InterruptedException {
        applicationContext = SpringApplication.run(GobangServerApplication.class, args);

        Thread server = new Thread(Server.start(), "Gobang-Server");
        server.start();

        Scanner scanner = new Scanner(System.in);

        while (true){
            String command = scanner.nextLine();
            switch (command){
                case "restart":
                    Server.end = true;
                    server.interrupt();
                    System.out.println("等待服务器结束");
                    server.join();
                    System.out.println("重启服务器");
                    server = new Thread(Server.start(), "Gobang-Server");
                    server.start();
                    break;
            }
        }

    }

}
