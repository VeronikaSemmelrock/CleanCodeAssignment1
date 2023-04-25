import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to WebCrawler. Please enter a URL that should be crawled, the depth of websites to crawl, and the target language! " +
                "Please enter the arguments in the format {URL};{depth};{targetLanguage}");
        String validUserInput = getValidInputViaUserInteraction(scanner.nextLine());
        WebCrawler webCrawler = new WebCrawler(new WebCrawlerConfiguration(validUserInput.split(";")));
        webCrawler.run();
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
