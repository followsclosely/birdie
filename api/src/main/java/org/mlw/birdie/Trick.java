package org.mlw.birdie;

import java.util.ArrayList;
import java.util.List;

public class Trick {
    private int seats;
    private int leader;
    private int winner;
    private List<Card> cards = new ArrayList<>();

    public Trick(int seats, int leader) {
        this.seats = seats;
        this.leader = leader;
    }

    public int getSeat(){
        return (leader + cards.size()) % seats;
    }
    public int getNextSeat(){
        return (leader + 1 + cards.size()) % seats;
    }

    public int getLeader() { return leader; }
    public List<Card> getCards() { return cards; }
    public int getWinner() { return winner; }
    public void setWinner(int winner) { this.winner = winner; }
    public int getPoints(){
        return cards.stream().mapToInt(Card::getPoints).sum();
    }
}
