import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class WebCrawler {

    private final WebCrawlerConfiguration rootConfiguration;
    private final WebCrawlerFileWriter webCrawlerFileWriter;
    private final Set<String> crawledLinks;

    public WebCrawler(WebCrawlerConfiguration configuration) {
        this.rootConfiguration = configuration;
        this.webCrawlerFileWriter = new WebCrawlerFileWriter(new File("output.md"));
        this.crawledLinks = new HashSet<>();
    }

    public void run() {
        run(rootConfiguration);
        webCrawlerFileWriter.writeToOutputFile();
    }

    private void run(WebCrawlerConfiguration configuration) {
        System.out.println("Crawling " + configuration.getUrl() + " with depth " + configuration.getDepth());

        Website website = getWebsite(configuration);
        if (website == null) {
            return;
        }
        WebCrawlerResult result = new WebCrawlerResult(configuration);
        Elements translatedHeadings = translateHeadings(website.getHeadings());
        result.setHeadings(translatedHeadings);
        Set<String> links = website.getLinks();
        result.setLinks(links);
        saveResult(result, configuration);
        processLinks(links, configuration);
    }

    private Website getWebsite(WebCrawlerConfiguration configuration) {
        String url = configuration.getUrl();
        crawledLinks.add(url);
        try {
            Document document = Jsoup.connect(url).get();
            return new Website(document);
        } catch (HttpStatusException e) {
            if (e.getStatusCode() == 404) {
                int currentDepth = getCurrentDepth(configuration);
                webCrawlerFileWriter.addBrokenLinkReport(configuration, currentDepth);
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return null;
    }

    private Elements translateHeadings(Elements headings) {
        // TODO
        return headings;
    }

    private void saveResult(WebCrawlerResult result, WebCrawlerConfiguration configuration) {
        int currentDepth = getCurrentDepth(configuration);
        if (currentDepth == 0) {
            webCrawlerFileWriter.setBaseReport(result);
        } else {
            webCrawlerFileWriter.addNestedReport(result, currentDepth);
        }
    }

    private void processLinks(Set<String> links, WebCrawlerConfiguration configuration) {
        if (configuration.getDepth() <= 0) {
            return;
        }
        for (String link : links) {
            String[] configurationArgs = new String[3];
            configurationArgs[0] = link;
            configurationArgs[1] = String.valueOf(configuration.getDepth() - 1);
            configurationArgs[2] = configuration.getLanguage();
            if (!crawledLinks.contains(link) && WebCrawlerConfiguration.isValidConfiguration(configurationArgs)) {
                WebCrawlerConfiguration nestedConfiguration = new WebCrawlerConfiguration(configurationArgs);
                run(nestedConfiguration);
            }
        }
    }

    private int getCurrentDepth(WebCrawlerConfiguration configuration) {
        return rootConfiguration.getDepth() - configuration.getDepth();
    }

}
