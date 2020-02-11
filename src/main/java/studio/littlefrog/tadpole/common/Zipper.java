package studio.littlefrog.tadpole.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.littlefrog.tadpole.validator.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author HuangShuCheng
 */
public class Zipper {
    private static final Logger log = LoggerFactory.getLogger(Zipper.class);

    private Zipper.Builder builder;

    private String zipPath;
    private String unzipPath;
    private File unzipFile;

    private Zipper(Zipper.Builder builder) {
        this.builder = builder;
        if (builder.compress) {
            this.zipPath = builder.targetPath + builder.name + ".zip";
            // 如果已经存在
            if (new File(zipPath).exists()) {
                this.zipPath = builder.targetPath + builder.name + System.currentTimeMillis() + ".zip";
            }
        } else {
            this.unzipPath = builder.targetPath + builder.name + File.separator;
            File unzipDir = new File(unzipPath);
            if (!unzipDir.exists() && !unzipDir.mkdirs()) {
                log.error("创建目录失败");
            }
        }
    }

    public Optional<File> process() {
        if (builder.compress) {
            try {
                zip();
                return Optional.of(new File(zipPath));
            } catch (Exception e) {
                exception(e);
                return Optional.empty();
            }
        } else {
            try {
                unzip();
                return Optional.ofNullable(unzipFile);
            } catch (Exception e) {
                exception(e);
                return Optional.empty();
            }
        }
    }

    private void zip() throws Exception {
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipPath))) {
            zip(builder.file, out, builder.file.getName());
        }
    }

    private void zip(File file, ZipOutputStream out, String base) throws Exception {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            out.putNextEntry(new ZipEntry(base + "/"));
            if (Objects.nonNull(files)) {
                for (File f : files) {
                    zip(f, out, base + File.separator + f.getName());
                }
            }
        } else {
            try (FileInputStream in = new FileInputStream(file)) {
                out.putNextEntry(new ZipEntry(base));
                int len;
                byte[] buf = new byte[1024];
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }


    private void unzip() throws Exception {
        try (ZipFile zipFile = new ZipFile(builder.file)) {
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();

                File file = new File(unzipPath + entry.getName());

                if (Objects.isNull(unzipFile)) {
                    unzipFile = file;
                }
                if (entry.isDirectory()) {
                    if (!file.exists() && !file.mkdirs()) {
                        log.error("创建目录失败");
                    }
                } else {
                    if (!file.exists() && !file.createNewFile()) {
                        log.error("创建文件失败");
                    }
                    try (InputStream is = zipFile.getInputStream(entry);
                         FileOutputStream fos = new FileOutputStream(file)) {
                        int len;
                        byte[] buf = new byte[1024];
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                        }
                    }
                }
            }
        }
    }

    private void exception(Exception e) {
        log.error("异常", e);
        Consumer<Exception> consumer = this.builder.exceptionConsumer;
        if (Objects.nonNull(consumer)) {
            consumer.accept(e);
        }
    }

    public static class Builder {
        private File file;

        private String targetPath;

        private Boolean compress = true;

        private String name;

        private Consumer<Exception> exceptionConsumer;

        public Zipper.Builder file(File file) {
            this.file = file;
            return this;
        }

        public Zipper.Builder name(String name) {
            this.name = name;
            return this;
        }

        public Zipper.Builder targetPath(String targetPath) {
            this.targetPath = targetPath;
            return this;
        }

        public Zipper.Builder zip() {
            this.compress = true;
            return this;
        }

        public Zipper.Builder unzip() {
            this.compress = false;
            return this;
        }

        public Zipper.Builder exception(Consumer<Exception> exceptionConsumer) {
            this.exceptionConsumer = exceptionConsumer;
            return this;
        }

        private void validate() {
            Assert.notNull(file, "file未设置");
            if (StringUtils.isBlank(targetPath)) {
                targetPath = System.getProperty("user.home");
            }
            if (!targetPath.endsWith(File.separator)) {
                targetPath = targetPath + File.separator;
            }

            if (StringUtils.isBlank(name)) {
                String fileName = file.getName();

                name = fileName.contains(".")
                        ? fileName.substring(0, fileName.lastIndexOf("."))
                        : fileName;
            }
        }

        public Zipper build() {
            validate();
            return new Zipper(this);
        }
    }

}
