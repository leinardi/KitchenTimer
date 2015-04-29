/*
 * Kitchen Timer
 * Copyright (C) 2015 Roberto Leinardi
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.leinardi.kitchentimer;

import android.app.Application;

/**
 * Created by leinardi on 13/04/15.
 */
public class KitchenTimer extends Application {

    // Yeah I now, it's not a real singleton,
    // but  there can be at most one instance of Application in Android
    private static KitchenTimer mSingleton;

    @Override
    public void onCreate() {
        super.onCreate();
        mSingleton = this;

        if (!BuildConfig.DEBUG) {
            FabricWrapper.with(this);
        }
    }

    public static KitchenTimer getInstance() {
        return mSingleton;
    }
}
