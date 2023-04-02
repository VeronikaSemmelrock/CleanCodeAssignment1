import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to WebCrawler. Please enter a URL that should be crawled, the depth of websites to crawl, and the target language! " +
                "Please enter the arguments in the format {URL};{depth};{targetLanguage}");
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();

        while (!verifyUserInput(userInput)) {
            System.out.println("Please enter correct arguments!");
            userInput = scanner.nextLine();
        }
        WebCrawler webCrawler = new WebCrawler(new WebCrawlerConfiguration(userInput.split(";")));
        webCrawler.run();
    }

    private static boolean verifyUserInput(String userInput) {
        return WebCrawlerConfiguration.isValidConfiguration(userInput.split(";"));
    }
}
