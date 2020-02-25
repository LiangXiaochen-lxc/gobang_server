package com.lxc.game.gobang.server.object;

import com.lxc.game.gobang.server.handler.Judgment;
import com.lxc.game.gobang.server.dao.pojo.GobangUser;

import java.nio.channels.SelectionKey;

public class ActiveUser {

    private final SelectionKey key;

    private Judgment judgment = null;

    private final int gid;

    private final int uid;

    private final String username;

    private int point;

    private int exp;

    private int winCount;

    private int loseCount;

    private int drawCount;

    public void setJudgment(Judgment judgment) {
        this.judgment = judgment;
    }

    public Judgment getJudgment() {
        return judgment;
    }

    private ActiveUser(){
        gid = -1;
        uid = -1;
        username = null;
        key = null;
    }

    public GobangUser toGobangUser(){
        return new GobangUser(gid);
    }

    public ActiveUser(GobangUser gobangUser, SelectionKey bindKey){
        gid = gobangUser.getGid();
        uid = gobangUser.getBase().getUid();
        username = gobangUser.getBase().getUsername();
        point = gobangUser.getPoint();
        exp = gobangUser.getExp();
        winCount = gobangUser.getWinCount();
        loseCount = gobangUser.getLoseCount();
        drawCount = gobangUser.getDrawCount();
        key = bindKey;
    }

    public int draw(){
        return ++drawCount;
    }

    public int lose(){
        return ++loseCount;
    }

    public int win(){
        return ++winCount;
    }

    public int gainPoint(int gainedPoint){
        return point += gainedPoint;
    }

    public int gainExp(int gainedExp){
        exp += gainedExp;
        return getCurrantExp();
    }

    public SelectionKey getKey() {
        return key;
    }

    public int getCurrantExp(){
        return exp % 100;
    }

    public int getLevel(){
        return exp / 100 + 1;
    }

    public int getWinCount() {
        return winCount;
    }

    public int getUid() {
        return uid;
    }

    public int getPoint() {
        return point;
    }

    public int getLoseCount() {
        return loseCount;
    }

    public int getGid() {
        return gid;
    }

    public int getExp() {
        return exp;
    }

    public int getDrawCount() {
        return drawCount;
    }

    public String getUsername() {
        return username;
    }
}
