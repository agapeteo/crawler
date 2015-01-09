import java.util.*;
import java.util.concurrent.RecursiveTask;

public class Forker extends RecursiveTask<List<String>> {
    private static final int SEQUENTIAL_THRESHOLD = 1;
    private final List<String> links;
    private final int start;
    private final int end;
    private UrlParser directParser;

    public Forker(List<String> links, UrlParser directParser) {
        this(links, directParser, 0, links.size());
    }

    public Forker(List<String> links, UrlParser directParser, int start, int end) {
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
        Forker one = new Forker(links, directParser, start, start + length/2);
        one.fork();
        Forker two = new Forker(links, directParser, start + length/2, end);
        two.fork();

        List<String> finalList = new ArrayList<String>();
        joinResults(finalList, one, two);

        return finalList;
    }

    private void joinResults(List<String> finalList, Forker one, Forker two) {
        finalList.addAll(one.join());
        finalList.addAll(two.join());
    }
}
