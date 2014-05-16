package mobi.wrt.oreader.app.html;

import org.jsoup.nodes.Element;

public class HtmlElement {

    private Element element;

    private String text;

    public HtmlElement(Element element, String text) {
        this.element = element;
        this.text = text;
    }
}
