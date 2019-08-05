package org.mlw.birdie.engine;

import com.google.common.eventbus.EventBus;

public abstract class AbstractPlayerAdapter {

    private EventBus server;
    protected String name;
    protected int seat;


    public AbstractPlayerAdapter(EventBus server) {
        this.server = server;
    }
    public AbstractPlayerAdapter(EventBus server, String name, int seat) {
        this(server);
        this.name = name;
        this.seat = seat;
    }

    public void post(Object object){
        if( server != null && object != null) {
            server.post(object);
        }
    }

    public String getName() { return name; }
    public int getSeat() {
        return seat;
    }
}
