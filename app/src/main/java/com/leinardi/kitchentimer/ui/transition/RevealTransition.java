/*
 * The MIT License (MIT)
 * Copyright (C) 2014 jimu Labs
 * Copyright (C) 2015 Roberto Leinardi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.leinardi.kitchentimer.ui.transition;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.graphics.Point;
import android.os.Build;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

/**
 * Created by lintonye on 14-12-02.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class RevealTransition extends Visibility {
    private final Point mEpicenter;
    private final int mSmallRadius;
    private final int mBigRadius;
    private final long mDuration;

    public RevealTransition(Point epicenter, int smallRadius, int bigRadius, long duration) {
        mEpicenter = epicenter;
        mSmallRadius = smallRadius;
        mBigRadius = bigRadius;
        mDuration = duration;
    }

    @Override
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        Animator animator = ViewAnimationUtils.createCircularReveal(view, mEpicenter.x, mEpicenter.y,
                mSmallRadius, mBigRadius);
        animator.setDuration(mDuration);
        return new WrapperAnimator(animator);
    }

    @Override
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        Animator animator = ViewAnimationUtils.createCircularReveal(view, mEpicenter.x, mEpicenter.y,
                mBigRadius, mSmallRadius);
        animator.setDuration(mDuration);
        return new WrapperAnimator(animator);
    }
}
