package com.teamtreehouse.albumcover;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AlbumDetailActivity extends Activity {

    public static final String EXTRA_ALBUM_ART_RESID = "EXTRA_ALBUM_ART_RESID";

    @Bind(R.id.album_art) ImageView albumArtView;
    @Bind(R.id.fab) ImageButton fab;
    @Bind(R.id.title_panel) ViewGroup titlePanel;
    @Bind(R.id.track_panel) ViewGroup trackPanel;
    @Bind(R.id.detail_container) ViewGroup detailContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        ButterKnife.bind(this);
        populate();
    }

    /**
     * Button will start as a tiny point and then scale to it's appropriate size
     */
    private void animate() {

        /* Play Button */
        // Approach 1
//        fab.setScaleX(0);
//        fab.setScaleY(0);
//        fab.animate().scaleX(1).scaleY(1).start();
        // Approach 2
//        ObjectAnimator scalex = ObjectAnimator.ofFloat(fab, "scaleX", 0, 1);
//        ObjectAnimator scaley = ObjectAnimator.ofFloat(fab, "scaleY", 0, 1);
//        AnimatorSet scaleFab = new AnimatorSet();
//        // play this set of animations together
//        scaleFab.playTogether(scalex, scaley);
//        // animation will last 0.25 seconds
//        scaleFab.setDuration(250);
        // We use XML animation approach here (animator.scale.xml)
        Animator scaleFab = AnimatorInflater.loadAnimator(this,R.animator.scale);
        scaleFab.setTarget(fab);

        /* Title and Tracks panels drop down */
        int titleStartValue = titlePanel.getTop();
        int titleEndValue = titlePanel.getBottom();
        ObjectAnimator animatorTitle = ObjectAnimator.ofInt(titlePanel,"bottom",titleStartValue,titleEndValue);
        animatorTitle.setInterpolator(new AccelerateInterpolator());
//        animatorTitle.setDuration(1000);
//        // animation will start after 0.5 seconds
//        animatorTitle.setStartDelay(500);

        int trackStartValue = trackPanel.getTop();
        int trackEndValue = trackPanel.getBottom();
        ObjectAnimator animatorTrack = ObjectAnimator.ofInt(trackPanel, "bottom", trackStartValue, trackEndValue);
        animatorTrack.setInterpolator(new DecelerateInterpolator());
//        animatorTrack.setDuration(1000);

        /* Set default values */
        fab.setScaleX(0);
        fab.setScaleY(0);
        titlePanel.setBottom(titleStartValue);
        trackPanel.setBottom(trackStartValue);

        /* Choreograph multiple animations */
        AnimatorSet set = new AnimatorSet();
        // play this set of animations sequentially
        set.playSequentially(animatorTitle, animatorTrack, scaleFab);
        set.start();
    }

    @OnClick(R.id.album_art)
    public void onAlbumArtClick(View view) {
        animate();
    }

    private void populate() {
        int albumArtResId = getIntent().getIntExtra(EXTRA_ALBUM_ART_RESID, R.drawable.mean_something_kinder_than_wolves);
        albumArtView.setImageResource(albumArtResId);

        Bitmap albumBitmap = getReducedBitmap(albumArtResId);
        colorizeFromImage(albumBitmap);
    }

    private Bitmap getReducedBitmap(int albumArtResId) {
        // reduce image size in memory to avoid memory errors
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 8;
        return BitmapFactory.decodeResource(getResources(), albumArtResId, options);
    }

    private void colorizeFromImage(Bitmap image) {
        Palette palette = Palette.from(image).generate();

        // set panel colors
        int defaultPanelColor = 0xFF808080;
        int defaultFabColor = 0xFFEEEEEE;
        titlePanel.setBackgroundColor(palette.getDarkVibrantColor(defaultPanelColor));
        trackPanel.setBackgroundColor(palette.getLightMutedColor(defaultPanelColor));

        // set fab colors
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled},
                new int[]{android.R.attr.state_pressed}
        };

        int[] colors = new int[]{
                palette.getVibrantColor(defaultFabColor),
                palette.getLightVibrantColor(defaultFabColor)
        };
        fab.setBackgroundTintList(new ColorStateList(states, colors));
    }
}
