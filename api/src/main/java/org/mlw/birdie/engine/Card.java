package org.mlw.birdie.engine;

public class Card {
    private Suit suit = null;
    public enum Suit{
        Red,Black,Yellow,Green
    }
    private int rank;
    private int number;
    private int points;

    Card(Suit suit, int rank, int number, int points){
        this.rank = rank;
        this.suit = suit;
        this.number = number;
        this.points = points;
    }

    public Suit getSuit() { return suit; }
    public int getNumber() { return number; }
    public int getPoints() { return points; }
    public int getRank() { return rank; }

    public String toString() {
        return suit == null ? "Rook" : suit.toString().substring(0,1) + "/" + String.format("%02d", number);
    }
}