package com.lxc.game.gobang.server.dao.pojo;

import javax.persistence.*;

@Entity
@Table(name = "user_gobang")
public class GobangUser {

    @Id
    @GeneratedValue
    private Integer gid;

    @OneToOne
    @JoinColumn(name = "uid", unique = true)
    private User base;

    @Column(name = "gobang_point")
    private int point;

    @Column(name = "gobang_exp")
    private int exp;

    @Column(name = "gobang_win")
    private int winCount;

    @Column(name = "gobang_lose")
    private int loseCount;

    @Column(name = "gobang_draw")
    private int drawCount;

    protected GobangUser(){

    }

    public GobangUser(User base){
        this.base = base;
    }

    public GobangUser(int gid) {
        this.gid = gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public int getDrawCount() {
        return drawCount;
    }

    public int getExp() {
        return exp;
    }

    public int getLoseCount() {
        return loseCount;
    }

    public int getPoint() {
        return point;
    }

    public int getWinCount() {
        return winCount;
    }

    public Integer getGid() {
        return gid;
    }

    public User getBase() {
        return base;
    }

    public void setBase(User base) {
        this.base = base;
    }

    public void setDrawCount(int gobangDraw) {
        this.drawCount = gobangDraw;
    }

    public void setExp(int gobangExp) {
        this.exp = gobangExp;
    }

    public void setLoseCount(int gobangLose) {
        this.loseCount = gobangLose;
    }

    public void setPoint(int gobangPoint) {
        this.point = gobangPoint;
    }

    public void setWinCount(int gobangWin) {
        this.winCount = gobangWin;
    }
}
