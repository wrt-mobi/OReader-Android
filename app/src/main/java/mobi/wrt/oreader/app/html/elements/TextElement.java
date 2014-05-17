package mobi.wrt.oreader.app.html.elements;

import android.text.Html;
import android.text.Spanned;

public class TextElement extends PageElement {

    private Spanned text;

    public TextElement(String text) {
        this.text = Html.fromHtml(text);
    }

    public Spanned getText() {
        return text;
    }

}
