package org.mlw.birdie.engine.event;

public class BasicEvent {
    private Object source;
    private Integer seat;

    public BasicEvent(Object source) {
        this.source = source;
    }
    public BasicEvent(Object source, Integer seat) {
        this.source = source;
        this.seat = seat;
    }

    public Object getSource() { return source; }
    public Integer getSeat() { return seat; }
}
