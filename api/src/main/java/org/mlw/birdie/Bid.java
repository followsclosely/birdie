package org.mlw.birdie;

public class Bid {
    private int seat;
    private Integer value;

    public Bid(int seat, Integer value) {
        this.seat = seat;
        this.value = value;
    }

    public Integer getValue() { return value; }
    public int getSeat() { return seat; }
}
