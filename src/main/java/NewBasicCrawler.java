import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Pattern;

public class NewBasicCrawler {
    private static final int MAX_DEPTH = 2;
    private static final Pattern ANY_HOST_FILTER = Pattern.compile("^http(s)?://(www\\.)?[\\w\\d\\W&&[^#]]+$");
    private static final Pattern BINARY_TYPES = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
                                                        + "|png|tiff?|mid|mp2|mp3|mp4"
                                                        + "|wav|avi|mov|mpeg|ram|m4v|pdf"
                                                        + "|rm|svg|wmv|swf|wma|zip|rar|gz))$");
    private final int depth = 1;
    private boolean internalOnly = true;

    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool();
        List<String> initial = new ArrayList<String>();
        initial.add("https://code.google.com/p/lightcrawler/");
        initial.add("http://bbc.com");
        initial.add("http://habrahabr.ru");
        initial.add("http://en.wikipedia.org/wiki/Main_Page");
        UrlMultithreadParser parser = new UrlMultithreadParser(initial, true);
//        do {
//            System.out.printf("******************************************\n");
//            System.out.printf("Main: Parallelism: %d\n", pool.getParallelism());
//            System.out.printf("Main: Active Threads: %d\n", pool.getActiveThreadCount());
//            System.out.printf("Main: Task Count: %d\n", pool.getQueuedTaskCount());
//            System.out.printf("Main: Steal Count: %d\n", pool.getStealCount());
//            System.out.printf("******************************************\n");
//        } while ((!parser.isDone()));
        Set<String> finalSet = new HashSet<String>(pool.invoke(parser));
//
        pool.shutdown();
        for (String each : finalSet){
            System.out.println(each);
        }
    }

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
