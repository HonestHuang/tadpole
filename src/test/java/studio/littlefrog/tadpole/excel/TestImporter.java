package studio.littlefrog.tadpole.excel;

import org.apache.commons.lang3.SystemUtils;
import org.apache.poi.ss.usermodel.Row;
import org.junit.jupiter.api.Test;
import studio.littlefrog.tadpole.excel.importer.Importer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * TODO
 *
 * @author HuangShuCheng
 */
public class TestImporter {

    @Test
    public void test() {
        File file = new File("D:\\blacklist.xlsx");
        List<HashMap> list = new ArrayList<>();
        new Importer.Builder()
                .v2007().input(file)
                .sheet(HashMap.class)
                .title(0)
                .from(1)
                .col(1, "domainId")
                .list(list)
                .rowConsumer((HashMap o, Row r,Row title)->{
                    System.out.println(r.getCell(0).toString());
                    System.out.println(title.getCell(0).toString());
                    o.put("title",title.getCell(0).toString());
                })
                .end()
                .build().generate();

    }

}
