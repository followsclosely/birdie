package org.mlw.birdie.console;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.mlw.birdie.DeckFactory;
import org.mlw.birdie.engine.DefaultGameContext;
import org.mlw.birdie.engine.RookEngine;
import org.mlw.birdie.engine.ai.basic.BasicPlayerAdapter;

import java.io.IOException;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        int numberOfPlayers = 4;
        RookEngine engine = new RookEngine(DeckFactory.getStandardDeck(), numberOfPlayers);
        engine.addPlayer(new ConsolePlayerAdapter(engine.getEventBus()));
        for(int i=1; i<numberOfPlayers; i++) engine.addPlayer(new BasicPlayerAdapter(engine.getEventBus(), String.format("Player %d", i), i));

        engine.createGame();
        engine.startGame();

        Thread.currentThread().join();

        System.out.println("BUMMER!");

//        for (int i=0; i<5; i++) {
//            engine.processDeal(context);
//            engine.processBidding(context);
//            //engine.processKitty(context);
//            //engine.processHand(context);
//        }
    }
}