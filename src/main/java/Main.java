import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    static ExecutorService executorService = Executors.newFixedThreadPool(5);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("Welcome to WebCrawler. Please enter a URL that should be crawled, the depth of websites to crawl, and the target language! " +
                "Please enter the arguments in the format {URL};{depth};{targetLanguage}");
//        String validUserInput = getValidInputViaUserInteraction(scanner.nextLine());
        List<String> urls = new ArrayList<>();
        urls.add("https://www.aau.at");

        List<Future<String>> futures = new ArrayList<>();

        StringBuilder result = new StringBuilder();
        String depth = 1 + "";
        String lang = "english";

        for(String url : urls) {
            WebCrawler webCrawler = new WebCrawler(new WebCrawlerConfiguration(new String[]{url, depth, lang}));
            Future<String> future = executorService.submit(() -> webCrawler.run());
            futures.add(future);
        }

        for(Future<String> future : futures) {
            result.append(future.get() + "\n\n");
        }

        System.out.println("result from main\n" + result);
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        scanner.close();
    }

    private static String getValidInputViaUserInteraction(String userInput) {
        while (!verifyUserInput(userInput)) {
            System.out.println("Please enter correct arguments in the format {URL};{depth};{targetLanguage}!");
            userInput = scanner.nextLine();
        }
        return userInput;
    }

    private static boolean verifyUserInput(String userInput) {
        return WebCrawlerConfiguration.isValidConfiguration(userInput.split(";"));
    }
}
