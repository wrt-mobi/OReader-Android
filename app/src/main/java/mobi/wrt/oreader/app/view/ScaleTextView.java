package mobi.wrt.oreader.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import by.istin.android.xcore.utils.Log;
import mobi.wrt.oreader.app.R;

/**
 * Created by Uladzimir_Klyshevich on 5/8/2014.
 */
public class ScaleTextView extends TextView {

    private Paint textPaint = new Paint();

    private float initialTextSize;

    public ScaleTextView(Context context) {
        super(context);
        initialTextSize = getPaint().getTextSize();
    }

    public ScaleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialTextSize = getPaint().getTextSize();
    }

    public ScaleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialTextSize = getPaint().getTextSize();
    }

    /**
     * Resize the text so that it fits
     *
     * @param text      The text. Neither <code>null</code> nor empty.
     * @param textWidth The width of the TextView. > 0
     */
    private void refitText(String text, int textWidth) {
        if (textWidth <= 0 || text == null || text.length() == 0)
            return;

        // the width
        int targetWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();

        this.textPaint.set(this.getPaint());
        this.textPaint.setTextSize(initialTextSize);
        Rect rect = new Rect();
        textPaint.getTextBounds(text, 0, text.length()-1, rect);
        int height = getHeight();
        int k = height /rect.height();
        Log.xd(this, k + " "+getLineSpacingExtra() + " " + getLineSpacingMultiplier());
        while (rect.width()/k > targetWidth) {
            textPaint.setTextSize(textPaint.getTextSize()/1.3f);
            textPaint.getTextBounds(text, 0, text.length()-1, rect);
            k = height /rect.height();
            Log.xd(this, k + " ");
        }
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, textPaint.getTextSize());
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        this.refitText(text.toString(), this.getWidth());
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        this.refitText(text.toString(), this.getWidth());
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldwidth, int oldheight) {
        if (width != oldwidth) {
            this.refitText(this.getText().toString(), width);
        }
    }
}
