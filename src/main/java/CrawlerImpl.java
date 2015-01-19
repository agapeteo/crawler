import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

public class CrawlerImpl implements Crawler {
    private final int depth;
    private final Set<String> initialLinks;
    private final boolean internalOnly;

    public CrawlerImpl(int depth, boolean internalOnly, Set<String> initialLinks) {
        this.depth = depth;
        this.internalOnly = internalOnly;
        this.initialLinks = initialLinks;
    }

    @Override
    public Set<String> crawl() {
        checkDepth();

        List<Set<String>> depthsOfUrls = new ArrayList<Set<String>>();
        addInitialLinks(depthsOfUrls);

        ForkJoinPool pool = new ForkJoinPool();

        for (int x = 0; x < depth; x++) {
            Forker parser = new Forker(new ArrayList<String>(depthsOfUrls.get(x)), new UrlParserImpl(internalOnly));
            Set<String> newUrls = new HashSet<String>(pool.invoke(parser));
            if (newUrls.isEmpty()) {
                throw new IllegalArgumentException("Initial links are invalid");
            }
            depthsOfUrls.add(newUrls);
        }

        pool.shutdown();

        return mergeResultsFromAllDepths(depthsOfUrls);
    }

    private void checkDepth() {
        final int maxDepth = 2;
        if (depth > maxDepth){
            throw new IllegalArgumentException("Depth could not be > " + maxDepth);
        }
    }

    private void addInitialLinks(List<Set<String>> urls) {
        urls.add(initialLinks);
    }

    private Set<String> mergeResultsFromAllDepths(List<Set<String>> urls) {
        Set<String> finalSet = new HashSet<String>();
        for (Set<String> each : urls){
            finalSet.addAll(each);
        }
        return finalSet;
    }
}
