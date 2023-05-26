import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Main {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("Welcome to WebCrawler. Please enter a URL that should be crawled, the depth of websites to crawl, and the target language! " +
                "Please enter the arguments in the format {URL};{depth};{targetLanguage}");
//        String validUserInput = getValidInputViaUserInteraction(scanner.nextLine());
        List<String> urls = new ArrayList<>();
        urls.add("https://www.aau.at");
//        urls.add("https://www.google.at");
//        urls.add("https://www.orf.at");
//        urls.add("https://www.cinecity.at");
        urls.add("https://www.neromylos.com");

        WebCrawler webCrawler = new WebCrawler(2, "english", urls);
        WebsiteService websiteService = new WebsiteService(new JsoupHttpConnector());
        webCrawler.setWebsiteService(websiteService);
        Translator translator = new TextTranslator2Translator();
        webCrawler.setTranslator(translator);

        webCrawler.run();
        scanner.close();
    }

    private static String getValidInputViaUserInteraction(String userInput, Translator translator) {
        while (!verifyUserInput(userInput, translator)) {
            System.out.println("Please enter correct arguments in the format {URL};{depth};{targetLanguage}!");
            userInput = scanner.nextLine();
        }
        return userInput;
    }

    private static boolean verifyUserInput(String userInput, Translator translator) {

        String[] userInputArgs = userInput.split(";");
        List<String> urls = new ArrayList<>();
        for (int i = 0; i < userInputArgs.length; i++) {
            if (i > 1) {
                urls.add(userInputArgs[i]);
            }
        }
        return isValidConfiguration(Integer.parseInt(userInputArgs[0]), userInputArgs[1], urls, translator);
    }

    public static boolean isValidConfiguration(int depth, String language, List<String> urls, Translator translator) {
        return WebCrawler.areValidURLs(urls) && WebCrawler.isValidDepth(depth) && translator.isValidLanguage(language);
    }

}
