package org.mlw.birdie.console;

import com.google.common.eventbus.EventBus;
import org.mlw.birdie.engine.AbstractPlayerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AbstractConsolePlayerAdapter extends AbstractPlayerAdapter {
    private static final Logger log = LoggerFactory.getLogger(AbstractConsolePlayerAdapter.class);

    private EventBus next = null;

    public AbstractConsolePlayerAdapter(EventBus server, EventBus next) {
        super(server);
        this.next = next;
        this.seat = 0;
    }

    //Helper methods to read from System.in
    private BufferedReader reader = null;
    protected synchronized String readLine() {
        if( reader==null ){
            reader = new BufferedReader(new InputStreamReader(System.in));
        }
        try {
            return reader.readLine();
        } catch (Exception ignore){
            return readLine();
        }
    }
    protected synchronized Integer readInteger() {
        try {
            String line = readLine().trim();
            return ( line.length() > 0) ? Integer.parseInt(line) : null;
        } catch (Exception ignore){
            System.out.println(ignore.getMessage());
            return readInteger();
        }
    }

    protected Object passEventDown(Object event){
        log.info("passEventDown: " + event);
        if(next!=null) next.post(event);
        return event;
    }
}
