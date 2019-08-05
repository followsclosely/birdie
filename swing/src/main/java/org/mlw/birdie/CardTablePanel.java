package org.mlw.birdie;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import org.mlw.birdie.engine.client.PlayerAdapter;
import org.mlw.birdie.engine.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CardTablePanel extends JPanel implements PlayerAdapter {

    private static final Logger log = LoggerFactory.getLogger(CardTablePanel.class);

    private int mySeat = 0;

    private CardImages images;
    private List<Card> cards = new ArrayList<>();

    private boolean cheatMode = true;
    private List<Card>[] cheat = new List[4];

    private int offset = 40;

    public CardTablePanel(CardImages images) {
        super(false);
        this.images = images;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension size = getSize();

        //Show all cards if in cheat mode.
        if( cheatMode ) {
            int i = 0;
            int y = 0;

            if( cheat[1] != null && cheat[3] != null) {
                y = (int) (size.getHeight() / 2) - ((cheat[1].size() * offset) / 2) - (images.getHeight() / 2);
                for (Card card : cheat[1]) {
                    g.drawImage(images.getCard(card), 20, y + (offset * i++), this);
                }

                int x = (int) (size.getWidth() / 2) - ((cheat[2].size() * offset) / 2) - (images.getWidth() / 3);
                i = 0;
                for (Card card : cheat[2]) {
                    g.drawImage(images.getCard(card), x + (offset * i++), 20, this);
                }
            }

            if( cheat[2] != null) {
                i = 0;
                for (Card card : cheat[3]) {
                    g.drawImage(images.getCard(card), (int) size.getWidth() - (images.getWidth() + 20), y + (offset * i++), this);
                }
            }
        }

        if( this.cards != null ) {
            int x = (int) (size.getWidth() / 2) - ((cards.size() * offset) / 2) - (images.getWidth() / 3);
            int i = 0;
            for (Card card : this.cards) {
                g.drawImage(images.getCard(card), x + (offset * i++), (int) size.getHeight() - (images.getHeight() + 20), this);
            }
        }
    }

    @Override
    @Subscribe
    public void onGameStartedEvent(GameStartedEvent event) {
    }

    @Subscribe
    public void onCheatEvent(CheatEvent event){
        if( cheatMode ){
            for (int i=0; i<4; i++){
                cheat[i] = new ArrayList(event.getContext().getHand().getCards(i));
            }
            repaint();
        }
    }

    @Override
    @Subscribe
    public void onHandDealtEvent(HandDealtEvent event) {
        log.info("event = " + event);
        this.cards.clear();
        this.cards.addAll(event.getCards());
        repaint();
    }

    @Override
    @Subscribe
    public void onBidRequestEvent(BidRequestEvent event) {

    }

    @Override
    @Subscribe
    public void onBidEvent(BidEvent event) {

    }

    @Override
    @Subscribe
    public void onBidWonEvent(BidWonEvent event) {
        log.info("event = " + event);
        if( event.getSeat() == mySeat) {
            this.cards.clear();
            this.cards.addAll(event.getHand().getCards(mySeat));
            repaint();
        }
    }

    @Override
    @Subscribe
    public void onTrumpSelectedEvent(TrumpSelectedEvent event) {
        log.info("event = " + event);
        if( event.getSeat() == mySeat) {
            this.cards.removeAll(event.getKitty());
            repaint();
        }
    }

    @Override
    @Subscribe
    public void onTurnEvent(TurnEvent event) {

    }

    @Subscribe
    public void onCardPlayedEvent(CardPlayedEvent event){
        if( event.getSeat() == mySeat) {
            this.cards.remove(event.getCard());
            repaint();
        } else {
            cheat[event.getSeat()].remove(event.getCard());
            repaint();
        }
    }

    @Subscribe
    public void handleDeadEvent(DeadEvent deadEvent) {
        System.out.println("UNHANDLED EVENT: " + deadEvent);
    }
}