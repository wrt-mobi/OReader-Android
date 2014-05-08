package mobi.wrt.oreader.app.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

import mobi.wrt.oreader.app.R;

public class SymbolImageView extends FrameLayout implements ImageAware{

    public SymbolImageView(Context context) {
        super(context);
        init();
    }

    public SymbolImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SymbolImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        addView(View.inflate(
                getContext(), R.layout.view_symbol_image, null
        ));
    }

    @Override
    public ViewScaleType getScaleType() {
        return ViewScaleType.CROP;
    }

    @Override
    public View getWrappedView() {
        return findViewById(R.id.symbolBg);
    }

    @Override
    public boolean isCollected() {
        return false;
    }

    @Override
    public boolean setImageDrawable(Drawable drawable) {
        ((ImageView)findViewById(R.id.symbolBg)).setImageDrawable(drawable);
        if (drawable != null) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            updateTextColor(bitmap);
        }
        return true;
    }

    public void updateTextColor(Bitmap bitmap) {
        int pixel = bitmap.getPixel(0, 0);
        int green = Color.green(pixel);
        int blue = Color.blue(pixel);
        int red = Color.red(pixel);
        double y = 0.2126*red + 0.7152*green + 0.0722*blue;
        ((TextView)findViewById(R.id.symbol)).setTextColor(y < 128 ? Color.WHITE : Color.BLACK);
    }

    @Override
    public boolean setImageBitmap(Bitmap bitmap) {
        ((ImageView)findViewById(R.id.symbolBg)).setImageBitmap(bitmap);
        if (bitmap != null) {
            updateTextColor(bitmap);
        }
        return true;
    }

    public void setSymbol(String symbol) {
        ((TextView)findViewById(R.id.symbol)).setText(symbol);
    }

}
