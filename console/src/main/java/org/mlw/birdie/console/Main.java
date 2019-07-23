package org.mlw.birdie.console;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.mlw.birdie.DeckFactory;
import org.mlw.birdie.engine.ClientEventBroker;
import org.mlw.birdie.engine.RookEngine;
import org.mlw.birdie.engine.ai.basic.BasicPlayerAdapter;

import java.io.IOException;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        //System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        System.setProperty("java.util.logging.ConsoleHandler.level", "DEBUG");

        int numberOfPlayers = 4;

        EventBus serverBus = new AsyncEventBus(Executors.newSingleThreadExecutor());
        RookEngine engine = new RookEngine(DeckFactory.getStandardDeck(), new ClientEventBroker(serverBus,4));
        serverBus.register(engine);

        engine.addPlayer(new ConsolePlayerAdapter(serverBus));
        for(int i=1; i<numberOfPlayers; i++) engine.addPlayer(new BasicPlayerAdapter(serverBus, String.format("Player %d", i), i));

        engine.startGame();

        Thread.currentThread().join();
    }
}