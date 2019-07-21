package com.qw.study.marvenwebsocket.app;

import com.qw.study.marvenwebsocket.common.Global;
import com.qw.study.marvenwebsocket.service.ChannelGroupHolder;
import com.qw.study.marvenwebsocket.service.GameRoomService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author : qw.r
 * @since : 19-1-11 23:50
 */
@Component
public class ScheduleNotificationTask {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleNotificationTask.class);

    @Autowired
    private ChannelGroupHolder channelGroupHolder;

    @Autowired
    private GameRoomService gameRoomService;

//    @Scheduled(cron = "* * * * * *")
//    public void start() {
//
//
//        Global.group.forEach(new Consumer<Channel>() {
//            @Override
//            public void accept(Channel channel) {
//                ChannelId channelId = channel.id();
//                logger.info("channelId:{} ",channelId.asLongText());
//                if(channel.isActive()) {
//                    logger.info("sending data");
//
//                    TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString() + "msg from server" + "：");
//
//                    channel.writeAndFlush(tws);
//                }
//            }
//        });
//
//    }


    //    @Scheduled(cron = "* * * * * *")
//    public void groupMsg() {
//
//        Map<String, ChannelGroup> roomMap = channelGroupHolder.loadAll();
//        if(roomMap.size() == 0) {
//            logger.info("no group find");
//        }
//        logger.info("group size : {}", roomMap.size());
//        roomMap.forEach(new BiConsumer<String, ChannelGroup>() {
//            @Override
//            public void accept(String groupId, ChannelGroup channels) {
//
//                channels.forEach(new Consumer<Channel>() {
//                    @Override
//                    public void accept(Channel channel) {
//                        ChannelId channelId = channel.id();
//                        logger.info("group id:{}, channelId:{} ", groupId, channelId.asLongText());
//                        if(channel.isActive()) {
//                            logger.info("sending data");
//                            TextWebSocketFrame tws = new TextWebSocketFrame(groupId + ":" + channel.id());
//                            channel.writeAndFlush(tws);
//                        }
//
//                    }
//                });
//
//            }
//        });
//
//    }
//}
//    @Scheduled(cron = "0/5 * * * * *")
    public void groupMsg() {

        Map<String, ChannelGroup> roomMap = channelGroupHolder.loadAll();
        if (roomMap.size() == 0) {
            logger.info("no group find");
        }
        logger.info("group size : {}", roomMap.size());
//        gameRoomService.sendToAllPlayer(roomMap.size());

    }

}