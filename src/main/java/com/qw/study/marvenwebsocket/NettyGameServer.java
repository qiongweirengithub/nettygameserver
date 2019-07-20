package com.qw.study.marvenwebsocket;

import com.qw.study.marvenwebsocket.service.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableScheduling
public class NettyGameServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyGameServer.class);

    @Autowired
    private NettyServer nettyServer;

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(NettyGameServer.class, args);
	}

    @PostConstruct
    public void init() {
        ExecutorService executorService  = Executors.newFixedThreadPool(10);
        executorService.submit(
        new Runnable() {
            @Override
            public void run() {
                try {
                    nettyServer.start(8081);
                } catch (InterruptedException e) {
                    logger.error("", e);
                }
            }
        });
    }


}
