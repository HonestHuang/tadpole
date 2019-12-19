package studio.littlefrog.tadpole.recorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jRecorder implements IRecorder {

    private static Logger logger = LoggerFactory.getLogger(Slf4jRecorder.class);

    @Override
    public IRecorder append(String message) {
        logger.info(message);
        return this;
    }

    @Override
    public IRecorder br(Integer num) {
        logger.info("");
        return this;
    }

    @Override
    public void close() throws Exception {

    }
}
