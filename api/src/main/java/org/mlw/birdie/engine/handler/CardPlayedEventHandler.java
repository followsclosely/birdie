package org.mlw.birdie.engine.handler;

import org.mlw.birdie.Card;
import org.mlw.birdie.GameContext;
import org.mlw.birdie.Hand;
import org.mlw.birdie.Trick;
import org.mlw.birdie.engine.ClientEventBroker;
import org.mlw.birdie.engine.event.CardPlayedEvent;
import org.mlw.birdie.engine.event.TrickWonEvent;
import org.mlw.birdie.engine.event.TurnEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CardPlayedEventHandler {

    private static final Logger log = LoggerFactory.getLogger(CardPlayedEventHandler.class);

    private final GameContext context;
    private final ClientEventBroker clients;

    public CardPlayedEventHandler(ClientEventBroker clients, GameContext context) {
        this.context = context;
        this.clients = clients;
    }

    public void onCardPlayedEvent(CardPlayedEvent event){
        log.info(String.format("Player%d played the %s", event.getSeat(), event.getCard()));

        Hand hand = context.getHand();

        Trick trick = hand.getTrick();

        if( trick.getSeat() != event.getSeat() ){
            log.info(String.format("It is not your turn player%d!", event.getSeat()));
        }

        Card card = event.getCard();
        if( context.getHand().getCards(event.getSeat()).indexOf(card) == -1) {
            log.info(String.format("Player%d does not have %s in his hand.", event.getSeat(), event.getCard()));
            return;
        }

        //If no cards have yet to be played...
        if( trick.getCards().isEmpty() ){
            context.getHand().getCards(event.getSeat()).remove(card);
            trick.getCards().add(card);
            clients.post(new TurnEvent(this, trick, trick.getSeat()), trick.getSeat());
            return;
        }
        else {
            Card lead = trick.getCards().get(0);
            Card.Suit trump = hand.getTrump();

            //If player did not follow suite, check if play is valid...
            if( !lead.getSuit().equals(card.getSuit()) ){
                long count = context.getHand().getCards(event.getSeat()).stream().filter(c -> c.getSuit().equals(lead.getSuit())).count();
                if( count > 0 ){
                    log.info(String.format("Player%d did not follow suit!", event.getSeat()));
                    //todo: re-prompt could cause an infinite loop!
                    clients.post(new TurnEvent(this, trick, trick.getSeat()), trick.getSeat());
                    return;
                }
            }

            context.getHand().getCards(event.getSeat()).remove(card);
            trick.getCards().add(card);

            //If the leader is up again, then end the hand.
            if (trick.getLeader() == trick.getSeat()) {

                //calculate winner
                int seat = determineWinnerOfTrick(hand);
                clients.post(new TrickWonEvent(this, trick));

                //If this is the end of the hand, then do end of hand processing.
                trick = hand.createTrick(seat);
            }

            clients.post(new TurnEvent(this, trick, trick.getSeat()), trick.getSeat());
        }
    }

    private int determineWinnerOfTrick(Hand hand){
        Trick trick = hand.getTrick();
        Card lead = trick.getCards().get(0);
        int leader = trick.getSeat();
        Card leadTrump = null;
        int winner = 0;

        for (int i = 1; i < context.getNumberOfPlayers(); i++) {
            Card card = trick.getCards().get(i);

            if( !hand.isTrump(lead) && hand.isTrump(card)){
                if( leadTrump == null || leadTrump.getRank() < card.getRank() ){
                    leadTrump = card;
                    trick.setWinner(winner = i);
                }
            }
            else if (leadTrump ==null && lead.getSuit().equals(card.getSuit()) && lead.getRank() < card.getRank()) {
                trick.setWinner(winner = i);
                lead = card;
            }
        }

        leader = (leader + winner) % context.getNumberOfPlayers();

        log.info(trick.getCards() + " Player" + leader + " won the trick with a " + trick.getCards().get(winner));
        return leader;
    }
}
