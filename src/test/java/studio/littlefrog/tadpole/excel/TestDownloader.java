package studio.littlefrog.tadpole.excel;

import org.junit.jupiter.api.Test;
import studio.littlefrog.tadpole.http.response.Downloader;
import studio.littlefrog.tadpole.recorder.IRecorder;
import studio.littlefrog.tadpole.recorder.Slf4jRecorder;
import studio.littlefrog.tadpole.common.TimeWatch;

import java.io.File;
import java.io.FileOutputStream;

public class TestDownloader {


    @Test
    public void test() throws Exception {

        TimeWatch timeWatch = new TimeWatch();
        timeWatch.start();
        FileOutputStream out = new FileOutputStream("d://testDownloader.rar");
        File file = new File("d://SDK.rar");
        IRecorder recorder = new Slf4jRecorder();
        new Downloader.Builder()
                .file(file)
                .out(out)
                .recorder(recorder)
                .build()
                .process();
        out.close();

        timeWatch.stopAll();
    }


}
