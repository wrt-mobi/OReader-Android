package mobi.wrt.oreader.app.view.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mobi.wrt.oreader.app.R;

public class SymbolViewUtils {

    public static final int DEFAULT_COLOR = 0xffCB4437;

    public static void updateTextColor(View view, Bitmap bitmap) {
        int pixel = bitmap.getPixel(0, 0);
        updateTextColor(view, pixel);
    }

    public static void updateTextColor(View view, int pixel) {
        if (view == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent == null) {
            return;
        }
        TextView symbolView = (TextView) parent.findViewById(R.id.symbol);
        if (symbolView == null) {
            return;
        }
        int green = Color.green(pixel);
        int blue = Color.blue(pixel);
        int red = Color.red(pixel);
        double y = 0.2126*red + 0.7152*green + 0.0722*blue;
        symbolView.setTextColor(y < 128 ? Color.WHITE : Color.BLACK);
    }

}
