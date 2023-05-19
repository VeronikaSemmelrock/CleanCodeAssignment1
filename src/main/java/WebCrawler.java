import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(300);

    private final int maxDepth;
    private final String targetLanguage;
    private final List<String> urls;

    private final WebCrawlerFileWriter webCrawlerFileWriter;


    private Translator translator;

    private String sourceLanguage;
    private WebsiteService websiteService;

    public WebCrawler(int maxDepth, String targetLanguage, List<String> urls) {
        this.maxDepth = maxDepth;
        this.targetLanguage = targetLanguage;
        this.urls = urls;
        this.webCrawlerFileWriter = new WebCrawlerFileWriter(new File("output.md"));
        this.websiteService = new WebsiteServiceImpl();
        this.translator = new Translator();
    }

    public String run() throws ExecutionException, InterruptedException {

        List<Future<String>> futures = new ArrayList<>();

        StringBuilder result = new StringBuilder();

        for(String url : urls) {
            Future<String> future = executorService.submit(() -> crawlConfiguration(url, 0));
            futures.add(future);
        }

        for(Future<String> future : futures) {
            String resultStr = future.get();
            printToConsole(resultStr);
            result.append(resultStr).append("\n\n");
        }

        System.out.println("result from main\n" + result);
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        return result.toString();
    }

    private synchronized void printToConsole(String s) {
        System.out.println(s);
    }

    private String crawlConfiguration(String url, int depth) throws ExecutionException, InterruptedException {
        System.out.println("Crawling " + url + " with depth " + depth);

        Website website = websiteService.getWebsite(url);
        if (website != null) {
            return crawlWebsiteWithConfiguration(website, url, depth);
        } else {
            return handleBrokenLink(url, depth);
        }
    }

    /**
     * This is a recursively called function that crawls the given website with the given configuration.
     * The configuration holds a depth via which the recursion is controlled, the URL of the website and the target language for the translation.
     *
//     * @param website
//     * @param configuration
     */
    private String crawlWebsiteWithConfiguration(Website website, String url, int depth) throws ExecutionException, InterruptedException {
        if (depth == 0) {
            sourceLanguage = website.getSourceLanguage();
        }
        Set<String> links = website.getLinks();

        List<Future<String>> futures = new ArrayList<>();
        if (depth < maxDepth) {
            for (String link : links) {
                if (isUnvisitedValidLink(link)) {
                    Future<String> future = executorService.submit(() -> crawlConfiguration(link, depth + 1));
                    futures.add(future);
                }
            }
        }

        Elements translatedHeadings = translateHeadings(website.getHeadings());
        WebCrawlerResult result = new WebCrawlerResult(depth, url, translatedHeadings);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(writeCrawlerResultToFileAtDepth(result));

        for (Future<String> future : futures) {
            stringBuilder.append(future.get());
        }
        System.out.println(Thread.currentThread().getName() + " generated result\n" + stringBuilder);
        return stringBuilder.toString();
    }

    private String handleBrokenLink(String url, int depth) {
        return webCrawlerFileWriter.writeCrawlerResultBrokenLinkToFileAtDepth(url, depth);
    }

    private Elements translateHeadings(Elements headings) {
        Elements translatedHeadings = new Elements();
        try {
            for (Element heading : headings) {
                Element translatedHeading = heading.html(translator.translate(heading.ownText(), targetLanguage));
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
            return webCrawlerFileWriter.writeCrawlerResultToFileAsBaseReport(result, maxDepth, targetLanguage, sourceLanguage);
        } else {
            return webCrawlerFileWriter.writeCrawlerResultToFileAsNestedReport(result);
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

    public static boolean isValidLanguage(String language) {
        return Translator.isValidLanguage(language);
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
