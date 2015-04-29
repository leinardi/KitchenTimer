/*
 * The MIT License (MIT)
 * Copyright (C) 2015 Alex Lockwood
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

package com.leinardi.kitchentimer.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.leinardi.kitchentimer.R;

public class PlayPauseView extends FrameLayout {

    private static final Property<PlayPauseView, Integer> COLOR =
            new Property<PlayPauseView, Integer>(Integer.class, "color") {
                @Override
                public Integer get(PlayPauseView v) {
                    return v.getColor();
                }

                @Override
                public void set(PlayPauseView v, Integer value) {
                    v.setColor(value);
                }
            };

    private static final long PLAY_PAUSE_ANIMATION_DURATION = 200;

    private final PlayPauseDrawable mDrawable;
    private final Paint mPaint = new Paint();
    private final int mPauseBackgroundColor;
    private final int mPlayBackgroundColor;

    private AnimatorSet mAnimatorSet;
    private int mBackgroundColor;
    private int mWidth;
    private int mHeight;

    public PlayPauseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        mBackgroundColor = getResources().getColor(R.color.colorPrimary);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mDrawable = new PlayPauseDrawable(context);
        mDrawable.setCallback(this);

        mPauseBackgroundColor = getResources().getColor(R.color.colorPrimary);
        mPlayBackgroundColor = getResources().getColor(R.color.colorAccent);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDrawable.setBounds(0, 0, w, h);
        mWidth = w;
        mHeight = h;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, view.getWidth(), view.getHeight());
                }
            });
            setClipToOutline(true);
        }
    }

    private void setColor(int color) {
        mBackgroundColor = color;
        invalidate();
    }

    private int getColor() {
        return mBackgroundColor;
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mDrawable || super.verifyDrawable(who);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mBackgroundColor);
        final float radius = Math.min(mWidth, mHeight) / 2f;
        canvas.drawCircle(mWidth / 2f, mHeight / 2f, radius, mPaint);
        mDrawable.draw(canvas);
    }

    public void toggle() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }

        mAnimatorSet = new AnimatorSet();
        final boolean isPlay = mDrawable.isPlay();
        final ObjectAnimator colorAnim = ObjectAnimator.ofInt(this, COLOR, isPlay ? mPauseBackgroundColor : mPlayBackgroundColor);
        colorAnim.setEvaluator(new ArgbEvaluator());
        final Animator pausePlayAnim = mDrawable.getPausePlayAnimator();
        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mAnimatorSet.setDuration(PLAY_PAUSE_ANIMATION_DURATION);
        mAnimatorSet.playTogether(colorAnim, pausePlayAnim);
        mAnimatorSet.start();
    }

    public boolean isPlay() {
        return mDrawable.isPlay();
    }

    public void setPausePlay(boolean showPause) {
        mDrawable.setPausePlay(showPause);
        setColor(showPause ? mPlayBackgroundColor : mPauseBackgroundColor);
    }
}
