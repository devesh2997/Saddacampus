package com.saddacampus.app.app.Animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ViewSwitcher;

/**
 * Created by Devesh Anand on 12-07-2017.
 */

public class AnimateViewSwitcherFlip {

    ViewSwitcher viewSwitcher;

    public AnimateViewSwitcherFlip(ViewSwitcher viewSwitcher) {
        this.viewSwitcher = viewSwitcher;
    }

    public void animateFlip(){

        ObjectAnimator animation = ObjectAnimator.ofFloat(viewSwitcher.getCurrentView(), "rotationY", 0.0f, 90f);
        animation.setDuration(200);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(viewSwitcher.getDisplayedChild()==0){
                    viewSwitcher.setDisplayedChild(1);
                }else{
                    viewSwitcher.setDisplayedChild(0);
                }

                ObjectAnimator animation2 = ObjectAnimator.ofFloat(viewSwitcher.getCurrentView(), "rotationY", 270f, 360f);
                animation2.setDuration(200);
                animation2.setInterpolator(new AccelerateDecelerateInterpolator());
                animation2.start();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }
}
