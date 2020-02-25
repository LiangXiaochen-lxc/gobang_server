package com.lxc.game.gobang.server.handler.impl;

import com.lxc.game.gobang.server.handler.Matcher;
import com.lxc.game.gobang.server.object.ActiveGame;
import com.lxc.game.gobang.server.object.ActiveUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SimpleMatcher implements Matcher {

    private ActiveUser matching = null;

    private final Logger matcherLog = LoggerFactory.getLogger("Matcher");

    private Map<Integer, ActiveUser> room = new HashMap<>();

    @Override
    public ActiveUser match(ActiveUser user) {
        ActiveUser opponent = null;
        if (matching == null){
            matching = user;
            matcherLog.info(user.getUsername() + " 开始匹配");
        } else if (matching.equals(user)){
            matching = null;
            matcherLog.info(user.getUsername() + " 取消匹配");
        } else {
            if (matching.getKey().channel().isOpen()){
                newGame(matching, user);
                opponent = matching;
                matching = null;
            } else {
                matching = user;
                matcherLog.info(user.getUsername() + " 开始匹配");
            }
        }
        return opponent;
    }

    @Override
    public ActiveUser match(ActiveUser user, int roomNumber) {
        ActiveUser creator = room.get(roomNumber);
        if (creator == null){
            room.put(roomNumber, user);
            matcherLog.info(user.getUsername() + " 进入" + roomNumber);
        } else if (creator.equals(user)){
            room.remove(roomNumber);
            creator = null;
            matcherLog.info(user.getUsername() + " 退出" + roomNumber);
        } else {
            if (creator.getKey().channel().isOpen()){
                newGame(creator, user);
                room.remove(roomNumber);
            } else {
                room.remove(roomNumber);
                room.put(roomNumber, user);
                matcherLog.info(user.getUsername() + " 进入" + roomNumber);
            }
        }
        return creator;
    }

    private void newGame(ActiveUser user1, ActiveUser user2){
        new ActiveGame(user1, user2, new CommonJudgment(user1));
        matcherLog.info(user1 + " " + user2 + " 匹配成功");
    }

}
