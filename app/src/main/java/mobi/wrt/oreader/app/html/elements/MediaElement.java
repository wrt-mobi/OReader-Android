package mobi.wrt.oreader.app.html.elements;

import org.jsoup.nodes.Element;

public class MediaElement extends PageElement {

    private Element element;

    public MediaElement(Element element) {
        this.element = element;
    }

    public Element getElement() {
        return element;
    }
}
