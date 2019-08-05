package org.mlw.birdie.engine;

import com.google.common.eventbus.EventBus;
import org.mlw.birdie.engine.event.support.GenericSubscriberExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientEventBroker {

    private static final Logger log = LoggerFactory.getLogger(ClientEventBroker.class);

    private int numberOfPlayers = 0;
    private final int numberOfSeats;
    private final EventBus server;
    private final EventBus[] clients;

    public ClientEventBroker(EventBus server, int numberOfSeats){
        this.server = server;
        this.numberOfSeats = numberOfSeats;
        this.clients = new EventBus[numberOfSeats];

        for(int i=0; i<numberOfSeats; i++){
            this.clients[i] = new EventBus(new GenericSubscriberExceptionHandler());
        }
    }

    public EventBus addPlayer(Object... listeners){
        log.info("Adding player: " + numberOfPlayers);
        EventBus eventBus = this.clients[numberOfPlayers++];
        if(listeners!=null) for(Object listener : listeners){
            eventBus.register(listener);
        }

        return eventBus;
    }

    public Object post(Object event){
        for(EventBus eventBus: clients){
            eventBus.post(event);
        }
        return event;
    }

    public void post(Object event, Integer... seats){
        if( seats != null){
            for(Integer seat : seats) {
                if( seat >=0 && seat < clients.length && clients[seat.intValue()] != null) {
                    clients[seat.intValue()].post(event);
                }
            }
        } else {
            post(event);
        }
    }

    public Object postToServer(Object event){
        if( server != null && event != null) {
            server.post(event);
        }
        return event;
    }

    public int getNumberOfSeats() { return numberOfSeats; }
}