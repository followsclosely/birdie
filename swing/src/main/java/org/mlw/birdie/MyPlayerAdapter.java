package org.mlw.birdie;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.mlw.birdie.engine.AbstractPlayerAdapter;
import org.mlw.birdie.engine.event.BidEvent;
import org.mlw.birdie.engine.event.BidRequestEvent;

public class MyPlayerAdapter extends AbstractPlayerAdapter {

    public MyPlayerAdapter(EventBus server, String name, int seat) {
        super(server, name, seat);
    }

    @Subscribe
    public void onBidRequestEvent(BidRequestEvent event) {
        post(new BidEvent(this, new Bid(seat, 130)));
    }
}