import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class UrlParserImpl implements UrlParser {
    private static final Pattern ANY_HOST_FILTER = Pattern.compile("^http(s)?://(www\\.)?[\\w\\d\\W&&[^#]]+$");
    private static final Pattern BINARY_TYPES = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
                                                + "|png|tiff?|mid|mp2|mp3|mp4"
                                                + "|wav|avi|mov|mpeg|ram|m4v|pdf"
                                                + "|rm|svg|wmv|swf|wma|zip|rar|gz))$");

    private final boolean internalOnly;

    public UrlParserImpl(boolean internalOnly) {
        this.internalOnly = internalOnly;
    }

    @Override
    public List<String> parseURL(String url) {
        final int timeout = 3000;
        List<String> result = new ArrayList<String>();
        Connection connection = Jsoup.connect(url);
        String host;
        Document document;
        try {
            host = new URL(url).getHost();
            document = connection.timeout(timeout).get();
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
