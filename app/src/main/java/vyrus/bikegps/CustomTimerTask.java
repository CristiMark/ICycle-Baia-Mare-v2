package vyrus.bikegps;

import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.model.Marker;

import java.util.TimerTask;

/**
 * Created by Cristi Mark on 27.01.2018.
 */

public class CustomTimerTask extends TimerTask {
    private static final int DELAY_MILLIS = 16;
    private static final float FLOAT_ANCHOR_FIRST_POINT = 0.5f;

    private Marker marker;
    private Handler handler = new Handler();

    public CustomTimerTask(final Marker marker) {
        this.marker = marker;
    }

    @Override
    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        final Handler handlerSetAnchor = new Handler();
                        final long start = SystemClock.uptimeMillis();
                        final long duration = 1500;
                        final Interpolator interpolator = new BounceInterpolator();

                        handlerSetAnchor.post(new Runnable() {
                            @Override
                            public void run() {
                                final long elapsed = SystemClock.uptimeMillis() - start;
                                float t = Math.max(
                                        1 - interpolator.getInterpolation((float) elapsed
                                                / duration), 0);
                                marker.setAnchor(FLOAT_ANCHOR_FIRST_POINT, 1.0f + 2 * t);

                                if (t > 0.0) {
                                    handlerSetAnchor.postDelayed(this, DELAY_MILLIS);
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }
}
