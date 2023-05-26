import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler {

    private final int maxDepth;
    private final String targetLanguage;
    private final List<String> urls;

    private final WebCrawlerResultBuilder webCrawlerResultBuilder;

    private Translator translator;

    private String sourceLanguage;
    private WebsiteService websiteService;

    public WebCrawler(int maxDepth, String targetLanguage, List<String> urls) {
        this.maxDepth = maxDepth;
        this.targetLanguage = targetLanguage;
        this.urls = Collections.unmodifiableList(urls);
        this.webCrawlerResultBuilder = new WebCrawlerResultBuilder();
    }

    public void run() {

        WebCrawlerScheduler webCrawlerScheduler = new WebCrawlerScheduler();

        for (String url : urls) {
            webCrawlerScheduler.submit(() -> crawlConfiguration(url, 0), url);
        }

        String result = webCrawlerScheduler.getScheduledResult();

        webCrawlerScheduler.shutdown();
        WebCrawlerFileWriter.writeToFile(result);
    }

    private String crawlConfiguration(String url, int depth) throws ExecutionException, InterruptedException, TimeoutException {
        System.out.println("Crawling " + url + " with depth " + depth);

        CrawledDocument website = websiteService.getWebsite(url);
        if (website != null) {
            return crawlWebsiteWithConfiguration(website, url, depth);
        } else {
            return handleBrokenLink(url, depth);
        }
    }

    /**
     * This is a recursively called function that crawls the given website with the given configuration.
     * The configuration holds a depth via which the recursion is controlled, the URL of the website and the target language for the translation.
     * <p>
     * //     * @param website
     * //     * @param configuration
     */
    private String crawlWebsiteWithConfiguration(CrawledDocument website, String url, int depth) throws ExecutionException, InterruptedException, TimeoutException {
        if (depth == 0) {
            sourceLanguage = website.getSourceLanguage();
        }

        WebCrawlerScheduler webCrawlerScheduler = new WebCrawlerScheduler();

        Set<String> links = website.getLinks();
        if (depth < maxDepth) {
            for (String link : links) {
                if (isUnvisitedValidLink(link)) {
                    webCrawlerScheduler.submit(() -> crawlConfiguration(link, depth + 1), link);
                }
            }
        }

        List<Heading> translatedHeadings = translateHeadings(website.getHeadings());
        WebCrawlerResult webCrawlerResult = new WebCrawlerResult(depth, url, translatedHeadings);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(writeCrawlerResultToFileAtDepth(webCrawlerResult));

        String result = webCrawlerScheduler.getScheduledResult();
        stringBuilder.append(result);

        System.out.println(Thread.currentThread().getName() + " generated result\n" + stringBuilder);
        return stringBuilder.toString();
    }

    private String handleBrokenLink(String url, int depth) {
        return webCrawlerResultBuilder.writeCrawlerResultBrokenLinkToFileAtDepth(url, depth);
    }

    private List<Heading> translateHeadings(List<Heading> headings) {
        List<Heading> translatedHeadings = new ArrayList<>();
        try {
            for (Heading heading : headings) {
                Heading translatedHeading = new Heading(translator.translate(heading.getText(), targetLanguage), heading.getIndent());
                translatedHeadings.add(translatedHeading);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something went wrong trying to translate the headings. The headings will be written to the file without translation!");
            return headings;
        }
        return translatedHeadings;
    }

    private String writeCrawlerResultToFileAtDepth(WebCrawlerResult result) {
        if (result.getDepth() == 0) {
            return webCrawlerResultBuilder.writeCrawlerResultToFileAsBaseReport(result, maxDepth, targetLanguage, sourceLanguage);
        } else {
            return webCrawlerResultBuilder.writeCrawlerResultToFileAsNestedReport(result);
        }
    }

    private boolean isUnvisitedValidLink(String url) {
        return isValidURL(url);
    }

    public void setWebsiteService(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    public void setTranslator(Translator translator) {
        this.translator = translator;
    }

    public static boolean isValidDepth(int inputDepth) {
        return inputDepth >= 0;
    }

    public static boolean isValidURL(String url) {
        Pattern pattern = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    public static boolean areValidURLs(List<String> urls) {
        for (String url : urls) {
            if (!isValidURL(url)) {
                return false;
            }
        }
        return true;
    }

}
