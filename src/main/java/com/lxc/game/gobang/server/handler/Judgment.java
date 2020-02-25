package com.lxc.game.gobang.server.handler;

import com.lxc.game.gobang.server.object.ActiveGame;
import com.lxc.game.gobang.server.object.ActiveUser;

public interface Judgment {

    int getBoardSize();

    int step(ActiveUser player, int x, int y);

    void surrender(ActiveUser player);

    void setGame(ActiveGame game);

    ActiveGame getGame();

    ActiveUser getOpponent(ActiveUser user);

}
