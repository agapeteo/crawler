import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class NewBasicCrawler {
    private static final int MAX_DEPTH = 2;
    private static final int depth = 1;

    public static void main(String[] args) {
        boolean internalOnly = true;
        Set<String> finalSet = new HashSet<String>();
        ForkJoinPool pool = new ForkJoinPool();

        Set<String> first = new HashSet<String>();
//        first.add("https://code.google.com/p/lightcrawler/");
        first.add("http://bbc.com");
        first.add("http://habrahabr.ru");
        first.add("http://en.wikipedia.org/wiki/Main_Page");

        List<Set<String>> initial = new ArrayList<Set<String>>();
        initial.add(first);

        for (int x = 0; x <= depth; x++) {
            Forker parser = new Forker(new ArrayList<String>(initial.get(x)), new UrlParserImpl(internalOnly));
            Set<String> newSet = new HashSet<String>(pool.invoke(parser));
            initial.add(newSet);
        }

        pool.shutdown();

        for (Set<String> each : initial){
            finalSet.addAll(each);
        }

        for (String each : finalSet) {
            System.out.println(each);
        }
    }

//    private startParsing() {}

//    private Set<String> retrieveLinksFromSite(int currentDepth, Set<String> initialLinks) {
//        if (currentDepth < depth) {
//            Set<String> localLinks = new HashSet<String>();
//            for (String link : initialLinks) {
//                Set<String> newLinks = parseURL(link.toLowerCase());
//                localLinks.addAll(retrieveLinksFromSite(currentDepth + 1, newLinks));
//            }
//            return localLinks;
//        } else {
//            return initialLinks;
//        }
//    }
}
