package vyrus.bikegps;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;

public class PhotoFullPopupWindow extends PopupWindow {

    public PhotoFullPopupWindow(Context context, int layout, View v, Bitmap imageUrl) {
        super(((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.popup_photo_full, null),
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        final View view = getContentView();
        final ImageButton closeButton = view.findViewById(R.id.ib_close);
        setOutsideTouchable(true);

        setFocusable(true);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final PhotoView photoView = view.findViewById(R.id.image_photo_view);
        photoView.setMaximumScale(6);

        Glide.with(context)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(photoView);

        showAtLocation(v, Gravity.CENTER, 0, 0);
    }
}
