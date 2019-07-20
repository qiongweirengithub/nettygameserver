package com.qw.study.marvenwebsocket.beans;


import cn.hutool.json.JSONUtil;
import io.netty.channel.Channel;

/**
 * @author : qw.r
 * @since : 19-7-13 23:22
 */
public class GamePlayer {

    private String name;
    private String id;
    private Channel channel;
    private String roomId;
    private String posix;
    private String posiy;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getPosix() {
        return posix;
    }

    public void setPosix(String posix) {
        this.posix = posix;
    }

    public String getPosiy() {
        return posiy;
    }

    public void setPosiy(String posiy) {
        this.posiy = posiy;
    }


    public static void main(String[] args) {
        GamePlayer player = new GamePlayer();
        player.setRoomId("504");
        player.setName("david");
        player.setRoomId("davidopenid");
        player.setPosix("0");
        player.setPosiy("0");
        System.out.println(JSONUtil.toJsonStr(player));
    }
}
