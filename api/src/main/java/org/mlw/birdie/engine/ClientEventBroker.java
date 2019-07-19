package org.mlw.birdie.engine;

import com.google.common.eventbus.EventBus;
import org.mlw.birdie.PlayerAdapter;


public class ClientEventBroker {

    private int numberOfSeats;
    private EventBus[] clients = null;
    public PlayerAdapter[] players = null;

    public ClientEventBroker(int numberOfSeats){
        this.numberOfSeats = numberOfSeats;
        this.clients = new EventBus[numberOfSeats];
        this.players = new PlayerAdapter[numberOfSeats];

        for(int i=0; i<numberOfSeats; i++){
            this.clients[i] = new EventBus("Player" + i);;
        }
    }

    public void addPlayer(PlayerAdapter player){
        for(int i=0, length=players.length; i<length; i++){
            if( players[i] == null){
                System.out.println("Adding player: " + player.getName());
                players[i] = player;
                clients[i].register(player);
                return;
            }
        }
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

    public int getNumberOfSeats() { return numberOfSeats; }
    public String getName(int seat) { return players[seat].getName(); }
}