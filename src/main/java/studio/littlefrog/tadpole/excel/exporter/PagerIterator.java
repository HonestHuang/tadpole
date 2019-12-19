package studio.littlefrog.tadpole.excel.exporter;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class PagerIterator<T> implements Iterator<T> {

    private int pageNo = 0;
    private int pageSize = 20;
    private List<T> list;
    private PageFetcher<T> pageFetcher;
    private int pointer;

    public PagerIterator(int pageNo, int pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public PagerIterator() {

    }

    public interface PageFetcher<T> {
        List<T> fetch(int pageNo, int pageSize);
    }

    public void provider(PageFetcher<T> pageFetcher) {
        this.pageFetcher = pageFetcher;
    }


    @Override
    public boolean hasNext() {
        if (Objects.isNull(list) || pointer >= list.size()) {
            list = this.pageFetcher.fetch(pageNo, pageSize);
            pageNo++;
            pointer = 0;
        }
        return list.size() > 0;
    }

    @Override
    public T next() {
        return list.get(pointer++);
    }
}
