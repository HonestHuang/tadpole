package studio.littlefrog.tadpole.http.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.littlefrog.tadpole.recorder.IRecorder;
import studio.littlefrog.tadpole.validator.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
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


    public void process() {
        File file = builder.file;
        OutputStream out = builder.outputStream;

        try (FileInputStream input = new FileInputStream(file);
             FileChannel fileChannel = input.getChannel();
             WritableByteChannel outChannel = Channels.newChannel(out)
        ) {
            fileChannel.transferTo(0, file.length(), outChannel);
            out.flush();
        } catch (Exception e) {
            exception(e);
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

        private OutputStream outputStream;

        private Consumer<Exception> exceptionConsumer;

        private IRecorder recorder;


        public Builder file(File file) {
            this.file = file;
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

        public Builder exception(Consumer<Exception> exceptionConsumer) {
            this.exceptionConsumer = exceptionConsumer;
            return this;
        }

        private void validate() {
            Assert.notNull(file, "文件或文件路径未设置");
            Assert.notNull(outputStream, "outputStream未设置");
        }

        public Downloader build() {
            validate();
            return new Downloader(this);
        }
    }

}
