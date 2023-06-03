package websiteService.crawledDocument;

public class Heading {

    private String text;
    private final int indent;

    public Heading(String text, int indent) {
        this.text = text;
        this.indent = indent;
    }

    public String getText() {
        return text;
    }

    public int getIndent() {
        return indent;
    }

    public void setText(String text) {
        this.text = text;
    }
}
