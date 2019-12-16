package studio.littlefrog.tadpole.recorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.littlefrog.tadpole.util.TimeWatch;
import studio.littlefrog.tadpole.validator.Assert;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Objects;

public class FileRecorder implements IRecorder {
    private static Logger logger = LoggerFactory.getLogger(FileRecorder.class);

    private ByteBuffer byteBuffer = ByteBuffer.allocateDirect(64 * 1024);

    private RandomAccessFile randomAccessFile;
    private FileChannel fileChannel;

    private String lineSeparator = System.getProperty("line.separator");

    public FileRecorder(String dir, String name) throws Exception {
        checkAndMakeDir(dir);
        File file = checkAndCreateFile(dir, name);

        this.randomAccessFile = new RandomAccessFile(file, "rw");
        this.fileChannel = randomAccessFile.getChannel();
    }

    private static void checkAndMakeDir(String dir) {
        Assert.notBlank(dir, "路径为空");
        File file = new File(dir);
        if (!file.exists()) {
            Assert.isTrue(file.mkdirs(), "创建路径失败");
        }
    }

    private static File checkAndCreateFile(String dir, String fileName) throws Exception {
        File file = new File(getAbsolutePath(dir, fileName));
        if (!file.exists()) {
            Assert.isTrue(file.createNewFile(), "创建文件失败");
        }
        return file;
    }

    public static String getAbsolutePath(String dir, String fileName) {
        Assert.notBlank(fileName, "文件名为空");
        if (!dir.endsWith(File.pathSeparator)) {
            dir += File.separator;
        }
        return dir + fileName;
    }

    @Override
    public IRecorder append(String message) {
        write(ByteBuffer.wrap(message.getBytes(Charset.forName("utf-8"))));
        return this;
    }

    @Override
    public IRecorder br() {
        return br(1);
    }

    @Override
    public IRecorder br(Integer num) {
        for (int i = 0; i < num; i++) {
            write(ByteBuffer.wrap((lineSeparator).getBytes()));
        }
        return this;
    }

    private synchronized void write(ByteBuffer msgBuffer) {
        if (byteBuffer.remaining() >= msgBuffer.remaining()) {
            byteBuffer.put(msgBuffer);
        } else {
            int needLength = msgBuffer.remaining();
            int remainder = byteBuffer.remaining();
            ByteBuffer buffer = ByteBuffer.allocate(needLength - remainder);

            for (int i = 0; i < needLength; i++) {
                if (i < remainder) {
                    byteBuffer.put(msgBuffer.get(i));
                } else {
                    buffer.put(msgBuffer.get(i));
                }
            }

            flushToDisk();
            write(buffer);
        }
    }

//    private synchronized void write(ByteBuffer msgBuffer) {
//        try {
//            msgBuffer.flip();
//            fileChannel.write(msgBuffer);
//        } catch (IOException e) {
//            logger.error("写入文件失败", e);
//        }
//
//    }


    @Override
    public void close() throws Exception {
        flushToDisk();
        if (Objects.nonNull(fileChannel)) {
            fileChannel.close();
        }
        if (Objects.nonNull(randomAccessFile)) {
            randomAccessFile.close();
        }
    }

    private void flushToDisk() {
        byteBuffer.flip();
        try {
            fileChannel.write(byteBuffer);
        } catch (IOException e) {
            logger.error("写入文件失败", e);
        }
        byteBuffer.clear();
    }

    public static void main(String args[]) throws Exception {
        TimeWatch watch = new TimeWatch();
        watch.start();
        try (IRecorder recorder = new FileRecorder("E:\\log", "b.txt")) {
            for (int i = 0; i < 102400000; i++) {
                recorder.append("0");
            }
        }
        watch.stop();
    }
}
