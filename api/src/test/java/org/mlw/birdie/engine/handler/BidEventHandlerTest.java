package org.mlw.birdie.engine.handler;

import com.google.common.eventbus.EventBus;
import org.junit.Assert;
import org.junit.Test;
import org.mlw.birdie.Bid;
import org.mlw.birdie.engine.ClientEventBroker;
import org.mlw.birdie.engine.DefaultGameContext;
import org.mlw.birdie.engine.event.BidEvent;

public class BidEventHandlerTest {

    private EventBus eventBus = new EventBus("test");

    @Test
    public void testSimple() {
        DefaultGameContext context = new DefaultGameContext(4);
        ClientEventBroker clients = new ClientEventBroker(context.getNumberOfPlayers());
        context.newHand();

        BidEventHandler handler = new BidEventHandler(clients, context);

        handler.onBidEvent(new BidEvent(null, new Bid(1, 85)));
        handler.onBidEvent(new BidEvent(null, new Bid(2, null)));
        handler.onBidEvent(new BidEvent(null, new Bid(3, null)));

        handler.onBidEvent(new BidEvent(null, new Bid(0, 130)));
        handler.onBidEvent(new BidEvent(null, new Bid(1, null)));

        Bid b = context.getHand().getMaxBid();
        Assert.assertEquals(0, b.getSeat());
        Assert.assertEquals(new Integer(130), b.getValue());
    }

    @Test
    public void testOutOfOrderBid() {
        DefaultGameContext context = new DefaultGameContext(4);
        ClientEventBroker clients = new ClientEventBroker(context.getNumberOfPlayers());
        context.newHand();

        BidEventHandler handler = new BidEventHandler(clients, context);

        handler.onBidEvent(new BidEvent(null, new Bid(1, 80)));
        handler.onBidEvent(new BidEvent(null, new Bid(2, 150)));
        handler.onBidEvent(new BidEvent(null, new Bid(1, 130)));

        Bid b = context.getHand().getMaxBid();
        Assert.assertEquals(2, b.getSeat());
        Assert.assertEquals(new Integer(150), b.getValue());
    }

    @Test
    public void testTooLowBid() {
        DefaultGameContext context = new DefaultGameContext(4);
        ClientEventBroker clients = new ClientEventBroker(context.getNumberOfPlayers());
        context.newHand();

        BidEventHandler handler = new BidEventHandler(clients, context);

        handler.onBidEvent(new BidEvent(null, new Bid(1, 90)));
        handler.onBidEvent(new BidEvent(null, new Bid(2, 115)));
        handler.onBidEvent(new BidEvent(null, new Bid(3, 115)));

        Bid b = context.getHand().getMaxBid();
        Assert.assertEquals(2, b.getSeat());
        Assert.assertEquals(new Integer(115), b.getValue());
    }
}