package org.mlw.birdie.engine;

import com.google.common.eventbus.EventBus;
import org.mlw.birdie.PlayerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientEventBroker {

    private static final Logger log = LoggerFactory.getLogger(ClientEventBroker.class);

    private int numberOfSeats;
    private EventBus server = null;
    private EventBus[] clients = null;
    public PlayerAdapter[] players = null;

    public ClientEventBroker(EventBus server, int numberOfSeats){
        this.server = server;
        this.numberOfSeats = numberOfSeats;
        this.clients = new EventBus[numberOfSeats];
        this.players = new PlayerAdapter[numberOfSeats];

        for(int i=0; i<numberOfSeats; i++){
            this.clients[i] = new EventBus("Player" + i);;
        }
    }

    public PlayerAdapter addPlayer(PlayerAdapter player){
        for(int i=0, length=players.length; i<length; i++){
            if( players[i] == null){
                log.info("Adding player: " + player.getName());
                players[i] = player;
                clients[i].register(player);
                break;
            }
        }
        return player;
    }

    public void post(Object event){
        for(EventBus eventBus: clients){
            eventBus.post(event);
        }
    }

    public void post(Object event, Integer... seats){
        if( seats != null){
            for(Integer seat : seats) {
                if( seat >=0 && seat < clients.length) {
                    clients[seat.intValue()].post(event);
                }
            }
        } else {
            post(event);
        }
    }

    public EventBus getServer() { return server; }

    public int getNumberOfSeats() { return numberOfSeats; }
    public String getName(int seat) { return players[seat].getName(); }
}