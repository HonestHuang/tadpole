package studio.littlefrog.tadpole.recorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.littlefrog.tadpole.validator.Assert;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class FileRecorder implements IRecorder {
    private static Logger logger = LoggerFactory.getLogger(FileRecorder.class);

    private ByteBuffer byteBuffer = ByteBuffer.allocateDirect(64 * 1024);

    private RandomAccessFile randomAccessFile;
    private FileChannel fileChannel;

    private AtomicLong wrotePosition = new AtomicLong(0L);
    private int bufferSize = 0;

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
        write(ByteBuffer.wrap((message + lineSeparator).getBytes(Charset.forName("utf-8"))));
        return this;
    }

    @Override
    public IRecorder br() {
        write(ByteBuffer.wrap((lineSeparator).getBytes()));
        return this;
    }


    private synchronized void write(ByteBuffer msgBuffer) {
        if (byteBuffer.remaining() >= msgBuffer.remaining()) {
            bufferSize += msgBuffer.remaining();
            byteBuffer.put(msgBuffer);
        } else {
            flushToDisk();
            bufferSize = msgBuffer.remaining();
        }
    }


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
        wrotePosition.addAndGet(bufferSize);
        byteBuffer.clear();
    }

    public static void main(String args[]) throws Exception {
        long start = System.currentTimeMillis();
        try (IRecorder recorder = new FileRecorder("E:\\log", "a.txt")) {
            for (int i = 0; i < 1024000; i++) {
                recorder.append("测试1111111111111111" + i).br();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时" + (end - start) + "ms");
    }
}
