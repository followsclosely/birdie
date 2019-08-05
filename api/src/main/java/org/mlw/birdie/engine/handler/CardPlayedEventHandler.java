package org.mlw.birdie.engine.handler;

import com.google.common.eventbus.Subscribe;
import org.mlw.birdie.*;
import org.mlw.birdie.engine.ClientEventBroker;
import org.mlw.birdie.engine.DefaultGameContext;
import org.mlw.birdie.engine.event.*;
import org.mlw.birdie.engine.scoring.PartnerScoringStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CardPlayedEventHandler {

    private static final Logger log = LoggerFactory.getLogger(CardPlayedEventHandler.class);

    private final DefaultGameContext context;
    private final ClientEventBroker clients;
    private final ScoringStrategy scoringStrategy;

    public CardPlayedEventHandler(ClientEventBroker clients, DefaultGameContext context){
        this(clients, context, new PartnerScoringStrategy());
    }
    public CardPlayedEventHandler(ClientEventBroker clients, DefaultGameContext context, ScoringStrategy scoringStrategy) {
        this.clients = clients;
        this.context = context;
        this.scoringStrategy = scoringStrategy;
    }

    @Subscribe
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
            //Send this CardPlayedEvent to all the clients...
            clients.post(event);
            clients.post(new TurnEvent(this, trick, trick.getSeat()), trick.getSeat());
            return;
        }
        else {
            Card lead = trick.getCards().get(0);
            Card.Suit trump = hand.getTrump();

            //If player did not follow suite, check if play is valid...
            if( !lead.getSuit().equals(card.getSuit()) ){
                long count = context.getHand().getCards(event.getSeat()).stream().filter(c -> lead.getSuit().equals(c.getSuit())).count();
                if( count > 0 ){
                    log.info(String.format("Player%d did not follow suit!", event.getSeat()));
                    //todo: re-prompt could cause an infinite loop!
                    clients.post(new TurnEvent(this, trick, trick.getSeat()), trick.getSeat());
                    return;
                }
            }

            context.getHand().getCards(event.getSeat()).remove(card);
            trick.getCards().add(card);
            //Send this CardPlayedEvent to all the clients...
            clients.post(event);

            //If the leader is up again, then end the hand.
            if (trick.getLeader() == trick.getSeat()) {

                //calculate winner
                int seat = determineWinnerOfTrick(hand);
                clients.post(new TrickWonEvent(this, trick));

                if ( context.getHand().getCards(seat).size() > 0 ) {
                    //If this is the end of the hand, then do end of hand processing.
                    trick = hand.createTrick(seat);
                } else {
                    determineWinnerOfHand(hand);
                    //todo: add up the score!
                    clients.postToServer(new DealRequestEvent());
                    return;
                }
            }

            clients.post(new TurnEvent(this, trick, trick.getSeat()), trick.getSeat());
        }
    }

    protected int determineWinnerOfTrick(Hand hand){
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

    protected boolean determineWinnerOfHand(Hand hand){

        //Print a summary of all the tricks.
        for(Trick trick : hand.getTricks()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Player").append(trick.getLeader()).append(" ");
            for(int i=0; i<context.getNumberOfPlayers(); i++){
                int index = (i+trick.getLeader()) % context.getNumberOfPlayers();
                char x = ( trick.getWinner() == index ? '*' : ' ');
                sb.append( "|" + x + trick.getCards().get(index) + x);
            }
            sb.append("| Player").append( ((trick.getLeader() + trick.getWinner()) % context.getNumberOfPlayers()));
            sb.append(" won " + trick.getPoints() + " points");
            log.info(sb.toString());
        }

        int[] scores = hand.getScores();

        int total=0;
        for(int i=0; i<context.getNumberOfPlayers(); i++){
            total+=scores[i];
            log.info("Player" + i + ": " + scores[i] + " points.");
        }

        log.info("Total:    " + total + " points.");

        int[] teams = scoringStrategy.calculate(hand);

        Bid bid = hand.getMaxBid();
        boolean bidMade = teams[bid.getSeat()] >= bid.getValue();
        if( bidMade ) {
            log.info("Player" + bid.getSeat() + " made the bid!");
        } else {
            log.info("Player" + bid.getSeat() + " FAILED to make the bid!");
        }

        return bidMade;
    }
}
