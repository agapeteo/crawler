import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class CrawlerImplTest {
    @Test
    public void testCrawl() throws Exception {
        //givem
        final int depth = 1;
        final Set<String> initialLinks = new HashSet<String>();
            initialLinks.add("https://code.google.com/p/lightcrawler/");
        final boolean internalOnly = true;
        final Set<String> actual = new HashSet<String>();;
            actual.add("https://code.google.com/projecthosting/terms.html");
            actual.add("https://code.google.com/p/lightcrawler/");
            actual.add("http://code.google.com/projecthosting/");
            actual.add("https://code.google.com/u/xjlink@gmail.com/");
            actual.add("https://code.google.com/hosting/search?q=label:Crawler");
            actual.add("https://code.google.com/hosting/search?q=label:OpenSource");
            actual.add("https://code.google.com/p/lightcrawler/people/list");
            actual.add("https://code.google.com/p/lightcrawler/feeds");
            actual.add("https://code.google.com/p/lightcrawler/source/checkout");
            actual.add("https://code.google.com/hosting/search?q=label:RSS");
            actual.add("https://code.google.com/hosting/search?q=label:Web");
            actual.add("https://code.google.com/hosting/search?q=label:Multi-Thread");
            actual.add("https://code.google.com/hosting/search?q=label:Java");
            actual.add("https://code.google.com/p/lightcrawler/w/list");
            actual.add("https://code.google.com/p/lightcrawler/issues/list");
            actual.add("https://code.google.com/p/lightcrawler/downloads/list");
            actual.add("https://code.google.com/p/support/");

        //when
        CrawlerImpl crawler = new CrawlerImpl(depth, internalOnly, initialLinks);
        Set<String> expected = crawler.crawl();

        //then
        Assert.assertEquals(actual.size(), expected.size());
    }

    @Test(expected = StackOverflowError.class)
    public void testCrawl_BadLinkAndDepthIsOne() {
        // given
        final int depth = 1;
        final Set<String> initialLinks = new HashSet<String>();
            initialLinks.add("https://i.ua");
        final boolean internalOnly = true;

        //when
        CrawlerImpl crawler = new CrawlerImpl(depth, internalOnly, initialLinks);
        crawler.crawl();

        //then
        //expected exception
    }

    @Test(expected = StackOverflowError.class)
    public void testCrawl_BadLinkAndDepthIsTwo() {
        // given
        final int depth = 2;
        final Set<String> initialLinks = new HashSet<String>();
            initialLinks.add("https://i.ua");
        final boolean internalOnly = false;

        //when
        CrawlerImpl crawler = new CrawlerImpl(depth, internalOnly, initialLinks);
        crawler.crawl();

        //then
        //expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrawl_EmptyRequest() {
        // given
        final int depth = 2;
        final Set<String> initialLinks = new HashSet<String>();
            initialLinks.add("");
        final boolean internalOnly = false;

        //when
        CrawlerImpl crawler = new CrawlerImpl(depth, internalOnly, initialLinks);
        crawler.crawl();

        //then
        //expected exception
    }
}