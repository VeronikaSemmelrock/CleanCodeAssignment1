import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WebCrawlerFileWriter {

    private final File outputFile;
    private final StringBuilder outputFileContent = new StringBuilder();

    public WebCrawlerFileWriter(File outputFile) {
        this.outputFile = outputFile;
    }

    public void setBaseReport(WebCrawlerResult webCrawlerResult) {
        WebCrawlerConfiguration webCrawlerConfiguration = webCrawlerResult.getWebCrawlerConfiguration();
        StringBuilder topLevelCrawledConfiguration = new StringBuilder();
        topLevelCrawledConfiguration.append("input: <a>")
                .append(webCrawlerConfiguration.getUrl())
                .append("</a>\n");
        topLevelCrawledConfiguration.append("<br>depth:")
                .append(webCrawlerConfiguration.getDepth())
                .append("\n");
        topLevelCrawledConfiguration.append("<br>source language:")
                .append("english")
                .append("\n");
        topLevelCrawledConfiguration.append("<br>target language:")
                .append(webCrawlerConfiguration.getLanguage())
                .append("\n");
        topLevelCrawledConfiguration.append("<br>summary:")
                .append("\n");
        outputFileContent.append(topLevelCrawledConfiguration);
        appendHeadings(webCrawlerResult, 0);
    }

    public void addNestedReport(WebCrawlerResult nestedWebCrawlerResult, int depth) {
        WebCrawlerConfiguration webCrawlerConfiguration = nestedWebCrawlerResult.getWebCrawlerConfiguration();
        String crawledUrl = webCrawlerConfiguration.getUrl();
        StringBuilder markdownCrawledUrl = new StringBuilder();
        markdownCrawledUrl.append("<br>");
        for (int i = 0; i < 2 * depth; i++) {
            markdownCrawledUrl.append("-");
        }
        if (depth > 0) {
            markdownCrawledUrl.append(">");
        }
        markdownCrawledUrl.append(" link to <a>");
        markdownCrawledUrl.append(crawledUrl);
        markdownCrawledUrl.append("</a>\n");
        outputFileContent.append(markdownCrawledUrl);
        appendHeadings(nestedWebCrawlerResult, depth);
    }

    private void appendHeadings(WebCrawlerResult webCrawlerResult, int depth) {
        Elements headings = webCrawlerResult.getHeadings();
        for (Element heading : headings) {
            outputFileContent.append(toMarkDownHeading(heading, depth));
        }
        outputFileContent.append("\n");
    }

    public void addBrokenLinkReport(WebCrawlerConfiguration configuration, int depth) {
        StringBuilder brokenLink = new StringBuilder();
        for (int i = 0; i < 2 * depth; i++) {
            brokenLink.append("-");
        }
        if (depth > 0) {
            brokenLink.append(">");
        }
        brokenLink.append(" broken link<a>").append(configuration.getUrl()).append("</a>").append("\n");
        outputFileContent.append(brokenLink);
    }

    private String toMarkDownHeading(Element heading, int depth) {
        StringBuilder markdownHeading = new StringBuilder();
        int headingLevel = Integer.parseInt(heading.tagName().substring(1));
        for (int i = 0; i < headingLevel; i++) {
            markdownHeading.append("#");
        }
        for (int i = 0; i < depth; i++) {
            markdownHeading.append("--");
        }
        if (depth > 0) {
            markdownHeading.append(">");
        }
        markdownHeading.append("\s");
        markdownHeading.append(heading.text());
        markdownHeading.append("\n");
        return markdownHeading.toString();
    }


    public void writeToOutputFile() {
        try (FileWriter fileWriter = new FileWriter(this.outputFile)) {
            fileWriter.write(this.outputFileContent.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
