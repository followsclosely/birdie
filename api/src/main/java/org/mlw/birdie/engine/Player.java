package org.mlw.birdie.engine;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Card> cards = new ArrayList<>();

    public Player(){}
    public Player(String name) {
        this.name = name;
    }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public List<Card> getCards() { return cards; }
}
