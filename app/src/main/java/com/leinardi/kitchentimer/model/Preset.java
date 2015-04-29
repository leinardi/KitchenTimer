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

package com.leinardi.kitchentimer.model;

import android.content.Context;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by leinardi on 17/04/15.
 */
@RealmClass
public class Preset extends RealmObject {
    private static final String REALM_FILE_NAME = "presets.realm";
    public static final String ID_FIELD_NAME = "id";
    public static final String LABEL_FIELD_NAME = "label";
    public static final String DURATION_FIELD_NAME = "duration";

    @PrimaryKey
    private String id;
    private String label;
    private long duration;
    private transient boolean isSwipeLayoutOpen;

    public Preset() {
        id = UUID.randomUUID().toString();
    }

    public Preset(String label, long duration) {
        this();
        this.label = label;
        this.duration = duration;
    }

    public Preset(String id, String label, long duration) {
        this.id = id;
        this.label = label;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isSwipeLayoutOpen() {
        return isSwipeLayoutOpen;
    }

    public void setIsSwipeLayoutOpen(boolean isSwipeLayoutOpen) {
        this.isSwipeLayoutOpen = isSwipeLayoutOpen;
    }

    public static Realm getRealmInstance(Context context) {
        return Realm.getInstance(context, Preset.REALM_FILE_NAME);
    }
}
