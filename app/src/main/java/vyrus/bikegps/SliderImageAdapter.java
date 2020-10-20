package vyrus.bikegps;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cristi Mark on 08.05.2017.
 */

public class SliderImageAdapter extends PagerAdapter {
    private Context sliderImageContext;
    private List<Bitmap> sliderImagesLinks = new ArrayList<>();


    public SliderImageAdapter(final Context context) {
        this.sliderImageContext = context;
    }

    @Override
    public int getCount() {
        return sliderImagesLinks.size();
    }

    public void setImages(final List<Bitmap> picture) {
        sliderImagesLinks = picture;
    }


    @Override
    public boolean isViewFromObject(final View v, final Object obj) {
        return v == obj;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int i) {
        final ImageView imageView = new ImageView(sliderImageContext);
        Glide.with(sliderImageContext)
                .load(sliderImagesLinks.get(i))
                .into(imageView);
        container.addView(imageView, 0);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PhotoFullPopupWindow(sliderImageContext, R.layout.popup_photo_full, v, sliderImagesLinks.get(i));
            }
        });
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull final ViewGroup container, final int i, @NonNull final Object obj) {
        container.removeView((ImageView) obj);
    }
}
