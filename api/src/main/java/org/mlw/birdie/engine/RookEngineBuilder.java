package org.mlw.birdie.engine;

import com.google.common.eventbus.EventBus;
import org.mlw.birdie.Deck;
import org.mlw.birdie.DeckFactory;
import org.mlw.birdie.PlayerAdapter;
import org.mlw.birdie.ScoringStrategy;
import org.mlw.birdie.engine.ai.basic.BasicPlayerAdapter;
import org.mlw.birdie.engine.event.DealRequestEvent;
import org.mlw.birdie.engine.event.GameStartedEvent;
import org.mlw.birdie.engine.event.GenericSubscriberExceptionHandler;
import org.mlw.birdie.engine.handler.BidEventHandler;
import org.mlw.birdie.engine.handler.CardPlayedEventHandler;
import org.mlw.birdie.engine.handler.DealRequestEventHandler;
import org.mlw.birdie.engine.scoring.PartnerScoringStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RookEngineBuilder {

    private static final Logger log = LoggerFactory.getLogger(RookEngineBuilder.class);

    private Deck deck;
    private EventBus serverBus;
    private int numberOfSeats = 4;
    private ClientEventBroker clients;
    private List<PlayerAdapter> playerAdapterList = new ArrayList<>();
    private DefaultGameContext context;
    private ScoringStrategy scoringStrategy;

    public RookEngineBuilder deck(Deck deck) { this.deck = deck; return this;}
    public RookEngineBuilder server(EventBus serverBus) { this.serverBus = serverBus;  return this; }
    public RookEngineBuilder seats(int numberOfSeats) { this.numberOfSeats = numberOfSeats;  return this; }
    public RookEngineBuilder player(PlayerAdapter player){ playerAdapterList.add(player); return this; }
    public RookEngineBuilder context(DefaultGameContext context) {this.context = context; return this; }
    public RookEngineBuilder scoring(ScoringStrategy scoring) { this.scoringStrategy = scoring; return this; }

    public Runnable build(){
        if( this.deck == null ){
            log.info("No deck provided. Using DeckFactory.getStandardDeck.");
            this.deck = DeckFactory.getStandardDeck();
        }
        if( this.serverBus == null) {
            log.info("No server EventBus provided. Using generic EventBus.");
            this.serverBus = new EventBus(new GenericSubscriberExceptionHandler());
        }
        if( clients == null ){
            log.info("No ClientEventBroker provided. Creating new instance with "+numberOfSeats+" seats.");
            this.clients = new ClientEventBroker(serverBus, numberOfSeats);
        }
        if( scoringStrategy == null ){
            log.info("No ScoringStrategy provided. Creating new PartnerScoringStrategy.");
            this.scoringStrategy = new PartnerScoringStrategy();
        }

        log.info("Adding players...");
        for(PlayerAdapter player : playerAdapterList){
            log.info("  ...adding " + player.getName());
            this.clients.addPlayer(player);
        }
        for(int i=playerAdapterList.size(); i<numberOfSeats; i++){
            this.clients.addPlayer(new BasicPlayerAdapter(serverBus, String.format("Player %d", i), i));
        }

        log.info("Creating a new GameContext and registering EventHandlers.");
        this.context = new DefaultGameContext(clients.getNumberOfSeats());
        this.serverBus.register(new DealRequestEventHandler(clients, context, deck));
        this.serverBus.register(new CardPlayedEventHandler(clients, context, scoringStrategy));
        this.serverBus.register(new BidEventHandler(clients, context));

        return () -> {
            clients.post(new GameStartedEvent(clients, context));
            serverBus.post(new DealRequestEvent());
        };
    }
}