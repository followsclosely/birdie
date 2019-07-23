package org.mlw.birdie;

public class Card implements Comparable<Card>{

    private Suit suit = null;
    public enum Suit{
        Red,Black,Yellow,Green;

        public static Suit instance(String c, Suit d){
            switch(c){
                case "R": return Red;
                case "B": return Black;
                case "Y": return Yellow;
                case "G": return Green;
                default: return d;
            }
        }
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
    public void setSuit(Suit suit) { this.suit = this.suit == null ? suit : this.suit; }

    public int getNumber() { return number; }
    public int getPoints() { return points; }
    public int getRank() { return rank; }

    public String toString() {
        return suit == null ? "Rook" : suit.toString().substring(0,1) + "/" + String.format("%02d", number);
    }

    @Override
    public boolean equals(Object obj) {
        if( !(obj instanceof Card) ) return false;
        Card card2 = (Card) obj;
        return suit == card2.suit && rank == card2.rank;
    }

    @Override
    public int compareTo(Card card2) {
        if( suit == null || card2.suit == null )
            return rank == card2.getRank() ? 0 : rank > card2.rank ? 1 : -1;
        else if (suit != card2.suit)
            return suit.ordinal() == card2.getSuit().ordinal() ? 0 : suit.ordinal() > card2.getSuit().ordinal() ? 1 : -1;
        else
            return rank == card2.rank ? 0 : rank > card2.rank ? 1 : -1;
    }
}