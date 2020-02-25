package com.lxc.game.gobang.server.object;

import com.lxc.game.gobang.server.handler.Judgment;

import java.util.Date;

public class ActiveGame {

    public static final int LEFT = 1;

    public static final int RIGHT = 2;

    public static final int EMPTY = 0;

    public static final int ILLEGAL = -1;
    public static final int DRAW = 3;

    private byte[][] board;
    private int totalStep;

    private final ActiveUser left;

    private final ActiveUser right;

    private int winner;

    private final Date startTime;

    public int step(){
        return ++totalStep;
    }

    public byte[][] getBoard() {
        return board;
    }

    public void setBoard(byte[][] board) {
        this.board = board;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public int getWinner() {
        return winner;
    }

    public Date getStartTime() {
        return startTime;
    }

    public ActiveUser getLeft() {
        return left;
    }

    public ActiveUser getRight() {
        return right;
    }

    private ActiveGame(){
        left = null;
        right = null;
        startTime = null;
    }

    public ActiveGame(ActiveUser left, ActiveUser right, Judgment judgment){
        judgment.setGame(this);
        left.setJudgment(judgment);
        right.setJudgment(judgment);
        this.left = left;
        this.right = right;
        this.startTime = new Date();
        int size = judgment.getBoardSize() + 2;
        this.board = new byte[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == 0 || i == size - 1 || j == 0 || j == size - 1) {
                    board[i][j] = ILLEGAL;
                }
            }
        }
    }

}
