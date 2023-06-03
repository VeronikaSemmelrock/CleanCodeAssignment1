package webcrawler;

import translatorService.TranslatorService;
import websiteService.crawledDocument.CrawledDocument;
import websiteService.crawledDocument.Heading;
import websiteService.httpConnector.HttpConnectorException;
import websiteService.WebsiteService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler {

    private final int maxDepth;
    private final String targetLanguage;

    private TranslatorService translatorService;
    private WebsiteService websiteService;

    public WebCrawler(int maxDepth, String targetLanguage) {
        this.maxDepth = maxDepth;
        this.targetLanguage = targetLanguage;
    }

    public void run(List<String> urls) {

        WebCrawlerScheduler webCrawlerScheduler = new WebCrawlerScheduler();

        for (String url : urls) {
            webCrawlerScheduler.submit(() -> crawlConfiguration(url, 0), url);
        }

        String resultOfChildren = webCrawlerScheduler.getResult();

        webCrawlerScheduler.shutdown();
        WebCrawlerFileWriter.writeToFile(resultOfChildren);
    }

    private String crawlConfiguration(String url, int depth) {
        System.out.println(Thread.currentThread().getName() + " is crawling " + url + " with depth " + depth);

        try {
            CrawledDocument document = websiteService.getWebsite(url);
            return crawlDocumentWithConfiguration(document, url, depth);
        } catch (HttpConnectorException e) {
            return handleBrokenLink(url, depth);
        }
    }

    private String crawlDocumentWithConfiguration(CrawledDocument document, String url, int depth) {
        if (depth == 0) {
            WebCrawlerReportBuilder.sourceLanguage = document.getSourceLanguage();
        }

        WebCrawlerScheduler webCrawlerScheduler = new WebCrawlerScheduler();

        Set<String> links = document.getLinks();
        if (depth < maxDepth) {
            for (String link : links) {
                if (isValidURL(link)) {
                    webCrawlerScheduler.submit(() -> crawlConfiguration(link, depth + 1), link);
                }
            }
        }

        List<Heading> translatedHeadings = translateHeadings(document.getHeadings());
        WebCrawlerResult webCrawlerResult = new WebCrawlerResult(depth, url, translatedHeadings);

        StringBuilder resultingReport = new StringBuilder();
        resultingReport.append(getCrawlerResultAsReport(webCrawlerResult));

        String resultOfChildren = webCrawlerScheduler.getResult();
        resultingReport.append(resultOfChildren);

        return resultingReport.toString();
    }

    private String handleBrokenLink(String url, int depth) {
        return WebCrawlerReportBuilder.getCrawlerResultAsBrokenLinkAtDepth(url, depth);
    }

    private List<Heading> translateHeadings(List<Heading> headings) {
        List<Heading> translatedHeadings = new ArrayList<>();
        try {
            for (Heading heading : headings) {
                Heading translatedHeading = new Heading(translatorService.translate(heading.getText(), targetLanguage), heading.getIndent());
                translatedHeadings.add(translatedHeading);
            }
        } catch (Exception e) {
            return getHeadingsWithErrorMessage(headings);
        }
        return translatedHeadings;
    }

    private List<Heading> getHeadingsWithErrorMessage(List<Heading> headings) {
        for (Heading heading : headings) {
            heading.setText(heading.getText() + " (Not translated because of translation error)");
        }
        return headings;
    }

    private String getCrawlerResultAsReport(WebCrawlerResult result) {
        if (result.getDepth() == 0) {
            return WebCrawlerReportBuilder.getCrawlerResultAsBaseReport(result, maxDepth, targetLanguage);
        } else {
            return WebCrawlerReportBuilder.getCrawlerResultAsNestedReport(result);
        }
    }

    public void setWebsiteService(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    public void setTranslatorService(TranslatorService translatorService) {
        this.translatorService = translatorService;
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
