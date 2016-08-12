package com.teamtreehouse.albumcover.transitions;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


// Custom transition must extend another Transition class
public class Fold extends Visibility {

    // Both methods rturn Animator object. Proves that Transition Animations are based upon Property animations
    @Override
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        Log.d("CustomAnimation","Fold.onAppear()");
        return createFoldAnimator(view, false);
    }

    @Override
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        Log.d("CustomAnimation","Fold.onDisappear()");
        return createFoldAnimator(view, true);
    }

    // Helper method
    public Animator createFoldAnimator(View view, boolean folding) {
        // same code as we used in property animations's animate()
        int start = view.getTop();
        // set the int "end" to equal the bottom position of the view.
        // top position + views. height - 1
        // view.getHeight() wont work correctly
        int end = view.getTop() + view.getMeasuredHeight() - 1;
        // shuffle start and end depending if we fold or unfold
        if (folding) {
            int temp = start;
            start = end;
            end = temp;
        }
        view.setBottom(start);
        // Transition Animation are based on Property animations
        ObjectAnimator animator = ObjectAnimator.ofInt(view, "bottom", start, end);
        return animator;
    }
}













