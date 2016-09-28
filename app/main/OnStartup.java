package main;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vk.group.VkGroupTask;

/**
 * Created by Zigzag on 10.09.2016.
 */
@Singleton
public class OnStartup {

    private static final Logger LOG = LoggerFactory.getLogger(OnStartup.class);

    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(4, r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });

    @Inject
    public OnStartup() {
        EXECUTOR.scheduleAtFixedRate(new VkGroupTask(), millisToNextHour(), 1L, TimeUnit.HOURS);
        LOG.info("EXECUTOR started!");
    }

    private long millisToNextHour() {
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        int millis = calendar.get(Calendar.MILLISECOND);
        long minutesToNextHour = 60L - minutes;
        long secondsToNextHour = 60L - seconds;
        long millisToNextHour = 1000L - millis;
        return minutesToNextHour * 60 * 1000 + secondsToNextHour * 1000 + millisToNextHour;
    }
}
