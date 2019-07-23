package org.mlw.birdie;

import org.mlw.birdie.engine.handler.CardPlayedEventHandler;
import org.mlw.birdie.engine.handler.CardPlayedEventHandlerTest;

public class RookTestUtils {

    private static final Deck deck = DeckFactory.getStandardDeck();

    public static Deck getDeck(){
        return deck;
    }

    public static Card getCard(String card){
        return getCard(null, Card.Suit.instance(card.substring(0,1),Card.Suit.Black), Integer.parseInt(card.substring(1)), null);

    }
    public static Card getCard(GameContext context, Card.Suit suit, int number, Integer addToHand){
        Card card = deck.getCards().stream().filter(c -> c.getSuit().equals(suit)).filter(c -> c.getNumber() == number).findFirst().get();
        if( context != null && addToHand != null){
            context.getHand().getCards(addToHand).add(card);
        }
        return card;
    }

    public static class HandBuilder {
        private Hand hand = null;
        public HandBuilder(int seats, int dealerIndex){
            hand = new Hand(seats, dealerIndex);
        }

        public HandBuilder hand(int leader, int winner, String... cards){
            Trick trick = hand.createTrick(leader);
            trick.setWinner(winner);
            for(String c : cards){
                trick.getCards().add(getCard(c));
            }

            return this;
        }

        public Hand build(){
            return hand;
        }
    }
}
