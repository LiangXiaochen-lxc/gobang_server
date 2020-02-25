package com.lxc.game.gobang.server.dao.pojo;

import com.lxc.game.gobang.server.object.ActiveGame;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "game_gobang")
public class GobangGame {

    @Id
    @GeneratedValue
    @Column(name = "gobang_uuid")
    private UUID gobangGameUUID;

    @ManyToOne
    @JoinColumn(name = "gobang_left")
    private GobangUser leftPlayer;

    @ManyToOne
    @JoinColumn(name = "gobang_right")
    private GobangUser rightPlayer;

    @Column(name = "gobang_winner")
    private int winner;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "gobang_begin")
    private Date startTime;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "gobang_end")
    private Date endTime;

    protected GobangGame(){

    }

    public GobangGame(ActiveGame game){
        leftPlayer = game.getLeft().toGobangUser();
        rightPlayer = game.getRight().toGobangUser();
        winner = game.getWinner();
        startTime = game.getStartTime();
        endTime = new Date();
    }

    public Date getEndTime() {
        return endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public GobangUser getLeftPlayer() {
        return leftPlayer;
    }

    public GobangUser getRightPlayer() {
        return rightPlayer;
    }

    public int getWinner() {
        return winner;
    }

    public UUID getGobangGameUUID() {
        return gobangGameUUID;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setGobangGameUUID(UUID gobangGameUUID) {
        this.gobangGameUUID = gobangGameUUID;
    }

    public void setLeftPlayer(GobangUser leftPlayer) {
        this.leftPlayer = leftPlayer;
    }

    public void setRightPlayer(GobangUser rightPlayer) {
        this.rightPlayer = rightPlayer;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }
}

