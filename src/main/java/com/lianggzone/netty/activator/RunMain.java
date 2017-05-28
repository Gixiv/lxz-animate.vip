package com.lianggzone.netty.activator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.lianggzone.netty.server.WebSocketChatServer;

/**
 * @author 卢锡仲
 * @since 0.1
 */
@SpringBootApplication
@ComponentScan("com.lianggzone.netty")
public class RunMain implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(RunMain.class);

    @Autowired
    private WebSocketChatServer webSocketChatServer;
    
    public static void main(String[] args) {
        SpringApplication.run(RunMain.class, args);
    }
    
    public void run(String... strings) throws Exception {
        try {
        	webSocketChatServer.start();
            Thread.currentThread().join();
        } catch (Exception e) {
            logger.error("startup error!", e);
        }
    }
}