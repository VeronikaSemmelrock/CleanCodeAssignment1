import java.util.List;

public class WebCrawlerResultBuilder {

    public static String sourceLanguage;

    public static String getCrawlerResultAsBaseReport(WebCrawlerResult webCrawlerResult, int maxDepth, String targetLanguage) {
        StringBuilder baseReport = new StringBuilder();
        baseReport.append("input: <a>" + webCrawlerResult.getUrl() + "</a>\n");
        baseReport.append("<br>depth: " + maxDepth + "\n");
        baseReport.append("<br>source language: " + sourceLanguage + "\n");
        baseReport.append("<br>target language: " + targetLanguage + "\n");
        baseReport.append("<br>summary:" + "\n");

        baseReport.append(getCrawlerResultHeadingsAtDepth(webCrawlerResult, 0));
        return baseReport.toString();
    }

    public static String getCrawlerResultAsNestedReport(WebCrawlerResult nestedWebCrawlerResult) {
        String crawledUrl = nestedWebCrawlerResult.getUrl();
        StringBuilder markdownCrawledUrl = new StringBuilder();
        markdownCrawledUrl.append("\n<br>");
        markdownCrawledUrl.append(getArrowRepresentationOfDepth(nestedWebCrawlerResult.getDepth()));
        markdownCrawledUrl.append(" link to <a>");
        markdownCrawledUrl.append(crawledUrl);
        markdownCrawledUrl.append("</a>\n");
        markdownCrawledUrl.append(getCrawlerResultHeadingsAtDepth(nestedWebCrawlerResult, nestedWebCrawlerResult.getDepth()));
        return markdownCrawledUrl.toString();
    }

    private static String getCrawlerResultHeadingsAtDepth(WebCrawlerResult webCrawlerResult, int depth) {
        List<Heading> headings = webCrawlerResult.getHeadings();
        StringBuilder stringBuilder = new StringBuilder();
        for (Heading heading : headings) {
            stringBuilder.append(getHeadingAsMarkDownHeadingWithDepth(heading, depth));
        }
        return stringBuilder.toString();
    }

    public static String getCrawlerResultAsBrokenLinkAtDepth(String url, int depth) {
        StringBuilder brokenLink = new StringBuilder();
        brokenLink.append("\n");
        brokenLink.append(getArrowRepresentationOfDepth(depth));
        brokenLink.append(" broken link <a>" + url + "</a>\n");
        return brokenLink.toString();
    }

    private static String getHeadingAsMarkDownHeadingWithDepth(Heading heading, int depth) {
        StringBuilder markdownHeading = new StringBuilder();
        int headingLevel = heading.getIndent();
        for (int i = 0; i < headingLevel; i++) {
            markdownHeading.append("#");
        }
        markdownHeading.append(getArrowRepresentationOfDepth(depth));
        markdownHeading.append("\s" + heading.getText() + "\n");
        return markdownHeading.toString();
    }

    private static String getArrowRepresentationOfDepth(int depth) {
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
