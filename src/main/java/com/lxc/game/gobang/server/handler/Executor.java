package com.lxc.game.gobang.server.handler;

import com.lxc.game.gobang.server.dao.pojo.GobangUser;
import com.lxc.game.gobang.server.exception.IllegalException;
import com.lxc.game.gobang.server.object.ActiveUser;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.List;

public interface Executor {

    GobangUser login(List<String> params, SelectionKey key) throws IllegalException, IOException;

    GobangUser register(List<String> params, SelectionKey key) throws IllegalException, IOException;

    GobangUser guest(List<String> params, SelectionKey key) throws IllegalException, IOException;

    void match(List<String> params, ActiveUser user, Matcher matcher) throws IllegalException, IOException;

    void room(List<String> params, ActiveUser user, Matcher matcher) throws IllegalException, IOException;

    void logout(List<String> params, ActiveUser user) throws IllegalException, IOException;

    void step(List<String> params, ActiveUser user) throws IllegalException, IOException;

    void surrender(List<String> params, ActiveUser user) throws IllegalException, IOException;

}
