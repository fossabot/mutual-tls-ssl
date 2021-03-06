package nl.altindag.client.util;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.junit.Before;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public abstract class LogTestHelper<T> {

    private ListAppender<ILoggingEvent> listAppender;

    @Before
    public void setup() {
        Logger logger = (Logger) LoggerFactory.getLogger(getTargetClass());

        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    protected abstract Class<T> getTargetClass();

    @SuppressWarnings("SameParameterValue")
    protected List<String> getLogs(Level level) {
        return listAppender.list.stream()
                .filter(logEvent -> logEvent.getLevel() == level)
                .map(ILoggingEvent::getFormattedMessage)
                .collect(toList());
    }

}
