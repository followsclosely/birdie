package org.mlw.birdie.console;

import org.mlw.birdie.DeckFactory;
import org.mlw.birdie.engine.DefaultGameContext;
import org.mlw.birdie.engine.RookEngine;
import org.mlw.birdie.engine.ai.basic.BasicPlayerAdapter;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        RookEngine engine = new RookEngine(DeckFactory.getStandardDeck(), 4);
        engine.addPlayer(new RecursivePlayerAdapter(new ConsolePlayerAdapter()));
        for(int i=1; i<4; i++) engine.addPlayer(new BasicPlayerAdapter(String.format("Player %d", i), i));

        DefaultGameContext context = engine.createGame();

        for (int i=0; i<5; i++) {
            engine.processDeal(context);
            engine.processBidding(context);
            engine.processKitty(context);
            engine.processHand(context);
        }
    }
}