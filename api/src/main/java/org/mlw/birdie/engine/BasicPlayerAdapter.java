package org.mlw.birdie.engine;


import java.util.List;

public class BasicPlayerAdapter extends AbstractPlayerAdapter {
    public BasicPlayerAdapter(String name){
        this.name = name;
    }

    @Override
    public Card.Suit handleCat(GameContext context, List<Card> cat) {
        return Card.Suit.Black;
    }
}
