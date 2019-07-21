package com.qw.study.marvenwebsocket.service;

import com.qw.study.marvenwebsocket.beans.GamePlayer;
import com.qw.study.marvenwebsocket.beans.GameRoom;
import com.qw.study.marvenwebsocket.common.GameConstants;
import com.qw.study.marvenwebsocket.exceptions.BusinessExceptions;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author : qw.r
 * @since : 19-7-13 23:19
 */
@Service
public class GameRoomService {

    private static final Logger logger = LoggerFactory.getLogger(GameRoomService.class);

    /**
     * key : group
     */
    private Map<String, GameRoom> gameRomeHolderMap;

    @Autowired
    private ChannelGroupHolder channelGroupHolder;


    public void createRoom(String roomeId) {
        /* todo lock */
        if(gameRomeHolderMap == null) {
            gameRomeHolderMap = new HashMap<>();
        }
        GameRoom gameRoom = new GameRoom();
        gameRoom.setId(roomeId);
        gameRomeHolderMap.put(roomeId, gameRoom);
    }

    public void joinRoom(String romeId, String playerId, String playerName, Channel channel, String posix, String posiy) {


        GameRoom room = gameRomeHolderMap.get(romeId);
        if(room == null) {
            String msg = String.format("group : %s has no game room, pls create first", romeId);
            throw new BusinessExceptions(msg);
        }

        GamePlayer player = new GamePlayer();
        player.setChannel(channel);
        player.setId(playerId);
        player.setName(playerName);
        player.setRoomId(romeId);
        player.setPosix(posix);
        player.setPosiy(posiy);

        /* join room */
        if(room.getPlayeri() == null) {
            room.setPlayeri(player);
        } else if(room.getPlayerii() == null) {
            room.setPlayerii(player);
        } else {
            String msg = String.format("group : %s  game room is full, can not join", romeId);
            throw new BusinessExceptions(msg);
        }

        /* create relations */
        channelGroupHolder.addToGroup(romeId, channel);

    }


    public void updatePlayer(String group, String playerId, String playerName, Channel channel, String posix, String posiy) {


        GameRoom room = gameRomeHolderMap.get(group);
        if(room == null) {
            String msg = String.format("group : %s has no game room, pls create first", group);
            throw new BusinessExceptions(msg);
        }

        GamePlayer player = new GamePlayer();
        player.setChannel(channel);
        player.setId(playerId);
        player.setName(playerName);
        player.setRoomId(group);
        player.setPosix(posix);
        player.setPosiy(posiy);
        room.setPlayeri(player);

        /* join room */
        if(room.getPlayeri() != null && playerId.equals(room.getPlayeri().getId())) {
            room.setPlayeri(player);
        } else if(room.getPlayerii() != null && playerId.equals(room.getPlayerii().getId())) {
            room.setPlayerii(player);
        } else {
            String msg = String.format("group : %s  game room is full, can not join", group);
            throw new BusinessExceptions(msg);
        }

        /* send msg to relate client */
        channelGroupHolder.sendMsg(room.getId(), room, GameConstants.GameMsgType.ROOM_UPDATE);

    }



    public void sendToAllPlayer(GameRoom msg) {
        if(gameRomeHolderMap == null) {
            logger.info("no room find");
            return;
        }
        /* send msg to relate client */
        gameRomeHolderMap.forEach(new BiConsumer<String, GameRoom>() {
            @Override
            public void accept(String s, GameRoom room) {
                channelGroupHolder.sendMsg(room.getId(), msg, GameConstants.GameMsgType.HEART_BEAT);
            }
        });

    }

}
