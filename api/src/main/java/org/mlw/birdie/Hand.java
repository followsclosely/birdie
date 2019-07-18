package org.mlw.birdie;

import java.util.ArrayList;
import java.util.List;

public class Hand {

    private static final int STARTING_BID = 75;

    private int seats;
    private int dealerIndex = 0;
    private List<List<Card>> cards;
    private List<Card> kitty = new ArrayList<>(5);

    private List<Bid> bids = new ArrayList<>(10);
    private List<Trick> tricks = new ArrayList<>(15);
    private Card.Suit trump;

    public Hand(int seats, int dealerIndex) {
        this.seats = seats;
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
    public Bid getLastBid(){
        return bids.size() > 0 ? bids.get(bids.size()-1) : new Bid((dealerIndex),STARTING_BID);
    }
    public Bid getMaxBid() {
        Bid max = new Bid((dealerIndex),STARTING_BID);
        for (Bid bid : bids) {
            if (bid.getValue() != null && bid.getValue() > max.getValue()) {
                max = bid;
            }
        }

        return max;
    }

    public boolean isTrump(Card card){
        return card.getSuit().equals(trump);
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

    public int[] getScores(){
        int[] scores = new int[seats];

        int winner = dealerIndex;
        for(Trick trick : tricks){
            int points = trick.getPoints();
            winner = (trick.getWinner() + trick.getLeader()) % seats;
            scores[winner] += points;
        }

        //Award the cat points to the last trick.
        int kittyPoints = kitty.stream().mapToInt(Card::getPoints).sum();
        scores[winner] += kittyPoints;

        return scores;
    }

    public int getSeats() { return seats; }
}
