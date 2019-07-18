package org.mlw.birdie;

public class Bid {
    private int seat;
    private Integer value;

    public Bid(int seat, Integer value) {
        this.seat = seat;
        this.value = value;
    }

    public Integer getValue() { return value; }
    public void setValue(Integer value) {this.value = value;}
    public int getSeat() { return seat; }

    @Override
    public String toString() {
        return "Bid{seat=" + seat + ", value=" + value + '}';
    }
}
