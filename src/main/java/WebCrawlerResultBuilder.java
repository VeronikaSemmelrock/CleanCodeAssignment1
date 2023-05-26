import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class WebCrawlerResultBuilder {

    public String writeCrawlerResultToFileAsBaseReport(WebCrawlerResult webCrawlerResult, int maxDepth, String targetLanguage, String sourceLanguage) {
        StringBuilder baseReport = new StringBuilder();
        baseReport.append("input: <a>" + webCrawlerResult.getUrl() + "</a>\n");
        baseReport.append("<br>depth: " + maxDepth + "\n");
        baseReport.append("<br>source language: " + sourceLanguage + "\n");
        baseReport.append("<br>target language: " + targetLanguage + "\n");
        baseReport.append("<br>summary:" + "\n");

        baseReport.append(appendCrawlerResultHeadingsToFileAtDepth(webCrawlerResult, 0));
        return baseReport.toString();
    }

    public String writeCrawlerResultToFileAsNestedReport(WebCrawlerResult nestedWebCrawlerResult) {
        String crawledUrl = nestedWebCrawlerResult.getUrl();
        StringBuilder markdownCrawledUrl = new StringBuilder();
        markdownCrawledUrl.append("\n<br>");
        markdownCrawledUrl.append(getArrowRepresentationOfDepth(nestedWebCrawlerResult.getDepth()));
        markdownCrawledUrl.append(" link to <a>");
        markdownCrawledUrl.append(crawledUrl);
        markdownCrawledUrl.append("</a>\n");
        markdownCrawledUrl.append(appendCrawlerResultHeadingsToFileAtDepth(nestedWebCrawlerResult, nestedWebCrawlerResult.getDepth()));
        return markdownCrawledUrl.toString();
    }

    private String appendCrawlerResultHeadingsToFileAtDepth(WebCrawlerResult webCrawlerResult, int depth) {
        List<Heading> headings = webCrawlerResult.getHeadings();
        StringBuilder stringBuilder = new StringBuilder();
        for (Heading heading : headings) {
            stringBuilder.append(headingToMarkDownHeadingWithDepth(heading, depth));
        }
        return stringBuilder.toString();
    }

    public String writeCrawlerResultBrokenLinkToFileAtDepth(String url, int depth) {
        StringBuilder brokenLink = new StringBuilder();
        brokenLink.append("\n");
        brokenLink.append(getArrowRepresentationOfDepth(depth));
        brokenLink.append(" broken link <a>" + url + "</a>\n");
        return brokenLink.toString();
    }

    private String headingToMarkDownHeadingWithDepth(Heading heading, int depth) {
        StringBuilder markdownHeading = new StringBuilder();
        int headingLevel = heading.getIndent();
        for (int i = 0; i < headingLevel; i++) {
            markdownHeading.append("#");
        }
        markdownHeading.append(getArrowRepresentationOfDepth(depth));
        markdownHeading.append("\s" + heading.getText() + "\n");
        return markdownHeading.toString();
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
