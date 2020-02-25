package com.lxc.game.gobang.server.handler;

import com.lxc.game.gobang.server.object.ActiveUser;

public interface Matcher {

    ActiveUser match(ActiveUser user);

    ActiveUser match(ActiveUser user, int roomNumber);

}
