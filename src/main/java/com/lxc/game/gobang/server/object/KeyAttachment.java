package com.lxc.game.gobang.server.object;

import com.lxc.game.gobang.server.dao.pojo.GobangUser;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public class KeyAttachment {



    private final ActiveUser user;

    private final ByteBuffer buffer;

    public KeyAttachment(GobangUser gobangUser, SelectionKey bindKey){
        user = new ActiveUser(gobangUser, bindKey);
        buffer = (ByteBuffer) bindKey.attachment();
    }

    public ActiveUser getUser() {
        return user;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }
}
