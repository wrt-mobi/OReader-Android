package mobi.wrt.oreader.app.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.paveldudka.util.FastBlur;

import java.util.HashMap;
import java.util.Set;

public class Displayers {

    public static DisplayImageOptions BITMAP_DISPLAYER_OPTIONS = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .delayBeforeLoading(300)
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .displayer(new SimpleBitmapDisplayer())
            .build();

    public static DisplayImageOptions BITMAP_DISPLAYER_BLUR_OPTIONS = new DisplayImageOptions.Builder().cloneFrom(BITMAP_DISPLAYER_OPTIONS)
            .preProcessor(new BitmapProcessor() {
                @Override
                public Bitmap process(Bitmap bitmap) {
                    if (bitmap == null) {
                        return null;
                    }
                    return FastBlur.doBlur(bitmap, 50, false);
                }
            })
            .build();

    public static DisplayImageOptions BITMAP_DISPLAYER_ICON_BG = new DisplayImageOptions.Builder().cloneFrom(BITMAP_DISPLAYER_OPTIONS)
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .preProcessor(new BitmapProcessor() {
                @Override
                public Bitmap process(Bitmap bitmap) {
                    if (bitmap == null) {
                        return null;
                    }
                    HashMap<Integer, Integer> colors = new HashMap<Integer, Integer>();
                    for (int i = 0; i < bitmap.getHeight(); i++) {
                        for (int j = 0; j < bitmap.getWidth(); j++) {
                            int pixel = bitmap.getPixel(i, j);
                            if (pixel == 0x00000000) {
                                continue;
                            }
                            Integer color = colors.get(pixel);
                            if (color == null) {
                                colors.put(pixel, 0);
                            } else {
                                colors.put(pixel, ++color);
                            }
                        }
                    }
                    Set<Integer> integers = colors.keySet();
                    Integer topColor = null;
                    Integer topColorCount = null;
                    for (Integer color : integers) {
                        Integer count = colors.get(color);
                        if (topColor == null) {
                            topColor = color;
                            topColorCount = count;
                        } else if (count > topColorCount) {
                            topColor = color;
                            topColorCount = count;
                        }
                    }
                    Bitmap image = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(image);
                    Paint paint = new Paint();
                    paint.setColor(topColor);
                    canvas.drawRect(new Rect(0, 0, 2, 2), paint);
                    return image;
                }
            })
            .build();
}
