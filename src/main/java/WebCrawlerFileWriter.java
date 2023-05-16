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

    public String writeCrawlerResultToFileAsBaseReport(WebCrawlerResult webCrawlerResult, String sourceLanguage) {
        WebCrawlerConfiguration webCrawlerConfiguration = webCrawlerResult.getWebCrawlerConfiguration();
        StringBuilder baseReport = new StringBuilder();
        baseReport.append("input: <a>" + webCrawlerConfiguration.getUrl() + "</a>\n");
        baseReport.append("<br>depth: " + webCrawlerConfiguration.getDepth() + "\n");
        baseReport.append("<br>source language: " + sourceLanguage + "\n");
        baseReport.append("<br>target language: " + webCrawlerConfiguration.getLanguage() + "\n");
        baseReport.append("<br>summary:" + "\n");

        baseReport.append(appendCrawlerResultHeadingsToFileAtDepth(webCrawlerResult, 0));
        return baseReport.toString();
    }

    public String writeCrawlerResultToFileAsNestedReport(WebCrawlerResult nestedWebCrawlerResult, int depth) {
        WebCrawlerConfiguration webCrawlerConfiguration = nestedWebCrawlerResult.getWebCrawlerConfiguration();
        String crawledUrl = webCrawlerConfiguration.getUrl();
        StringBuilder markdownCrawledUrl = new StringBuilder();
        markdownCrawledUrl.append("\n<br>");
        markdownCrawledUrl.append(getArrowRepresentationOfDepth(depth));
        markdownCrawledUrl.append(" link to <a>");
        markdownCrawledUrl.append(crawledUrl);
        markdownCrawledUrl.append("</a>\n");
        markdownCrawledUrl.append(appendCrawlerResultHeadingsToFileAtDepth(nestedWebCrawlerResult, depth));
        return markdownCrawledUrl.toString();
    }

    private String appendCrawlerResultHeadingsToFileAtDepth(WebCrawlerResult webCrawlerResult, int depth) {
        Elements headings = webCrawlerResult.getHeadings();
        StringBuilder stringBuilder = new StringBuilder();
        for (Element heading : headings) {
            stringBuilder.append(headingToMarkDownHeadingWithDepth(heading, depth));
        }
        return stringBuilder.toString();
    }

    public String writeCrawlerResultBrokenLinkToFileAtDepth(WebCrawlerConfiguration configuration, int depth) {
        StringBuilder brokenLink = new StringBuilder();
        brokenLink.append("\n");
        brokenLink.append(getArrowRepresentationOfDepth(depth));
        brokenLink.append(" broken link <a>" + configuration.getUrl() + "</a>\n");
        return brokenLink.toString();
    }

    private String headingToMarkDownHeadingWithDepth(Element heading, int depth) {
        StringBuilder markdownHeading = new StringBuilder();
        int headingLevel = Integer.parseInt(heading.tagName().substring(1));
        for (int i = 0; i < headingLevel; i++) {
            markdownHeading.append("#");
        }
        markdownHeading.append(getArrowRepresentationOfDepth(depth));
        markdownHeading.append("\s" + heading.text() + "\n");
        return markdownHeading.toString();
    }


    public void flush() {
        try (FileWriter fileWriter = new FileWriter(this.outputFile)) {
            fileWriter.write(this.outputFileContent.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getArrowRepresentationOfDepth(int depth) {
        StringBuilder arrow = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            arrow.append("--");
        }
        if (depth > 0) {
            arrow.append(">");
        }

        return arrow.toString();
    }
}
