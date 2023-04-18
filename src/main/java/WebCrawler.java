import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class WebCrawler {

    private final WebCrawlerConfiguration rootConfiguration;
    private final WebCrawlerFileWriter webCrawlerFileWriter;
    private final Set<String> crawledLinks;

    private String sourceLanguage;

    public WebCrawler(WebCrawlerConfiguration configuration) {
        this.rootConfiguration = configuration;
        this.webCrawlerFileWriter = new WebCrawlerFileWriter(new File("output.md"));
        this.crawledLinks = new HashSet<>();
    }

    public void run() {
        crawl(rootConfiguration);
        webCrawlerFileWriter.flush();
    }

    private void crawl(WebCrawlerConfiguration configuration) {
        System.out.println("Crawling " + configuration.getUrl() + " with depth " + configuration.getDepth());

        Website website = getWebsite(configuration);
        if (website != null) {
            if (isRootDepth(configuration)) {
                sourceLanguage = website.getSourceLanguage();
            }
            Elements translatedHeadings = translateHeadings(website.getHeadings());
            Set<String> links = website.getLinks();
            WebCrawlerResult result = new WebCrawlerResult(configuration, translatedHeadings, links);

            writeToFile(result, getCurrentDepth(configuration));
            crawlLinks(links, getCurrentDepth(configuration));
        }
    }

    private boolean isRootDepth(WebCrawlerConfiguration configuration) {
        return configuration == rootConfiguration;
    }

    private Website getWebsite(WebCrawlerConfiguration configuration) {
        String url = configuration.getUrl();
        crawledLinks.add(url);
        try {
            Document document = Jsoup.connect(url).get();
            return new Website(document);
        } catch (Exception e) {
            handleBrokenLink(configuration);
        }
        return null;
    }

    private void handleBrokenLink(WebCrawlerConfiguration configuration) {
        int currentDepth = getCurrentDepth(configuration);
        webCrawlerFileWriter.writeBrokenLinkReport(configuration, currentDepth);
    }

    private Elements translateHeadings(Elements headings) {
        Elements translatedHeadings = headings.clone();
        try {
            for (Element heading : headings) {
                Element translatedHeading = heading.html(Translator.translate(heading.ownText(), rootConfiguration.getLanguage()));
                translatedHeadings.add(translatedHeading);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something went wrong trying to translate the headings. The headings will be written to the file without translation!");
            return headings;
        }
        return translatedHeadings;
    }

    private void writeToFile(WebCrawlerResult result, int currentDepth) {
        if (currentDepth == 0) {
            webCrawlerFileWriter.writeBaseReport(result, sourceLanguage);
        } else {
            webCrawlerFileWriter.writeNestedReport(result, currentDepth);
        }
    }

    private void crawlLinks(Set<String> links, int currentDepth) {
        if (currentDepth <= 0) {
            return;
        }
        for (String link : links) {
            String[] configurationArgs = new String[3];
            configurationArgs[0] = link;
            configurationArgs[1] = String.valueOf(currentDepth - 1);
            configurationArgs[2] = rootConfiguration.getLanguage();
            if (isUnvisitedValidLink(link, configurationArgs)) {
                WebCrawlerConfiguration nestedConfiguration = new WebCrawlerConfiguration(configurationArgs);
                crawl(nestedConfiguration);
            }
        }
    }

    private int getCurrentDepth(WebCrawlerConfiguration configuration) {
        return rootConfiguration.getDepth() - configuration.getDepth();
    }

    private boolean isUnvisitedValidLink(String link, String[] configurationArgs) {
        return !crawledLinks.contains(link) && WebCrawlerConfiguration.isValidConfiguration(configurationArgs);
    }

}
