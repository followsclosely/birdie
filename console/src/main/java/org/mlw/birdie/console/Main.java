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

        int numberOfPlayers = 4;

        RookEngine engine = new RookEngine(DeckFactory.getStandardDeck(), new ClientEventBroker(4));
        EventBus serverBus = new AsyncEventBus(Executors.newSingleThreadExecutor());
        serverBus.register(engine);

        engine.addPlayer(new ConsolePlayerAdapter(serverBus));
        for(int i=1; i<numberOfPlayers; i++) engine.addPlayer(new BasicPlayerAdapter(serverBus, String.format("Player %d", i), i));

        engine.startGame();

        Thread.currentThread().join();

//        for (int i=0; i<5; i++) {
//            engine.processDeal(context);
//            engine.processBidding(context);
//            //engine.processKitty(context);
//            //engine.processHand(context);
//        }
    }
}