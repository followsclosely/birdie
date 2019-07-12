package org.mlw.birdie;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private int dealerIndex = 0;
    private List<List<Card>> cards;
    private List<Card> kitty = new ArrayList<>(5);

    private List<Bid> bids = new ArrayList<>(10);
    private List<Trick> tricks = new ArrayList<>(15);
    private Card.Suit trump;

    public Hand(int seats, int dealerIndex) {
        this.dealerIndex = dealerIndex;
        this.cards = new ArrayList<List<Card>>(seats);
        for(int i=0; i<seats; i++){
            this.cards.add(new ArrayList<Card>());
        }
    }

    public int getDealerIndex() { return dealerIndex; }

    public List<Card> getCards(PlayerAdapter player){
        return cards.get(player.getSeat());
    }

    public List<Bid> getBids() { return bids; }
    public Bid getMaxBid() {
        Bid max = new Bid(0,0);
        for (Bid bid : bids) {
            if (bid.getValue() != null && bid.getValue() > max.getValue()) {
                max = bid;
            }
        }

        return max;
    }

    public Card.Suit getTrump() { return trump; }
    public void setTrump(Card.Suit trump) {
        this.trump = trump;

        //Set the rook trump!
        for(List<Card> cards : this.cards){
            for(Card card : cards){
                if( card.getSuit() == null) {
                    card.setSuit(trump);
                }
            }
        }
    }

    public List<Card> getKitty() { return kitty; }

    public List<Trick> getTricks() { return tricks; }
    public Trick getTrick(){ return tricks.get(tricks.size()-1); }
    public Trick createTrick(int leader){
        Trick trick = new Trick(leader);
        tricks.add(trick);
        return trick;
    }
}
