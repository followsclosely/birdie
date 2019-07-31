package org.mlw.birdie;

import com.google.common.eventbus.Subscribe;
import org.mlw.birdie.engine.event.BidWonEvent;
import org.mlw.birdie.engine.event.CardPlayedEvent;
import org.mlw.birdie.engine.event.HandDealtEvent;
import org.mlw.birdie.engine.event.TrumpSelectedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardTablePanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(CardTablePanel.class);

    private CardImages images;
    private List<Card> cards = new ArrayList<>();

    private Hand hand = null;

    private int offset = 40;

    public CardTablePanel(CardImages images) {
        super(false);
        this.images = images;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension size = getSize();

        //Show all cards if in cheat mode.
        if( this.hand != null) {
            int i = 0;
            List<Card> cards1 = this.hand.getCards(1);
            int y = (int)(size.getHeight()/2) - ((cards1.size()*offset)/2) - (images.getHeight()/2);
            for (Card card : cards1) {
                g.drawImage(images.getCard(card), 20, y + (offset * i++),this);
            }

            List<Card> cards2 = this.hand.getCards(2);
            int x = (int) (size.getWidth() / 2) - ((cards2.size() * offset) / 2) - (images.getWidth() / 3);
            i = 0;
            for (Card card : cards2) {
                g.drawImage(images.getCard(card), x + (offset * i++), 20, this);
            }

            i = 0;
            for (Card card : this.hand.getCards(3)) {
                g.drawImage(images.getCard(card), (int)size.getWidth() - (images.getWidth() + 20), y + (offset * i++),this);
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

    @Subscribe
    public void onHandDealtEvent(HandDealtEvent event) throws InterruptedException {

        log.info("event = " + event);

        this.cards.clear();
        this.cards.addAll(event.getCards());
        repaint();
        Thread.sleep(500);
        this.hand = event.getHand();
    }

    @Subscribe
    public void onBidWonEvent(BidWonEvent event){

        log.info("event = " + event);

        this.cards.addAll(event.getHand().getKitty());
        Collections.sort(this.cards);
        repaint();
    }

    @Subscribe
    public void onCardPlayedEvent(CardPlayedEvent event){
        log.info("event = " + event);
        this.cards.remove(event.getCard());
        repaint();
    }

    @Subscribe
    public void onTrumpSelectedEvent(TrumpSelectedEvent event){
        log.info("event = " + event);
        this.cards.removeAll(event.getKitty());
        repaint();
    }
}
