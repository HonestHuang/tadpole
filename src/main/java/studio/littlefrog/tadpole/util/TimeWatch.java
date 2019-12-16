package studio.littlefrog.tadpole.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.littlefrog.tadpole.validator.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 用以统计耗时
 */
public class TimeWatch {
    private static Logger logger = LoggerFactory.getLogger(TimeWatch.class);

    private Map<String, TimeHolder> mapTimeHolder = new HashMap<>();
    private static final String defaultTitle = "默认";

    private BiConsumer<String, Long> consoleConsumer;

    public TimeWatch(BiConsumer<String, Long> consoleConsumer) {
        this.consoleConsumer = consoleConsumer;
    }

    public TimeWatch() {
        this.consoleConsumer = (title, time) -> {
            logger.info("作业<{}>耗时{}毫秒", title, time);
        };
    }

    public TimeWatch start() {
        start(defaultTitle);
        return this;
    }

    public TimeWatch start(String key) {
        Assert.isFalse(mapTimeHolder.containsKey(key), "请勿重复start");
        mapTimeHolder.put(key, new TimeHolder(key));
        return this;
    }

    public TimeWatch stop() {
        stop(defaultTitle);
        return this;
    }

    public TimeWatch stop(String key) {
        Assert.isTrue(mapTimeHolder.containsKey(key), "key对应的任务不存在");
        TimeHolder timeHolder = mapTimeHolder.get(key);
        if (!timeHolder.closed) {
            timeHolder.closed = true;
            consoleConsumer.accept(timeHolder.key, System.currentTimeMillis() - timeHolder.start);
        }
        return this;
    }

    public TimeWatch stopAll() {
        mapTimeHolder.keySet().forEach(this::stop);
        return this;
    }

    private static class TimeHolder {
        private String key;
        private Long start = System.currentTimeMillis();
        private Boolean closed = false;


        TimeHolder(String key) {
            this.key = key;
        }
    }
}
