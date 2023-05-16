import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WebCrawler {

    private final WebCrawlerConfiguration rootConfiguration;
    private final WebCrawlerFileWriter webCrawlerFileWriter;
    private final Set<String> crawledLinks;

    static ExecutorService executorService = Executors.newFixedThreadPool(5);


    private StringBuilder stringBuilder = new StringBuilder();

    private Translator translator;

    private String sourceLanguage;
    private WebsiteService websiteService;

    public WebCrawler(WebCrawlerConfiguration configuration) {
        this.rootConfiguration = configuration;
        this.webCrawlerFileWriter = new WebCrawlerFileWriter(new File("output.md"));
        this.crawledLinks = new HashSet<>();
        this.websiteService = new WebsiteServiceImpl();
        this.translator = new Translator();
    }

//    public void run() {
//        crawlConfiguration(rootConfiguration);
//        webCrawlerFileWriter.flush();
//    }

    public String run() throws ExecutionException, InterruptedException {
        crawlConfiguration(rootConfiguration);
//        webCrawlerFileWriter.flush();
        return stringBuilder.toString();
    }

    private void crawlConfiguration(WebCrawlerConfiguration configuration) throws ExecutionException, InterruptedException {
        System.out.println("Crawling " + configuration.getUrl() + " with depth " + configuration.getDepth());

        Website website = websiteService.getWebsite(configuration);
        if (website != null) {
            crawlWebsiteWithConfiguration(website, configuration);
        } else {
            handleBrokenLink(configuration);
        }
    }

    /**
     * This is a recursively called function that crawls the given website with the given configuration.
     * The configuration holds a depth via which the recursion is controlled, the URL of the website and the target language for the translation.
     *
     * @param website
     * @param configuration
     */
    private void crawlWebsiteWithConfiguration(Website website, WebCrawlerConfiguration configuration) throws ExecutionException, InterruptedException {
        if (isRootConfiguration(configuration)) {
            sourceLanguage = website.getSourceLanguage();
        }
        crawledLinks.add(configuration.getUrl());
        Set<String> links = website.getLinks();

        List<Future<String>> futures = new ArrayList<>();
        if (configuration.getDepth() != 0) {
            for (String url : links) {
                WebCrawler webCrawler = new WebCrawler(new WebCrawlerConfiguration(new String[]{url, configuration.getDepth() - 1 + "", configuration.getLanguage()}));
                Future<String> future = executorService.submit(() -> webCrawler.run());
                futures.add(future);
            }
        }

        Elements translatedHeadings = translateHeadings(website.getHeadings());
        WebCrawlerResult result = new WebCrawlerResult(configuration, translatedHeadings);
        stringBuilder.append(writeCrawlerResultToFileAtDepth(result, getCurrentDepth(configuration)));

        for(Future<String> future : futures) {
            stringBuilder.append(future.get());
        }
    }

    private boolean isRootConfiguration(WebCrawlerConfiguration configuration) {
        return configuration == rootConfiguration;
    }

    private void handleBrokenLink(WebCrawlerConfiguration configuration) {
        int currentDepth = getCurrentDepth(configuration);
        webCrawlerFileWriter.writeCrawlerResultBrokenLinkToFileAtDepth(configuration, currentDepth);
    }

    private Elements translateHeadings(Elements headings) {
        Elements translatedHeadings = new Elements();
        try {
            for (Element heading : headings) {
                Element translatedHeading = heading.html(translator.translate(heading.ownText(), rootConfiguration.getLanguage()));
                translatedHeadings.add(translatedHeading);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something went wrong trying to translate the headings. The headings will be written to the file without translation!");
            return headings;
        }
        return translatedHeadings;
    }

    private String writeCrawlerResultToFileAtDepth(WebCrawlerResult result, int currentDepth) {
        if (currentDepth == 0) {
            return webCrawlerFileWriter.writeCrawlerResultToFileAsBaseReport(result, sourceLanguage);
        } else {
            return webCrawlerFileWriter.writeCrawlerResultToFileAsNestedReport(result, currentDepth);
        }
    }

//    private void crawlLinks(Set<String> links, int depth) {
//        if (depth <= 0) {
//            return;
//        }
//        for (String link : links) {
//            String[] configurationArgs = new String[3];
//            configurationArgs[0] = link;
//            configurationArgs[1] = String.valueOf(depth - 1);
//            configurationArgs[2] = rootConfiguration.getLanguage();
//            if (isUnvisitedValidLink(link, configurationArgs)) {
//                crawlUnvisitedValidLink(configurationArgs);
//            }
//        }
//    }
//
//    private void crawlUnvisitedValidLink(String[] configurationArgs) {
//        WebCrawlerConfiguration nestedConfiguration = new WebCrawlerConfiguration(configurationArgs);
//        crawlConfiguration(nestedConfiguration);
//    }

    private int getCurrentDepth(WebCrawlerConfiguration configuration) {
        return rootConfiguration.getDepth() - configuration.getDepth();
    }

    private boolean isUnvisitedValidLink(String link, String[] configurationArgs) {
        return !crawledLinks.contains(link) && WebCrawlerConfiguration.isValidConfiguration(configurationArgs);
    }

    public void setWebsiteService(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    public void setTranslator(Translator translator) {
        this.translator = translator;
    }
}
