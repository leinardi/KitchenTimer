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
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lintonye on 14-12-05.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Scale extends Visibility {
    public Scale(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Scale() {
        super();
    }

    @Override
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        return createScaleAnimator(view, 0, 1);
    }

    @Override
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        return createScaleAnimator(view, 1, 0);
    }

    public Animator createScaleAnimator(View view, float startScale, float endScale) {
        PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("scaleX", startScale, endScale);
        PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("scaleY", startScale, endScale);
        return ObjectAnimator.ofPropertyValuesHolder(view, holderX, holderY);
    }
}
