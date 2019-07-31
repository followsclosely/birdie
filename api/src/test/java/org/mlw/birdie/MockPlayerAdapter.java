package org.mlw.birdie;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.mlw.birdie.engine.AbstractPlayerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockPlayerAdapter extends AbstractPlayerAdapter {

    private Map<Class, List<Object>> events = new HashMap<Class, List<Object>>(){
        @Override
        public List<Object> get(Object key) {
            List<Object> list = super.get(key);
            if( list == null ){
                //todo: Why is this cast needed?
                super.put((Class)key, list = new ArrayList<>());
            }
            return list;
        }
    };

    public MockPlayerAdapter(EventBus server, int seat){
        super(server, "Mock"+ seat, seat);
    }

    private int eventsHandled = 0;
    @Override public String getName() { return "mock"; }

    @Subscribe
    public void handleDeadEvent(DeadEvent deadEvent) {
        events.get(deadEvent.getEvent().getClass()).add(deadEvent.getEvent());
        eventsHandled++;
    }

    public List<Object> getEvents(Class klass){
        return events.get(klass);
    }
}
