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
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.teamtreehouse.albumcover.transitions.Fold;
import com.teamtreehouse.albumcover.transitions.Scale;

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

    private TransitionManager mTransitionManager;
    private Scene mExpandedScene;
    private Scene mCollapsedScene;
    private Scene mCurrentScene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        ButterKnife.bind(this);
        populate();
        setupTransitions();
    }

    // Custom transition (replaces animate() from property animation)
    private Transition createTransition() {
        TransitionSet set = new TransitionSet();
        set.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        Transition tFab = new Scale();
        tFab.setDuration(150);
        tFab.addTarget(fab);

        Transition tTitle = new Fold();
        tTitle.setDuration(150);
        tTitle.addTarget(titlePanel);

        Transition tTrack = new Fold();
        tTrack.setDuration(150);
        tTrack.addTarget(trackPanel);

        set.addTransition(tTrack);
        set.addTransition(tTitle);
        set.addTransition(tFab);
        return set;
    }

    @OnClick(R.id.album_art)
    public void onAlbumArtClick(View view) {
        Log.d("CustomAnimation","onAlbumArtClick()");
        int visibility=View.INVISIBLE;
        if(titlePanel.getVisibility()==View.INVISIBLE)
            visibility=View.VISIBLE;
//        animate();
        Transition transition = createTransition();

        // beginDelayedTransition forces TransitionManager to capture changes to Views
        // in detailContainer and apply according transitions from "transition"
        // when changes to the Views happen
        TransitionManager.beginDelayedTransition(detailContainer, transition);

        // these changes will be captured and transition animations will be played according to their targets
        fab.setVisibility(visibility);
        trackPanel.setVisibility(visibility);
        titlePanel.setVisibility(visibility);
    }

    /**
     * depending on the current scene transition to another
     * @param view
     */
    @OnClick(R.id.track_panel)
    public void onTrackPanelClicked(View view) {
        if (mCurrentScene == mExpandedScene) {
            mCurrentScene = mCollapsedScene;
        }
        else {
            mCurrentScene = mExpandedScene;
        }
        // change to the given scene
        mTransitionManager.transitionTo(mCurrentScene);
        // will refer to these lines from setupTransitions() :
        // mTransitionManager.setTransition(mExpandedScene, mCollapsedScene, collapseTransitionSet);
        // mTransitionManager.setTransition(mCollapsedScene, mExpandedScene, expandTransitionSet);
        // Thus according to the current scene and previous one, different Transition sets will be played

    }

    private void setupTransitions() {
        mTransitionManager = new TransitionManager();
        ViewGroup transitionRoot = detailContainer;

        /* Expanded scene */
        // create scene from XML layout
        mExpandedScene = Scene.getSceneForLayout(transitionRoot,
                R.layout.activity_album_detail_expanded, this);

        // setup a code, which will run when this scene is being entered
        mExpandedScene.setEnterAction(new Runnable() {
            // This code will be executed when this scene enters the screen
            @Override
            public void run() {
                Log.d("AlbumDetailsActivity","mExpandedScene.EnterAction");
                // all Views will be deleted, thus recreate them here
                ButterKnife.bind(AlbumDetailActivity.this);
                // we call populate() because after transition all Views will be deleted
                // thus we need to get references to them again
                // moreover album image and panel colors will also be reset to hardocded values
                populate();
                mCurrentScene = mExpandedScene;
            }
        });

        // choreograph transitions
        TransitionSet expandTransitionSet = new TransitionSet();
        expandTransitionSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
        // This transition captures the layout bounds of target views
        // before and after the scene change and animates those changes during the transition.
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(200);
        expandTransitionSet.addTransition(changeBounds);

        Fade fadeLyrics = new Fade();
        // set Fade animation to Lyrics TextView
        fadeLyrics.addTarget(R.id.lyrics);
        fadeLyrics.setDuration(150);
        expandTransitionSet.addTransition(fadeLyrics);
        // first changeBounds animation will be played for the whole scene
        // next Fade animation will be played for Lyrics TextView


        /* Collapsed scene */
        mCollapsedScene = Scene.getSceneForLayout(transitionRoot,
                R.layout.activity_album_detail, this);

        mCollapsedScene.setEnterAction(new Runnable() {
            @Override
            public void run() {
                Log.d("AlbumDetailsActivity","mCollapsedScene.EnterAction");
                ButterKnife.bind(AlbumDetailActivity.this);
                populate();
                mCurrentScene = mCollapsedScene;
            }
        });

        TransitionSet collapseTransitionSet = new TransitionSet();
        collapseTransitionSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        Fade fadeOutLyrics = new Fade();
        fadeOutLyrics.addTarget(R.id.lyrics);
        fadeOutLyrics.setDuration(150);
        collapseTransitionSet.addTransition(fadeOutLyrics);

        ChangeBounds resetBounds = new ChangeBounds();
        resetBounds.setDuration(200);
        collapseTransitionSet.addTransition(resetBounds);


        // from mExpandedScene to Scene mCollapsedScene play collapseTransitionSet
        mTransitionManager.setTransition(mExpandedScene, mCollapsedScene, collapseTransitionSet);
        mTransitionManager.setTransition(mCollapsedScene, mExpandedScene, expandTransitionSet);
        mCollapsedScene.enter();
    }



    private void populate() {
        Log.d("AlbumDetailsActivity","populate()");
        // setup album image
        int albumArtResId = getIntent().getIntExtra(EXTRA_ALBUM_ART_RESID, R.drawable.mean_something_kinder_than_wolves);
        albumArtView.setImageResource(albumArtResId);

        // colorize from the image
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
