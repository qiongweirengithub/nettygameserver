package com.qw.study.marvenwebsocket.service;

import cn.hutool.json.JSONUtil;
import com.qw.study.marvenwebsocket.beans.GamePlayer;
import com.qw.study.marvenwebsocket.common.Global;
import com.qw.study.marvenwebsocket.utils.SerializeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:MyWebSocketServerHandler Function: TODO ADD FUNCTION.
 *
 * @author hxy
 */
@Service
@ChannelHandler.Sharable
public class MyWebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(MyWebSocketServerHandler.class);

    @Autowired
    private ChannelGroupHolder channelGroupHolder;

    @Autowired
    private GameRoomService gameRoomService;

    private WebSocketServerHandshaker handshaker;

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {

        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
//            buf.retain();
            buf.release();
        }

        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }

    }


    /**
     * channel 通道 action 活跃的 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Global.group.add(ctx.channel());
        logger.info("connect active event: {}" + ctx.channel().remoteAddress().toString());
    }

    /**
     * channel 通道 Inactive 不活跃的 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端关闭了通信通道并且不可以传输数据
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 移除
        Global.group.remove(ctx.channel());
        logger.info("connect inactive event: {}" + ctx.channel().remoteAddress().toString());
    }

    /**
     * channel 通道 Read 读取 Complete 完成 在通道读取完成后会在这个方法里通知，对应可以做刷新操作 ctx.flush()
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * use to handle user status change
     * @param ctx
     * @param frame
     */
    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            logger.info("关闭链路的指令");
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }

        // 判断是否ping消息
        if (frame instanceof PingWebSocketFrame) {
            logger.info("ping消息");
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        // 本例程仅支持文本消息，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            logger.info("本例程仅支持文本消息，不支持二进制消息");
            throw new UnsupportedOperationException(
                    String.format("%s frame types not supported", frame.getClass().getName()));
        }

        // parse request data json
        String request = ((TextWebSocketFrame) frame).text();

        GamePlayer player = SerializeUtils.readObject(request, GamePlayer.class);

        logger.info("{} received {}", ctx.channel(), SerializeUtils.toJson(player));

        gameRoomService.updatePlayer(player.getRoomId(), player.getId(), player.getName(), ctx.channel(), player.getPosix(), player.getPosiy());

    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws IOException {

        // 如果HTTP解码失败，返回HHTP异常
        if (!req.decoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        //获取url后置参数
        HttpMethod method = req.method();
        String uri = req.uri();
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        Map<String, List<String>> parameters = queryStringDecoder.parameters();
        logger.info("request uro decode: {}" + JSONUtil.toJsonStr(parameters));


        if (method == HttpMethod.GET && "/webssss".equals(uri)) {
            //....处理
            ctx.attr(AttributeKey.valueOf("type")).set("anzhuo");
        } else if (method == HttpMethod.GET && "/websocket".equals(uri)) {
            //...处理
            ctx.attr(AttributeKey.valueOf("type")).set("live");
        }

//        ReferenceCountUtil.retain(req);

        // 构造握手响应返回，本机测试
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://" + req.headers().get(HttpHeaders.Names.HOST) + uri, null, false);

        handshaker = wsFactory.newHandshaker(req);

        if (handshaker == null) {

            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());

        } else {

            Channel channel = ctx.channel();
            String channelId = channel.id().asLongText();
            Map<String, String> paramMap = RequestParser.parse(req);
            logger.info("channel:{} handling shack:{}", channelId, JSONUtil.toJsonStr(paramMap));


            String roomId = paramMap.get("roomId");
            gameRoomService.createRoom(roomId);
            gameRoomService.joinRoom(roomId, paramMap.get("openid"), paramMap.get("playerName"), channel, "0", "0");

            handshaker.handshake(ctx.channel(), req);
        }
    }

    /**
     * exception 异常 Caught 抓住 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    /**
     * 接收客户端发送的消息 channel 通道 Read 读 简而言之就是从通道中读取数据，也就是服务端接收客户端发来的数据。但是这个数据在不进行解码时它是ByteBuf类型的
     */

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        /* 传统的HTTP接入 */
        if (msg instanceof FullHttpRequest) {
            logger.info("full http request :{}", msg.toString());
            handleHttpRequest(ctx, ((FullHttpRequest) msg));

            /*  WebSocket接入 */
        } else if (msg instanceof WebSocketFrame) {
//            logger.info("request webSocketFrame uri : {}" + handshaker.uri());
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);

        }
    }

}