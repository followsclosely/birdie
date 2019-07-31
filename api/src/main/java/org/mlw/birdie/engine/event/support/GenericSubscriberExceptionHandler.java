package org.mlw.birdie.engine.event.support;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericSubscriberExceptionHandler implements SubscriberExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GenericSubscriberExceptionHandler.class);

    @Override
    public void handleException(Throwable exception, SubscriberExceptionContext context) {
        log.error("Unexpected Error", exception);
    }
}
