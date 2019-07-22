package org.mlw.birdie.engine.handler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mlw.birdie.Card;
import org.mlw.birdie.MockPlayerAdapter;
import org.mlw.birdie.Trick;
import org.mlw.birdie.engine.ClientEventBroker;
import org.mlw.birdie.engine.DefaultGameContext;
import org.mlw.birdie.engine.event.CardPlayedEvent;
import org.mlw.birdie.engine.event.TrickWonEvent;
import org.mlw.birdie.engine.event.TurnEvent;

import static org.junit.Assert.assertEquals;
import static org.mlw.birdie.RookTestUtils.getCard;

public class CardPlayedEventHandlerTest {

    @Before
    public void setUp() throws Exception { }

    @After
    public void tearDown(){ }

    @Test
    public void testLeaderWinswWithHighCard() {

        DefaultGameContext context = new DefaultGameContext(4);
        ClientEventBroker clients = new ClientEventBroker(context.getNumberOfPlayers());
        context.newHand().setTrump(Card.Suit.Black);

        CardPlayedEventHandler handler = new CardPlayedEventHandler(clients, context);

        Trick trick = context.getHand().createTrick(0);

        handler.onCardPlayedEvent(new CardPlayedEvent(this, getCard(context, Card.Suit.Black,1, 0),0));
        handler.onCardPlayedEvent(new CardPlayedEvent(this, getCard(context, Card.Suit.Black,7, 1),1));
        handler.onCardPlayedEvent(new CardPlayedEvent(this, getCard(context, Card.Suit.Black,5, 2),2));
        handler.onCardPlayedEvent(new CardPlayedEvent(this, getCard(context, Card.Suit.Black,9, 3),3));

        assertEquals(0, trick.getWinner());
    }

    @Test
    public void testPlayer1WinsWithHighCard() {

        DefaultGameContext context = new DefaultGameContext(4);
        ClientEventBroker clients = new ClientEventBroker(context.getNumberOfPlayers());
        context.newHand().setTrump(Card.Suit.Red);

        CardPlayedEventHandler handler = new CardPlayedEventHandler(clients, context);

        Trick trick = context.getHand().createTrick(0);

        handler.onCardPlayedEvent(new CardPlayedEvent(this, getCard(context, Card.Suit.Black,13, 0),0));
        handler.onCardPlayedEvent(new CardPlayedEvent(this, getCard(context, Card.Suit.Black,14, 1),1));
        handler.onCardPlayedEvent(new CardPlayedEvent(this, getCard(context, Card.Suit.Yellow,1, 2),2));
        handler.onCardPlayedEvent(new CardPlayedEvent(this, getCard(context, Card.Suit.Black,9, 3),3));

        assertEquals(1, trick.getWinner());
    }

    @Test
    public void testPlayer2WinsWithHighTrumpCard() {

        DefaultGameContext context = new DefaultGameContext(4);
        ClientEventBroker clients = new ClientEventBroker(context.getNumberOfPlayers());
        MockPlayerAdapter[] players = new MockPlayerAdapter[context.getNumberOfPlayers()];
        for (int i=0; i<context.getNumberOfPlayers(); i++){
            players[i] = (MockPlayerAdapter)clients.addPlayer(new MockPlayerAdapter());
        }

        context.newHand().setTrump(Card.Suit.Red);

        CardPlayedEventHandler handler = new CardPlayedEventHandler(clients, context);

        Trick trick = context.getHand().createTrick(0);

        handler.onCardPlayedEvent(new CardPlayedEvent(this, getCard(context, Card.Suit.Black,13, 0),0));
        handler.onCardPlayedEvent(new CardPlayedEvent(this, getCard(context, Card.Suit.Black,14, 1),1));
        handler.onCardPlayedEvent(new CardPlayedEvent(this, getCard(context, Card.Suit.Red,2, 2),2));
        handler.onCardPlayedEvent(new CardPlayedEvent(this, getCard(context, Card.Suit.Green,1, 3),3));

        assertEquals(2, trick.getWinner());

        //Check that player 0 never got a turn event.
        assertEquals(0, players[0].getEvents(TurnEvent.class).size());

        //Check that player 1 got a turn event.
        assertEquals(1, players[1].getEvents(TurnEvent.class).size());

        //Check that the TrickWonEvent was sent.
        assertEquals(2, ((TrickWonEvent)players[0].getEvents(TrickWonEvent.class).get(0)).getTrick().getWinner());
    }

    @Test
    public void testPlayer1DoesNotFollowSuit() {


        DefaultGameContext context = new DefaultGameContext(4);
        ClientEventBroker clients = new ClientEventBroker(context.getNumberOfPlayers());
        context.newHand().setTrump(Card.Suit.Red);

        CardPlayedEventHandler handler = new CardPlayedEventHandler(clients, context);

        Trick trick = context.getHand().createTrick(0);

        handler.onCardPlayedEvent(new CardPlayedEvent(this, getCard(context, Card.Suit.Black,13, 0),0));
        //Add the black 14 to Player2 hand...
        getCard(context, Card.Suit.Black,14, 1);
        handler.onCardPlayedEvent(new CardPlayedEvent(this, getCard(context, Card.Suit.Red,14, 1),1));

        assertEquals("There should be just one card as Player1 did not follow suit.", 1, trick.getCards().size());
    }
}