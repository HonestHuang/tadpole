package studio.littlefrog.tadpole.excel.exporter;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class PagerIterator<T> implements Iterator<T> {

    private int pageNo;
    private int pageSize = 20;
    private List<T> list;
    private PageFetcher<T> pageFetcher;
    private int currentPageIndex;

    public static interface PageFetcher<T> {
        List<T> fetch(int pageNo, int pageSize);
    }

    public void provider(PageFetcher<T> pageFetcher) {
        this.pageFetcher = pageFetcher;
    }


    @Override
    public boolean hasNext() {
        if (Objects.isNull(list) || currentPageIndex >= list.size()) {
            list = this.pageFetcher.fetch(pageNo, pageSize);
            pageNo++;
            currentPageIndex = 0;
        }
        return list.size() > 0;
    }

    @Override
    public T next() {
        return list.get(currentPageIndex++);
    }
}
