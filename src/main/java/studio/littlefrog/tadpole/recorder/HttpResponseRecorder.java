package studio.littlefrog.tadpole.recorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.littlefrog.tadpole.validator.Assert;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class HttpResponseRecorder implements IRecorder {

    private static Logger logger = LoggerFactory.getLogger(HttpResponseRecorder.class);

    private PrintWriter writer;

    public HttpResponseRecorder(HttpServletResponse response) {
        try {
            response.setContentType("text/html; charset=UTF-8");
            writer = response.getWriter();
        } catch (Exception e) {
            Assert.exception(e.getMessage());
        }
    }

    @Override
    public IRecorder append(String message) {
        writer.write(message);
        return this;
    }

    @Override
    public IRecorder br(Integer num) {
        writer.write(lineSeparator);
        return this;
    }

    @Override
    public void close() throws Exception {
        try {
            writer.close();
        } catch (Exception e) {
            logger.error("writer close 失败", e);
        }
    }
}
