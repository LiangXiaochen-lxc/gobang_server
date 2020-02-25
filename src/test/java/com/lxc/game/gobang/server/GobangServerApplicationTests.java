package com.lxc.game.gobang.server;

import com.lxc.game.gobang.server.dao.GobangGameDao;
import com.lxc.game.gobang.server.dao.GobangUserDao;
import com.lxc.game.gobang.server.dao.UserDao;
import com.lxc.game.gobang.server.dao.pojo.User;
import com.lxc.game.gobang.server.dao.pojo.GobangGame;
import com.lxc.game.gobang.server.dao.pojo.GobangUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.UUID;

@SpringBootTest
class GobangServerApplicationTests {

    @Autowired
    private GobangUserDao gobangUserDao;

    @Autowired
    private GobangGameDao gobangGameDao;

    @Autowired
    private UserDao userDao;

    @Test
    void contextLoads() {

    }

}
