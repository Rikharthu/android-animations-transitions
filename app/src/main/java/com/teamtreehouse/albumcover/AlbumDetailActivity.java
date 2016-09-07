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
import android.os.Handler;
import android.support.v7.graphics.Palette;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
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


    @OnClick(R.id.album_art)
    public void onAlbumArtClick(View view) {

        // Property animations
//        animate();

        // Transition Animations
        Transition transition = createTransition();

        TransitionManager.beginDelayedTransition(detailContainer, transition);
        fab.setVisibility(View.INVISIBLE);
        // hide title and track panel
        titlePanel.setVisibility(View.INVISIBLE);
        trackPanel.setVisibility(View.INVISIBLE);
    }

    /** Used to replace animate() method */
    private Transition createTransition() {
        TransitionSet set = new TransitionSet();
        set.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        // Our custom Transition
        Transition tFab = new Scale();
        // Configure transition
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

    /** Propery Animations */
    private void animate(){
        /* Button */
        // using animate() method
        // set button scale to 0
//        fab.setScaleX(0);
//        fab.setScaleY(0);
        // animate button to end scale (1=full scale)
//        fab.animate().scaleX(1).scaleY(1).start();
        // You can use ObjectAnimator for that too:
//        ObjectAnimator scalex=ObjectAnimator.ofFloat(fab,"scaleX",0,1);
//        ObjectAnimator scaley=ObjectAnimator.ofFloat(fab,"scaleY",0,1);
//        AnimatorSet scaleFab=new AnimatorSet();
//        scaleFab.playTogether(scalex,scaley);
//        scaleFab.start();
        // Also you can get Animator from XML file
        // get Animator from XML: res/animator/scale.xml
        Animator scaleFabXML = AnimatorInflater.loadAnimator(this,R.animator.scale);
        scaleFabXML.setTarget(fab);
        scaleFabXML.start();

        /* Panels */
        // animate slide-in effect
        // get start and end values
        int titleStartValue=titlePanel.getTop();
        int titleEndValue=titlePanel.getBottom();
        // ObjectAnimator let's animate any object's properties that we reference by names
        // target object, property name, start and end values
        ObjectAnimator animatorTitle = ObjectAnimator.ofInt(titlePanel, "bottom",titleStartValue, titleEndValue);
        // set interpolator to make animation accelerate/decelerate (values calculation)
        animatorTitle.setInterpolator(new AccelerateInterpolator());
        // set animation duration
        animatorTitle.setDuration(1000);

        // same for track panel
        int trackStartValue=trackPanel.getTop();
        int trackEndValue=trackPanel.getBottom();
        ObjectAnimator animatorTrack =  ObjectAnimator.ofInt(trackPanel, "bottom",trackStartValue, trackEndValue);
        animatorTrack.setInterpolator(new DecelerateInterpolator());
        animatorTitle.setDuration(1000);

        // make titlePanel and trackPanel to be hidden before playing animations
        titlePanel.setBottom(titleStartValue);
        trackPanel.setBottom(trackStartValue);

        // Choreograph animation using the AnimatorSet
        AnimatorSet set = new AnimatorSet();
        // make animations play one after another
        set.playSequentially(animatorTitle,animatorTrack);
        set.start();
    }

    @OnClick(R.id.track_panel)
    public void onTrackPanelClicked(View view) {
        // Keep track of the current scene and required scene
        if (mCurrentScene == mExpandedScene) {
            mCurrentScene = mCollapsedScene;
        }
        else {
            mCurrentScene = mExpandedScene;
        }

        // Start transition
        mTransitionManager.transitionTo(mCurrentScene);
    }

    private void setupTransitions() {
        // Set transition when this Activity starts with an intent
        /* Comment these lines to see, how transitions defined in XML (styles.xml) work
           on different sdk version */
//        getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
        Slide slide = new Slide(Gravity.BOTTOM);
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        getWindow().setEnterTransition(slide);
        // back button
//        getWindow().setReturnTransition(new Slide(Gravity.LEFT));
        // by default shared elements animations run on the overlay. disable this
        getWindow().setSharedElementsUseOverlay(false);


        mTransitionManager=new TransitionManager();

        // The root of the View hierarchy to run the transition on.
        ViewGroup transitionRoot = detailContainer;

        /* Expanded Scene */
        // create new scene
        mExpandedScene = Scene.getSceneForLayout(transitionRoot, // ViewGroup
                R.layout.activity_album_detail_expanded, // Target layout we want transition to
                this); // context

        // What happens when this transition occurs
        mExpandedScene.setEnterAction(new Runnable() {
            @Override
            public void run() {
                // Views are recreated => bind them again
                ButterKnife.bind(AlbumDetailActivity.this);
                // populate expanded scene with correct data
                populate();
            }
        });

        // Create a Transition Set for choreographing
        TransitionSet expandTransitionSet = new TransitionSet();

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(200);
        // add transition to the set
        expandTransitionSet.addTransition(changeBounds);

        Fade fadeLyrics = new Fade();
        fadeLyrics.setDuration(150);
        // assign target for Fade transition
        fadeLyrics.addTarget(R.id.lyrics);
        expandTransitionSet.addTransition(fadeLyrics);
        // Configure ordering
        expandTransitionSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        // Launch transition with current Set
//        TransitionManager.go(expandedScene,expandTransitionSet);
//        TransitionManager.go(expandedScene);
//        TransitionManager.go(expandedScene, new ChangeBounds());


        /* Collapsed Scene */
        // Same as for Expanded scene, but reverse animation ordering
        // create new scene
        mCollapsedScene = Scene.getSceneForLayout(transitionRoot, // ViewGroup
                R.layout.activity_album_detail, // Target layout we want transition to
                this); // context

        // What happens when this transition occurs
        mCollapsedScene.setEnterAction(new Runnable() {
            @Override
            public void run() {
                // Views are recreated => bind them again
                ButterKnife.bind(AlbumDetailActivity.this);
                // populate expanded scene with correct data
                populate();
            }
        });

        TransitionSet collapseTransitionSet = new TransitionSet();
        collapseTransitionSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        Fade fadeOutLyrics = new Fade();
        fadeOutLyrics.setDuration(150);
        fadeOutLyrics.addTarget(R.id.lyrics);
        collapseTransitionSet.addTransition(fadeOutLyrics);

        ChangeBounds resetBounds = new ChangeBounds();
        resetBounds.setDuration(200);
        collapseTransitionSet.addTransition(resetBounds);


        // Assign transitions
        // when transition from mExpandedScene to mCollapsedScene play collapseTransitionSet
        mTransitionManager.setTransition(mExpandedScene, mCollapsedScene,collapseTransitionSet);
        mTransitionManager.setTransition(mCollapsedScene, mExpandedScene,expandTransitionSet);

        // Switch to starter scene without any animation
        mCollapsedScene.enter();

        // prevent shared element transition from starting
        // to make sure that shared element is ready (liek image is loaded from the internet)
        postponeEnterTransition();
    }

    /** Populate album details with correct data:image, text,colors */
    private void populate() {
        // simulate latency
        // post this code to run after a small delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int albumArtResId = getIntent().getIntExtra(EXTRA_ALBUM_ART_RESID, R.drawable.mean_something_kinder_than_wolves);
                albumArtView.setImageResource(albumArtResId);

                Bitmap albumBitmap = getReducedBitmap(albumArtResId);
                colorizeFromImage(albumBitmap);

                // start postponed transition ( see bottom of setupTransition() )
                // when shared element is ready (like image is finally loaded)
                startPostponedEnterTransition();
            }

        }, 1000);
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
//        fab.setBackgroundTintList(new ColorStateList(states, colors));
    }
}
