package mobi.wrt.oreader.app.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import org.jsoup.nodes.Element;

import java.util.List;

import by.istin.android.xcore.widget.XArrayAdapter;
import mobi.wrt.oreader.app.R;
import mobi.wrt.oreader.app.html.elements.MediaElement;
import mobi.wrt.oreader.app.html.elements.PageElement;
import mobi.wrt.oreader.app.html.elements.TextElement;

public class ArticleAdapter extends XArrayAdapter<PageElement> {

    private ListView listView;
    public ArticleAdapter(Context context, ListView listView, int resource, List<PageElement> objects) {
        super(context, resource, objects);
        this.listView = listView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        PageElement item = getItem(position);
        if (item instanceof TextElement) {
            return 0;
        } else {
            return 1;
        }
    }

    protected int getResource(int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == 0) {
            return R.layout.adapter_page_element_text;
        }
        return R.layout.adapter_page_element_image;
    }

    @Override
    protected void bindView(int position, PageElement item, View view, ViewGroup parent) {
        if (item instanceof TextElement) {
            ((TextView) view).setText(((TextElement)item).getText(), TextView.BufferType.SPANNABLE);
        } else {
            Element element = ((MediaElement) item).getElement();
            ImageView imageView = (ImageView) view;
            if (element.tag().getName().equalsIgnoreCase("img")) {
                ImageLoader.getInstance().displayImage(element.attr("src"), imageView, new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        if (resetImageView(view)) return;
                        super.onLoadingStarted(imageUri, view);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                        if (layoutParams == null) {
                            return;
                        }
                        int imageHeight = (int) (((double) listView.getWidth() * (double) loadedImage.getHeight()) / (double) loadedImage.getWidth());
                        layoutParams.width = listView.getWidth();
                        layoutParams.height = imageHeight;
                        view.setLayoutParams(layoutParams);
                    }

                });
            } else {
                resetImageView(view);
            }
        }

    }

    public boolean resetImageView(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            return true;
        }
        layoutParams.width = listView.getWidth();
        layoutParams.height = 0;
        view.setLayoutParams(layoutParams);
        ((ImageView)view).setImageBitmap(null);
        return false;
    }
}
