package vyrus.bikegps;

import android.app.Activity;
import android.graphics.Point;
import android.view.View;

import com.github.amlcurran.showcaseview.targets.Target;

/**
 * Created by Cristi Mark on 02.04.2018.
 */

public class ViewTarget implements Target {

    private final View targetView;

    public ViewTarget(final View view) {
        targetView = view;
    }

    public ViewTarget(final int viewId, final Activity activity) {
        targetView = activity.findViewById(viewId);
    }

    @Override
    public Point getPoint() {
        final int[] location = new int[2];
        targetView.getLocationInWindow(location);
        final int x = location[0] + targetView.getWidth() / 2;
        final int y = location[1] + targetView.getHeight() / 2;
        return new Point(x, y);
    }
}
