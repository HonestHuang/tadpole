package studio.littlefrog.tadpole.http.response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.littlefrog.tadpole.recorder.IRecorder;
import studio.littlefrog.tadpole.validator.Assert;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;
import java.util.function.Consumer;

public class Downloader {

    private static Logger logger = LoggerFactory.getLogger(Downloader.class);

    private Builder builder;

    private Downloader(Builder builder) {
        this.builder = builder;
    }

    private Downloader() {
    }

    private void prepare() throws UnsupportedEncodingException {
        String name = builder.name;
        HttpServletResponse response = builder.response;
        if (Objects.nonNull(response)) {
            response.setContentType("application/octet-stream");
            if (StringUtils.isNotBlank(name)) {
                if (builder.agent.contains("Firefox")) {
                    name = "=?UTF-8?B?" + new BASE64Encoder().encode(name.getBytes("utf-8")) + "?=";
                } else {
                    name = URLEncoder.encode(name, "utf-8");
                    name = name.replace("+", " ");
                }
                response.setHeader("Content-Disposition", "attachment;filename=" + name);

            }
        }
    }

    public void process() {
        File file = builder.file;
        InputStream input = builder.input;
        OutputStream out = builder.outputStream;

        try {
            prepare();

            if (Objects.nonNull(file)) {
                zeroCopy(file, out);
            }
            if (Objects.nonNull(input)) {
                commonWrite(input, out);
            }
        } catch (Exception e) {
            exception(e);
        }
    }

    private void commonWrite(InputStream in, OutputStream out) throws IOException {
        IOUtils.copy(in, out);
    }

    private void zeroCopy(File file, OutputStream out) throws IOException {
        try (FileInputStream input = new FileInputStream(file);
             FileChannel fileChannel = input.getChannel();
             WritableByteChannel outChannel = Channels.newChannel(out)
        ) {
            fileChannel.transferTo(0, file.length(), outChannel);
            out.flush();
        }
    }


    private void exception(Exception e) {
        logger.error("导出excel异常", e);
        Consumer<Exception> consumer = this.builder.exceptionConsumer;
        if (Objects.nonNull(consumer)) {
            consumer.accept(e);
        }
        IRecorder recorder = this.builder.recorder;
        if (Objects.nonNull(recorder)) {
            recorder.append(e.getMessage()).br();
        }
    }

    public static class Builder {

        private File file;
        private InputStream input;
        private HttpServletResponse response;
        private String name;
        private String agent = "";

        private OutputStream outputStream;

        private Consumer<Exception> exceptionConsumer;

        private IRecorder recorder;

        public Builder agent(String agent) {
            this.agent = agent;
            return this;
        }

        public Builder file(File file) {
            this.file = file;
            return this;
        }

        public Builder input(InputStream input) {
            this.input = input;
            return this;
        }

        public Builder out(OutputStream outputStream) {
            this.outputStream = outputStream;
            return this;
        }

        public Builder recorder(IRecorder recorder) {
            this.recorder = recorder;
            return this;
        }

        public Builder response(HttpServletResponse response) {
            this.response = response;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder exception(Consumer<Exception> exceptionConsumer) {
            this.exceptionConsumer = exceptionConsumer;
            return this;
        }

        private void validate() {
            Assert.isTrue(Objects.nonNull(file) || Objects.nonNull(input), "输入未设置");
            Assert.notNull(outputStream, "outputStream未设置");
        }

        public Downloader build() {
            validate();
            return new Downloader(this);
        }
    }

}
