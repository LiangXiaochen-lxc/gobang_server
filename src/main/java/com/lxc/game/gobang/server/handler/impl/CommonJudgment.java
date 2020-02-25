package com.lxc.game.gobang.server.handler.impl;

import com.lxc.game.gobang.server.constant.Const;
import com.lxc.game.gobang.server.handler.Judgment;
import com.lxc.game.gobang.server.object.ActiveGame;
import com.lxc.game.gobang.server.object.ActiveUser;

public class CommonJudgment implements Judgment {

    private ActiveGame game;

    private ActiveUser now;

    @Override
    public int getBoardSize() {
        return Const.BOARD_SIZE;
    }

    @Override
    public int step(ActiveUser player, int x, int y) {

        byte[][] board = game.getBoard();
        byte step;

        if (board[x][y] != ActiveGame.EMPTY || !player.equals(now)){
            return ActiveGame.ILLEGAL;
        }

        if (player.equals(game.getRight())){
            step = ActiveGame.RIGHT;
        } else if (player.equals(game.getLeft())){
            step = ActiveGame.LEFT;
        } else {
            return ActiveGame.ILLEGAL;
        }

        board[x][y] = step;
        int length = -1;
        int[][][] loop = new int[][][]{
                {{1, 0}, {-1, 0}},
                {{0, 1}, {0, -1}},
                {{1, 1}, {-1, -1}},
                {{1, -1}, {-1, 1}}
        };

        for (int i = 0; i < 4; i++) {
            for (int l = 0; l < 2; l++) {
                for (int j = x, k = y; board[j][k] != ActiveGame.ILLEGAL; j+=loop[i][l][0], k+=loop[i][l][1]) {
                    if (board[j][k] == step){
                        length++;
                    } else {
                        break;
                    }
                }
            }
            if (length >= 5){
                gameEnd();
                return step;
            } else {
                length = -1;
            }
        }

        if (game.step() >= board.length * board.length * 0.75){
            gameEnd();
            return ActiveGame.DRAW;
        }

        now = getOpponent(player);
        return ActiveGame.EMPTY;
    }

    @Override
    public void surrender(ActiveUser player) {
        if (player.equals(game.getLeft())){
            game.setWinner(ActiveGame.RIGHT);
        } else if (player.equals(game.getRight())){
            game.setWinner(ActiveGame.LEFT);
        }
        gameEnd();
    }

    @Override
    public void setGame(ActiveGame game) {
        this.game = game;
    }

    @Override
    public ActiveGame getGame() {
        return game;
    }

    @Override
    public ActiveUser getOpponent(ActiveUser user) {
        if (user.equals(game.getLeft())){
            return game.getRight();
        } else {
            return game.getLeft();
        }
    }

    private void gameEnd(){
        game.getRight().setJudgment(null);
        game.getLeft().setJudgment(null);
    }

    public CommonJudgment(ActiveUser first){
        this.now = first;
    }

}
