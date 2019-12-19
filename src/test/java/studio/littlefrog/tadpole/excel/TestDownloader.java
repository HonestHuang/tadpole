package studio.littlefrog.tadpole.excel;

import org.junit.jupiter.api.Test;
import studio.littlefrog.tadpole.excel.exporter.Exporter;
import studio.littlefrog.tadpole.excel.exporter.PagerIterator;
import studio.littlefrog.tadpole.http.response.Downloader;
import studio.littlefrog.tadpole.recorder.IRecorder;
import studio.littlefrog.tadpole.recorder.Slf4jRecorder;
import studio.littlefrog.tadpole.util.TimeWatch;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
