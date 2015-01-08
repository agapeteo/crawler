import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Pattern;

public class UrlMultithreadParser extends RecursiveTask<List<String>> {
    private static final int SEQUENTIAL_THRESHOLD = 1;
    private static final Pattern ANY_HOST_FILTER = Pattern.compile("^http(s)?://(www\\.)?[\\w\\d\\W&&[^#]]+$");
    private static final Pattern BINARY_TYPES = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
            + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf"
            + "|rm|svg|wmv|swf|wma|zip|rar|gz))$");
    private final List<String> links;
    private final boolean internalOnly;
    private final int start;
    private final int end;

    public UrlMultithreadParser(List<String> links, boolean internalOnly) {
        this(links, internalOnly, 0, links.size());
    }

    public UrlMultithreadParser(List<String> links, boolean internalOnly, int start, int end) {
        this.links = links;
        this.internalOnly = internalOnly;
        this.start = start;
        this.end = end;
    }

    @Override
    protected List<String> compute() {
        int length = end - start;
        if (length == SEQUENTIAL_THRESHOLD){
            return parseURL(links.get(start));
        }
        UrlMultithreadParser one = new UrlMultithreadParser(links, internalOnly, start, start + length/2);
        one.fork();
        UrlMultithreadParser two = new UrlMultithreadParser(links, internalOnly, start + length/2, end);
        two.fork();

        List<String> finalList = new ArrayList<String>();
        List<String> newLinks1 = one.join();
        List<String> newLinks2 = two.join();
        finalList.addAll(newLinks1);
        finalList.addAll(newLinks2);

        return finalList;
    }

    private List<String> parseURL(String url) {
        System.out.println("------->"+url);
        List<String> result = new ArrayList<String>();
        Connection connection = Jsoup.connect(url);
        String host;
        Document document;
        try {
            host = new URL(url).getHost();
            document = connection.timeout(3000).get();
        } catch (IOException e) {
//            LOG.error("Can't connect to " + url, e); //todo: logging
            return Collections.emptyList();
        }
        Elements links = document.select("a[href]");
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

    private void addInternalLink(String extractedLink, String host, List<String> result) {
        String pattern = "^http(s)?://(www\\.)?" + host + "[\\w\\d\\W&&[^#]]+$";
        if (Pattern.matches(pattern, extractedLink)
                && !BINARY_TYPES.matcher(extractedLink.toLowerCase()).matches()) {
            result.add(extractedLink);
        }
    }

    private void addAnyLink(String extractedLink, List<String> result) {
        if (ANY_HOST_FILTER.matcher(extractedLink).matches()
                && !BINARY_TYPES.matcher(extractedLink.toLowerCase()).matches()) {
            result.add(extractedLink);
        }
    }
}
