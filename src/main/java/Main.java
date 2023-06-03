import translatorService.TextTranslator2TranslatorService;
import translatorService.TranslatorService;
import webcrawler.WebCrawler;
import webcrawler.WebCrawlerScheduler;
import websiteService.httpConnector.JsoupHttpConnector;
import websiteService.WebsiteService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final int argCountBeforeUrlArgs = 1;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            System.out.println("Welcome to WebCrawler. Please enter the depth of websites to crawl, the target language and a list of URLs that should be crawled! " +
                    "Please enter the arguments in the format {depth};{targetLanguage};{URL};{URL};{URL};...{URL}");

            TranslatorService translatorService = new TextTranslator2TranslatorService();
            String validUserInput = getValidInputViaUserInteraction(scanner.nextLine(), translatorService);
            String[] validUserInputArgs = validUserInput.split(";");

            WebCrawlerScheduler.initializeThreadPoolWithThreadCount(300);
            WebCrawler webCrawler = new WebCrawler(Integer.parseInt(validUserInputArgs[0]), validUserInputArgs[1]);
            WebsiteService websiteService = new WebsiteService(new JsoupHttpConnector());
            webCrawler.setWebsiteService(websiteService);
            webCrawler.setTranslatorService(translatorService);

            webCrawler.run(extractUrlsFromUserInputArgs(validUserInputArgs));
        } finally {
            scanner.close();
        }
    }

    private static String getValidInputViaUserInteraction(String userInput, TranslatorService translatorService) {
        while (!verifyUserInput(userInput, translatorService)) {
            System.out.println("Please enter correct arguments in the format {depth};{targetLanguage};{URL};{URL};{URL};...{URL}!");
            userInput = scanner.nextLine();
        }
        return userInput;
    }

    private static boolean verifyUserInput(String userInput, TranslatorService translatorService) {
        try {
            String[] userInputArgs = userInput.split(";");
            List<String> urls = extractUrlsFromUserInputArgs(userInputArgs);
            return isValidConfiguration(Integer.parseInt(userInputArgs[0]), userInputArgs[1], urls, translatorService);
        } catch (Exception e) {
            return false;
        }
    }

    private static List<String> extractUrlsFromUserInputArgs(String[] userInputArgs) {
        List<String> urls = new ArrayList<>();
        for (int i = 0; i < userInputArgs.length; i++) {
            if (i > argCountBeforeUrlArgs) {
                urls.add(userInputArgs[i]);
            }
        }
        return urls;
    }

    public static boolean isValidConfiguration(int depth, String language, List<String> urls, TranslatorService translatorService) {
        return WebCrawler.areValidURLs(urls) && WebCrawler.isValidDepth(depth) && translatorService.isValidLanguage(language);
    }

}
