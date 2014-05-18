package mobi.wrt.oreader.app.html.elements;

import android.text.Html;
import android.text.Spanned;

public class TextElement extends PageElement {

    private Spanned text;

    public TextElement(String text) {
        while (text.startsWith("<br/>")) {
            text = text.substring(5);
        }
        while (text.endsWith("<br/>")) {
            text = text.substring(0, text.length() - 5);
        }
        this.text = Html.fromHtml(text);
    }

    public Spanned getText() {
        return text;
    }

}
