package org.mlw.birdie.console;

import com.google.common.eventbus.EventBus;
import org.mlw.birdie.engine.event.GenericSubscriberExceptionHandler;
import org.mlw.birdie.engine.RookEngineBuilder;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        //System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");

        EventBus serverBus = new EventBus(new GenericSubscriberExceptionHandler());

        new RookEngineBuilder()
                .seats(4)
                .server(serverBus)
                .player(new ConsolePlayerAdapter(serverBus))
                .build().run();

        Thread.currentThread().join();
    }
}