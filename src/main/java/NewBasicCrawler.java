import java.util.*;

public class NewBasicCrawler {

    public static void main(String[] args) {
        Set<String> first = new HashSet<String>();
        int i = 0;
        first.add("https://code.google.com/p/lightcrawler/");
        first.add("https://i.u_a");
//        first.add("http://bbc.com");
//        first.add("http://h.wikipedia.org/wiki/Main_Page");

        Crawler crawler = new CrawlerImpl(2, true, first);

        Set<String> finalSet = crawler.crawl();

        for (String each : finalSet) {
            System.out.println(++i + " " + each);
        }
    }
}
