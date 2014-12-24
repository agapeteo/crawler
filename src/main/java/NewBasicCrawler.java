import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class NewBasicCrawler {
    private static final int MAX_DEPTH = 2;
    private static final Pattern ANY_HOST_FILTER = Pattern.compile("(https?://)(www\\.)?[^#\n]+");
    private static final Pattern BINARY_TYPES = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
                                                        + "|png|tiff?|mid|mp2|mp3|mp4"
                                                        + "|wav|avi|mov|mpeg|ram|m4v|pdf"
                                                        + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
    private final int depth = 1;
    private boolean internalOnly = true;

    public static void main(String[] args) throws InterruptedException {
        NewBasicCrawler c = new NewBasicCrawler();
        Set<String> initial = new HashSet();
//        initial.add("https://code.google.com/p/lightcrawler/");
//        initial.add("http://ukr.net");
//        initial.add("http://habrahabr.ru");
        initial.add("http://en.wikipedia.org/wiki/Main_Page");
        Set<String> retrievedLinks = c.retrieveLinksFromSite(0, initial);
        for (String each : retrievedLinks) {
            System.out.println(each);
        }
        System.out.println(retrievedLinks.size());
    }

    private Set<String> retrieveLinksFromSite(int currentDepth, Set<String> initialLinks) throws InterruptedException {
        if (depth > MAX_DEPTH) {
            // (depth should be <= MAX_DEPTH)
        }
        if (currentDepth < depth) {
            Set<String> localLinks = new HashSet<String>();
            for (String link : initialLinks) {
                Set<String> newLinks = parseURL(link.toLowerCase());
                localLinks.addAll(retrieveLinksFromSite(currentDepth + 1, newLinks));
            }
            return localLinks;
        } else {
            return initialLinks;
        }
    }

    public Set<String> parseURL(String url) throws InterruptedException {
        Set<String> result = new HashSet<String>();
        Thread.sleep(500); // timeout or ban
        Connection connection = Jsoup.connect(url);
        String host;
        Document document;
        try {
            host = new URL(url).getHost();
            document = connection.timeout(3000).get();
        } catch (IOException e) {
//            LOG.error("Can't connect to " + url, e); //todo: logging
            System.out.println(e.getMessage() + " Cant connect to " + url);
            return Collections.emptySet();
        }
        Elements links = document.select("a[href]");
//        Elements links = document.select("a[href*=" + host +"]");
            for (Element link : links) {
                String extractedLink = link.attr("abs:href");
                if (internalOnly) {
                    addInternalLink(extractedLink, host, result);
                } else {
                    addAnyLink(extractedLink, result);
                }
            }
        return result;
    }

    private void addInternalLink(String extractedLink, String host, Set<String> result) {
        String pattern = "(https?://)(www\\.)?" + host + "[^#\n]+";
        if (Pattern.matches(pattern, extractedLink)
                && !BINARY_TYPES.matcher(extractedLink.toLowerCase()).matches()) {
            result.add(extractedLink);
        }
    }

    private void addAnyLink(String extractedLink, Set<String> result) {
        if (ANY_HOST_FILTER.matcher(extractedLink).matches()
                && !BINARY_TYPES.matcher(extractedLink.toLowerCase()).matches()) {
            result.add(extractedLink);
        }
    }
}
