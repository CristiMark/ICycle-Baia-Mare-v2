package vyrus.bikegps.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import vyrus.bikegps.R;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class LoadingActivity extends Activity implements TextureView.SurfaceTextureListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private static final String TAG = "LoadingActivity";

    private MediaPlayer loadingScreenMediaPlayer;
    private TextureView videoHolder;

    @Override
    public boolean onError(final MediaPlayer mediaPlayer, final int what, final int extra) {
        return false;
    }

    @Override
    public void onCompletion(final MediaPlayer mediaPlayer) {
        done();
    }

    public void done() {
        final Intent intent = new Intent(this, SponsorActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSurfaceTextureAvailable(final SurfaceTexture surface, final int width, final int height) {
        final Surface surfaceTexture = new Surface(surface);
        this.loadingScreenMediaPlayer = new MediaPlayer();

        try {
            this.loadingScreenMediaPlayer.setSurface(surfaceTexture);
            this.loadingScreenMediaPlayer.setLooping(false);
            this.loadingScreenMediaPlayer.prepareAsync();
            this.loadingScreenMediaPlayer.setOnErrorListener(this);
            this.loadingScreenMediaPlayer.setOnPreparedListener(new C04751());
            this.loadingScreenMediaPlayer.setOnCompletionListener(this);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(final SurfaceTexture surface, final int width, final int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(final SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(final SurfaceTexture surface) {
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        videoHolder = (TextureView) findViewById(R.id.texture_view);
        this.videoHolder.setSurfaceTextureListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.loadingScreenMediaPlayer != null) {
            this.loadingScreenMediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.loadingScreenMediaPlayer != null) {
            this.loadingScreenMediaPlayer.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.loadingScreenMediaPlayer != null) {
            this.loadingScreenMediaPlayer.stop();
            this.loadingScreenMediaPlayer.release();
            this.loadingScreenMediaPlayer = null;
        }
    }

    class C04751 implements MediaPlayer.OnPreparedListener {

        public void onPrepared(final MediaPlayer mediaPlayer) {
            final int videoWidth = mediaPlayer.getVideoWidth();
            final int videoHeight = mediaPlayer.getVideoHeight();
            final float videoProportion = (float) videoWidth / (float) videoHeight;
            final int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
            final int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
            final float screenProportion = (float) screenWidth / (float) screenHeight;
            android.view.ViewGroup.LayoutParams layoutParams = videoHolder.getLayoutParams();

            if (videoProportion > screenProportion) {
                layoutParams.width = screenWidth;
                layoutParams.height = (int) ((float) screenWidth / videoProportion);
            } else {
                layoutParams.width = (int) (videoProportion * (float) screenHeight);
                layoutParams.height = screenHeight;
            }
            videoHolder.setLayoutParams(layoutParams);

            if (!loadingScreenMediaPlayer.isPlaying()) {
                LoadingActivity.this.loadingScreenMediaPlayer.start();
            }
            videoHolder.setClickable(true);
        }
    }
}
