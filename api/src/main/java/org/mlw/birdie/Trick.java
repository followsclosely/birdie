package org.mlw.birdie;

import java.util.ArrayList;
import java.util.List;

public class Trick {
    private int leader;
    private int winner;
    private List<Card> cards = new ArrayList<>();

    public Trick(int leader) {
        this.leader = leader;
    }

    public int getLeader() { return leader; }
    public List<Card> getCards() { return cards; }

    public int getWinner() { return winner; }
    public void setWinner(int winner) { this.winner = winner; }
}
