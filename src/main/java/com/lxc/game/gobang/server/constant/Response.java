package com.lxc.game.gobang.server.constant;

public enum Response {

    MatchedFirst("mf"),

    MatchedSecond("ms"),

    Win("w"),
    SurrenderWin("sw"),

    Lose("l"),

    Draw("d"),

    WrongAuth("e"),

    ExistUsername("eu"),

    LoginSuccess("ls"),

    Logout("lo");

    private String response;

    Response(String response){
        this.response = response;
    }

    public byte[] toBytes(){
        return response.getBytes();
    }

    @Override
    public String toString() {
        return response;
    }

}
