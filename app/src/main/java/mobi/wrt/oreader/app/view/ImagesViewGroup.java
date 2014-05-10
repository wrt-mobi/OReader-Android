package mobi.wrt.oreader.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import mobi.wrt.oreader.app.R;
import mobi.wrt.oreader.app.image.Displayers;
import mobi.wrt.oreader.app.image.IContentImage;

/**
 * Created by Uladzimir_Klyshevich on 5/6/2014.
 */
public class ImagesViewGroup extends RelativeLayout {

    public static enum DisplayMode {
        FULL, CROP;
    }

    private List<IContentImage> mImages;

    private int mLastWidth = -1;

    private DisplayMode mDisplayMode = DisplayMode.FULL;

    public ImagesViewGroup(Context context) {
        super(context);
    }

    public ImagesViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImagesViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDisplayMode(DisplayMode displayMode) {
        if (mDisplayMode != displayMode) {
            mDisplayMode = displayMode;
        }
        if (mLastWidth == -1) {
            return;
        }
        refresh(mLastWidth);
    }

    public void setSrc(List<IContentImage> images) {
        if (mImages == images) {
            return;
        }
        mImages = images;
        if (mLastWidth == -1) {
            return;
        }
        refresh(mLastWidth);
    }

    private void checkWidthAndRefresh(int width) {
        if (mLastWidth == width) {
            return;
        }
        mLastWidth = width;
        refresh(width);
    }
    private static LayoutParams DEFAULT_LAYOUT_PARAMS = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    private void refresh(int w) {
        removeAllViews();
        if (mImages == null) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (layoutParams != DEFAULT_LAYOUT_PARAMS) {
                setLayoutParams(DEFAULT_LAYOUT_PARAMS);
            }
            return;
        }
        int containerWidth = w;
        if (containerWidth == 0) {
            return;
        }
        for (int i = 0; i < mImages.size(); i++) {
            IContentImage contentImage = mImages.get(i);
            ImageView imageView = new ImageView(getContext());
            LayoutParams params = null;
            String url = contentImage.getUrl();
            ViewGroup thumbnailContainer = null;
            if (i == 0) {
                params = initBigImage(containerWidth, contentImage, imageView);
                addView(imageView, params);
            } else {
                //TODO remove when will multi image support
                if (true) {
                    return;
                }
                if (thumbnailContainer == null) {
                    LinearLayout linearLayout = new LinearLayout(getContext());
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getContext().getResources().getDimensionPixelSize(R.dimen.image_thumbs_height));
                    layoutParams.addRule(ALIGN_PARENT_BOTTOM);
                    thumbnailContainer = linearLayout;
                    addView(thumbnailContainer);
                }
                params = new LayoutParams(containerWidth/(mImages.size()-2), ViewGroup.LayoutParams.MATCH_PARENT);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(params);
                thumbnailContainer.addView(imageView);
            }
            ImageLoader.getInstance().displayImage(url, imageView, Displayers.BITMAP_DISPLAYER_OPTIONS);
        }
    }

    private LayoutParams initBigImage(int containerWidth, IContentImage contentImage, ImageView imageView) {
        LayoutParams params;
        if (mDisplayMode == DisplayMode.FULL) {
            params = initFirstImageForFullDisplayMode(containerWidth, contentImage, imageView);
        } else {
            params = initFirstImageForCropDisplayMode(containerWidth, contentImage, imageView);
        }
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = params.width;
        layoutParams.height = params.height;
        setLayoutParams(layoutParams);
        return params;
    }

    private LayoutParams initFirstImageForFullDisplayMode(int containerWidth, IContentImage contentImage, ImageView imageView) {
        LayoutParams params;
        Integer width = contentImage.getWidth();
        Integer height = contentImage.getHeight();
        if (width == null || height == null) {
            //make square image
            params = new LayoutParams(containerWidth, containerWidth);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            int imageHeight = (int) (((double) containerWidth * (double) height) / (double) width);
            params = new LayoutParams(containerWidth, imageHeight);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        return params;
    }

    private LayoutParams initFirstImageForCropDisplayMode(int containerWidth, IContentImage contentImage, ImageView imageView) {
        LayoutParams params = new LayoutParams(containerWidth, containerWidth/2);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return params;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        checkWidthAndRefresh(width);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        checkWidthAndRefresh(w);
    }
}
