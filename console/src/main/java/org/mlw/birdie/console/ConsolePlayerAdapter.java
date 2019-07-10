package org.mlw.birdie.console;

import org.mlw.birdie.engine.AbstractPlayerAdapter;
import org.mlw.birdie.engine.Card;
import org.mlw.birdie.engine.GameContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ConsolePlayerAdapter extends AbstractPlayerAdapter {

    public ConsolePlayerAdapter() throws IOException {
        System.out.println("Enter your Name: ");
        this.name = readline();
        if( this.name == null || this.name.trim().length() == 0){
            this.name = "Player 0";
        }
    }

    @Override
    public void handleDeal(GameContext context, List<Card> cards) {
        super.handleDeal(context, cards);

        System.out.println("Select Trump: ");
        for(Card.Suit suit : Card.Suit.values()){
            System.out.println("  " + suit.ordinal() + ": " + suit);
        }
        Card.Suit trump = Card.Suit.values()[Integer.parseInt(readline())];

        System.out.println( trump + " is Trump!");
    }

    @Override
    public Card.Suit handleCat(GameContext context, List<Card> cat) {
        return null;
    }

    private BufferedReader reader = null;
    private synchronized String readline() {
        if( reader==null ){
            reader = new BufferedReader(new InputStreamReader(System.in));
        }
        try {
            return reader.readLine();
        } catch (Exception ignore){
            return readline();
        }
    }
}