package studio.littlefrog.tadpole.excel;

import org.junit.jupiter.api.Test;
import studio.littlefrog.tadpole.excel.exporter.Exporter;
import studio.littlefrog.tadpole.excel.exporter.PagerIterator;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestExporter {


    @Test
    public void testListObjExporter() throws Exception {
        List<ExporterVO> list = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            list.add(new ExporterVO("content:" + (i + 1)));
        }
        FileOutputStream out = new FileOutputStream("d://testListObjExporter.xlsx");

        new Exporter.Builder()
                .v2007BigData()
                .sheet("sheet名称", list).col("content", "标题").end()
                .output(out)
                .build()
                .generate();
        out.close();
    }

    @Test
    public void testTwoSheetExporter() throws Exception {
        List<ExporterVO> list1 = new ArrayList<>();
        List<Map<String, String>> list2 = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            list1.add(new ExporterVO("content:" + (i + 1)));
            Map<String, String> map = new HashMap<>();
            map.put("content", "content2:" + (i + 1));
            list2.add(map);
        }
        FileOutputStream out = new FileOutputStream("d://testTwoSheetExporter.xlsx");

        new Exporter.Builder()
                .v2007()
                .sheet("sheet名称1", list1).col("content", "标题").end()
                .sheet("sheet名称2", list2).col("content", "标题").end()
                .output(out)
                .build()
                .generate();
        out.close();
    }

    @Test
    public void testDyncExporter() throws Exception {
        FileOutputStream out = new FileOutputStream("d://testDyncExporter.xlsx");

        PagerIterator<ExporterVO> iterator = new PagerIterator<>();


        iterator.provider((pageNo, pageSize) -> {
            List<ExporterVO> list = new ArrayList<>();
            if (pageNo * pageSize < 10000) {
                for (int i = 0; i < pageSize; i++) {
                    list.add(new ExporterVO(pageNo + ":" + i));
                }
            }
            return list;


        });


        new Exporter.Builder()
                .v2007BigData()
                .sheet("sheet名称1", iterator).col("content", "标题").end()
                .output(out)
                .build()
                .generate();
        out.close();
    }

}
