import java.util.*;
import java.util.concurrent.RecursiveTask;

public class UrlMultithreadParser extends RecursiveTask<List<String>> {
    private static final int SEQUENTIAL_THRESHOLD = 1;
    private final List<String> links;
    private final int start;
    private final int end;
    private UrlParser directParser;

    public UrlMultithreadParser(List<String> links, UrlParser directParser) {
        this(links, directParser, 0, links.size());
    }

    public UrlMultithreadParser(List<String> links, UrlParser directParser, int start, int end) {
        this.links = links;
        this.directParser = directParser;
        this.start = start;
        this.end = end;
    }

    @Override
    protected List<String> compute() {
        int length = end - start;
        if (length == SEQUENTIAL_THRESHOLD){
            return directParser.parseURL(links.get(start));
        }
        UrlMultithreadParser one = new UrlMultithreadParser(links, directParser, start, start + length/2);
        one.fork();
        UrlMultithreadParser two = new UrlMultithreadParser(links, directParser, start + length/2, end);
        two.fork();

        List<String> finalList = new ArrayList<String>();
        List<String> newLinks1 = one.join();
        List<String> newLinks2 = two.join();
        finalList.addAll(newLinks1);
        finalList.addAll(newLinks2);

        return finalList;
    }
}
