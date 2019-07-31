package org.mlw.birdie.engine;

import com.google.common.eventbus.EventBus;
import org.mlw.birdie.engine.event.support.GenericSubscriberExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class ClientEventBroker {

    private static final Logger log = LoggerFactory.getLogger(ClientEventBroker.class);

    private int numberOfSeats;
    private EventBus server = null;
    private EventBus[] clients = null;
    public AbstractPlayerAdapter[] players = null;

    public ClientEventBroker(EventBus server, int numberOfSeats){
        this.server = server;
        this.numberOfSeats = numberOfSeats;
        this.clients = new EventBus[numberOfSeats];
        this.players = new AbstractPlayerAdapter[numberOfSeats];

        for(int i=0; i<numberOfSeats; i++){
            this.clients[i] = new EventBus(new GenericSubscriberExceptionHandler());
        }
    }

    public AbstractPlayerAdapter addPlayer(AbstractPlayerAdapter player){
        return addPlayer(player, null);
    }
    public AbstractPlayerAdapter addPlayer(AbstractPlayerAdapter player, List<Object> listeners){
        for(int i=0, length=players.length; i<length; i++){
            if( players[i] == null){
                log.info("Adding player: " + player.getName());
                players[i] = player;

                if(listeners!=null) for(Object listener : listeners){
                    clients[i].register(listener);
                }

                clients[i].register(player);
                break;
            }
        }
        return player;
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

    //public EventBus getServer() { return server; }
    public Object postToServer(Object event){
        if( server != null && event != null) {
            server.post(event);
        }
        return event;

    }

    public int getNumberOfSeats() { return numberOfSeats; }
    public String getName(int seat) { return players[seat].getName(); }
}