package org.mlw.birdie.console;

import org.mlw.birdie.engine.BasicPlayerAdapter;
import org.mlw.birdie.engine.DeckFactory;
import org.mlw.birdie.engine.RookEngine;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {


        RookEngine engine = new RookEngine();
        engine.setDeck(DeckFactory.getStandardDeck());
        engine.addPlayer(new ConsolePlayerAdapter());
        for(int i=1; i<4; i++) engine.addPlayer(new BasicPlayerAdapter(String.format("Player %d", i)));

        engine.startGame();
        engine.startHand();

//        for(int i=0, length=engine.getPlayer(0).getCards().size(); i<length; i++){
//            System.out.print("  "+ String.format("%02d", i) + "  ");
//        }
//
//        System.out.println();
//        System.out.println(engine.getPlayer(0).getCards());
//        System.out.println("Select a Card: ");
//
//        Card card = engine.getPlayer(0).getCards().get(Integer.parseInt(reader.readLine()));
//        System.out.println("You played the  " + card);
    }
}