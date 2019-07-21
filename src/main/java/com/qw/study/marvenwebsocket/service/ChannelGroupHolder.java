package com.qw.study.marvenwebsocket.service;

import cn.hutool.json.JSONUtil;
import com.qw.study.marvenwebsocket.beans.GamePlayer;
import com.qw.study.marvenwebsocket.beans.GameRoom;
import com.qw.study.marvenwebsocket.exceptions.BusinessExceptions;
import com.qw.study.marvenwebsocket.utils.SerializeUtils;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author : qw.r
 * @since : 19-6-27 22:55
 */
@Service
public class ChannelGroupHolder {

    private static final Logger logger = LoggerFactory.getLogger(ChannelGroupHolder.class);

    private Map<String, ChannelGroup> roomMap = new HashMap<>();

    private Map<String, ChannelInfo> channelInfoMap = new HashMap<>();

    public Map<String, ChannelGroup> loadAll() {
        return roomMap;
    }

    public ChannelGroup load(String groupId) {
        return roomMap.get(groupId);
    }

    public void createGroup(String groupId) {
        ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        roomMap.put(groupId, group);
    }


    public void addToGroup(String groupId, Channel channel) {
        if (roomMap.get(groupId) == null) {
            roomMap.put(groupId, new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
        }
        ChannelGroup group = roomMap.get(groupId);
        if (group.contains(channel)) {
            logger.warn("channel:{} is already in group:{}", channel.id(), groupId);
            return;
        }
        /* create relation of channel and group */
        group.add(channel);
        ChannelInfo channelInfo = new ChannelInfo();
        channelInfo.setChannelGroup(group);
        channelInfo.setChannelId(channel.id().asLongText());
        channelInfo.setGamePlayer(new GamePlayer());
        channelInfoMap.put(channel.id().asLongText(), channelInfo);
    }

    public String getChannelGroup(Channel channel) {
        if (channel == null || channel.id() == null) {
            logger.error("channel data is null");
            throw new BusinessExceptions("channel can not be null");
        }
        String channelId = channel.id().asLongText();
        ChannelInfo channelInfo = channelInfoMap.get(channelId);
        if (channelInfo == null || channelInfo.getChannelId() == null || channelInfo.getChannelId().length() == 0) {
            throw new BusinessExceptions("channel:{} has no group info");
        }
        if (roomMap.get(channelInfo.getChannelId()) == null) {
            throw new BusinessExceptions("channel:{}, group:{} has no group");
        }
        return channelInfo.getChannelId();
    }

    public void delGroup(String groupId, WebSocketServerHandshaker handshaker) {

        if (roomMap.get(groupId) == null) {
            return;
        }
        ChannelGroup group = roomMap.get(groupId);
        group.forEach(new Consumer<Channel>() {
            @Override
            public void accept(Channel channel) {
                channelInfoMap.remove(channel.id().asLongText());
                handshaker.close(channel, new CloseWebSocketFrame());
            }
        });
    }

    private void sendMsg(String groupId, TextWebSocketFrame msgFrame) {
        if (roomMap.get(groupId) == null) {
            logger.warn("channel :{} is null", groupId);
            return;
        }
//        logger.info("start send msg:{}", SerializeUtils.toJson(msgFrame));
        ChannelGroup group = roomMap.get(groupId);
        group.forEach(new Consumer<Channel>() {
            @Override
            public void accept(Channel channel) {

                if (channel.isWritable()) {
                    logger.info("writable room map size:{}, group size:{}", roomMap.size(), group.size(), SerializeUtils.toJson(msgFrame));
                    channel.writeAndFlush(msgFrame).addListener(future -> {
                        if (!future.isSuccess()) {
                            logger.warn("unexpected push. msg:{} fail:{}", msgFrame, future.cause().getMessage());
                        } else {
                        }
                    });
                } else {
                    try {
//                        channel.writeAndFlush(msgFrame).sync();
                        logger.info("abandon msg remoteAddress:[{}], msg:[{}]", channel.remoteAddress(), msgFrame);
                    } catch (Exception e) {
                        logger.info("abandon msg exception. msg:[{}]", msgFrame, e);
                    }
                }


            }
        });
//        logger.info("finish send msg:{}", SerializeUtils.toJson(msgFrame));

    }

    public void sendMsg(String groupId, GameRoom msg, String msgType) {

        /* format msg */
        Map<String, Object> nettyMsg = new HashMap<>();
        nettyMsg.put("msg_info", msg);
        nettyMsg.put("msg_type", msgType);
        TextWebSocketFrame msgFrame = null;
        try {
            msgFrame = msg.getTextFrame();
            if(msgFrame == null) {
                msgFrame = new TextWebSocketFrame(SerializeUtils.toJson(nettyMsg));

            } else {
                msgFrame.touch(SerializeUtils.toJson(nettyMsg));
            }

        } catch (Exception e) {
            msgFrame = new TextWebSocketFrame(SerializeUtils.toJson(nettyMsg));
            logger.error("get room msg object exceptions", e);
        }

        /* send by room */
        sendMsg(groupId, msgFrame);
    }


    class ChannelInfo {

        private String channelId;
        private ChannelGroup channelGroup;
        private GamePlayer gamePlayer;

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public ChannelGroup getChannelGroup() {
            return channelGroup;
        }

        public void setChannelGroup(ChannelGroup channelGroup) {
            this.channelGroup = channelGroup;
        }

        public GamePlayer getGamePlayer() {
            return gamePlayer;
        }

        public void setGamePlayer(GamePlayer gamePlayer) {
            this.gamePlayer = gamePlayer;
        }
    }


}
