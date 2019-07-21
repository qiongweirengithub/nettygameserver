package com.qw.study.marvenwebsocket.beans;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class GameRoom {

    private String id;
    private GamePlayer playeri;
    private GamePlayer playerii;
    private TextWebSocketFrame textFrame;

    public TextWebSocketFrame getTextFrame() {
        return textFrame;
    }

    public void setTextFrame(TextWebSocketFrame textFrame) {
        this.textFrame = textFrame;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GamePlayer getPlayeri() {
        return playeri;
    }

    public void setPlayeri(GamePlayer playeri) {
        this.playeri = playeri;
    }

    public GamePlayer getPlayerii() {
        return playerii;
    }

    public void setPlayerii(GamePlayer playerii) {
        this.playerii = playerii;
    }
}
