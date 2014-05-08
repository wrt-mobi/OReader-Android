package mobi.wrt.oreader.app.image;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.paveldudka.util.FastBlur;

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
                    return FastBlur.doBlur(bitmap, 50, false);
                }
            })
            .build();
}
