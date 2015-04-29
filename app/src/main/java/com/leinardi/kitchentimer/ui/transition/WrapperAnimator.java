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
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.os.Build;

/**
 * Created by lintonye on 14-12-02.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class WrapperAnimator extends Animator {
    private final Animator mWrappedAnimator;

    public WrapperAnimator(Animator wrappedAnimator) {
        mWrappedAnimator = wrappedAnimator;
    }

    @Override
    public long getStartDelay() {
        return mWrappedAnimator.getStartDelay();
    }

    @Override
    public void setStartDelay(long startDelay) {
        mWrappedAnimator.setStartDelay(startDelay);
    }

    @Override
    public Animator setDuration(long duration) {
        mWrappedAnimator.setDuration(duration);
        return this;
    }

    @Override
    public long getDuration() {
        return mWrappedAnimator.getDuration();
    }

    @Override
    public void setInterpolator(TimeInterpolator value) {
        mWrappedAnimator.setInterpolator(value);
    }

    @Override
    public boolean isRunning() {
        return mWrappedAnimator.isRunning();
    }

    @Override
    public void start() {
        mWrappedAnimator.start();
    }

    @Override
    public void cancel() {
        mWrappedAnimator.cancel();
    }

    @Override
    public void pause() {
        if (!isRevealAnimator()) {
            mWrappedAnimator.pause();
        } else {
        }
    }

    @Override
    public void resume() {
        if (!isRevealAnimator()) {
            mWrappedAnimator.resume();
        } else {
        }
    }

    @Override
    public void addListener(AnimatorListener listener) {
        mWrappedAnimator.addListener(listener);
    }

    @Override
    public void removeAllListeners() {
        mWrappedAnimator.removeAllListeners();
    }

    @Override
    public void removeListener(AnimatorListener listener) {
        mWrappedAnimator.removeListener(listener);
    }

    private boolean isRevealAnimator() {
        return mWrappedAnimator.getClass().getName().contains("RevealAnimator");
    }
}
