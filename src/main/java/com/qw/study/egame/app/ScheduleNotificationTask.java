package com.qw.study.egame.app;

import com.qw.study.egame.service.ChannelGroupHolder;
import com.qw.study.egame.service.GameRoomService;
import io.netty.channel.group.ChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

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