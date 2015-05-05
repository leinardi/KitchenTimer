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

package com.leinardi.kitchentimer.compat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateUtils;

import com.leinardi.kitchentimer.compat.model.Food;
import com.leinardi.kitchentimer.compat.model.Food.FoodMetaData;
import com.leinardi.kitchentimer.model.Preset;
import com.leinardi.kitchentimer.utils.LogUtils;

import io.realm.Realm;

public class DatabaseManager {
    private static final String TAG = DatabaseManager.class.getSimpleName();
    private SQLiteDatabase mDatabase;
    private Context mContext;
    private DatabaseHelper mDatabaseHelper;
    public static final String DB_NAME = "DB_FOODS";
    private static final String TABLE = "FOOD";
    private static final int DB_VERSION = 2;

    public DatabaseManager(Context context) {
        this.mContext = context;
        this.mDatabaseHelper = new DatabaseHelper(context);
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " +
                            TABLE + " (" +
                            FoodMetaData.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                            FoodMetaData.NAME + " TEXT NOT NULL," +
                            FoodMetaData.HOURS + " INTEGER NOT NULL," +
                            FoodMetaData.MINUTES + " INTEGER NOT NULL," +
                            FoodMetaData.SECONDS + " INTEGER NOT NULL)"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            LogUtils.d("DbTool.onUpgrade", "old:" + oldVersion + " new:" + newVersion);
            if (db.getVersion() == 1) {
                db.execSQL("ALTER TABLE " + TABLE + " RENAME TO tmp_" + TABLE);
                onCreate(db);
                db.execSQL("INSERT INTO " +
                                TABLE + "(" +
                                FoodMetaData.NAME + ", " +
                                FoodMetaData.HOURS + ", " +
                                FoodMetaData.MINUTES + ", " +
                                FoodMetaData.SECONDS + ") " +
                                "SELECT " +
                                "nome, " +
                                FoodMetaData.HOURS + ", " +
                                FoodMetaData.MINUTES + ", " +
                                FoodMetaData.SECONDS + " " +
                                "FROM tmp_" + TABLE
                );
                db.execSQL("DROP TABLE IF EXISTS tmp_" + TABLE);
            } else {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE);
                onCreate(db);
            }
        }
    }

    public void open() {
        mDatabase = mDatabaseHelper.getWritableDatabase();
    }

    public void close() {
        mDatabaseHelper.close();
    }

    public void insert(Food record) {
        ContentValues values = new ContentValues();
        values.put(FoodMetaData.NAME, record.name);
        values.put(FoodMetaData.HOURS, record.hours);
        values.put(FoodMetaData.MINUTES, record.minutes);
        values.put(FoodMetaData.SECONDS, record.seconds);
        mDatabase.insert(TABLE, null, values);
    }

    public void update(Food record) {
        ContentValues values = new ContentValues();
        values.put(FoodMetaData.NAME, record.name);
        values.put(FoodMetaData.HOURS, record.hours);
        values.put(FoodMetaData.MINUTES, record.minutes);
        values.put(FoodMetaData.SECONDS, record.seconds);
        mDatabase.update(TABLE, values, "_id=" + record.id, null);
    }

    public void delete(long examId) {
        mDatabase.delete(TABLE, "_id=" + examId, null);
    }

    public void truncate() {
        mDatabase.delete(TABLE, null, null);
    }

    public Cursor query(long examId) {
        return mDatabase.query(TABLE, FoodMetaData.COLUMNS, "_id=" + examId, null, null, null, null);
    }

    public Cursor getRecords() {
        return mDatabase.rawQuery("SELECT * FROM " + TABLE + " ORDER BY " + FoodMetaData.NAME, null);
    }

    public boolean importPresetFromOldDatabase() {
        Cursor cursor = null;
        Realm realm = null;

        try {
            cursor = getRecords();
            realm = Preset.getRealmInstance(mContext);

            realm.beginTransaction();
            while (cursor.moveToNext()) {
                int hours = cursor.getInt(cursor.getColumnIndex(FoodMetaData.HOURS));
                int minutes = cursor.getInt(cursor.getColumnIndex(FoodMetaData.MINUTES));
                int seconds = cursor.getInt(cursor.getColumnIndex(FoodMetaData.SECONDS));
                String label = cursor.getString(cursor.getColumnIndex(FoodMetaData.NAME));
                long duration = (hours * 60 * 60 + minutes * 60 + seconds) * DateUtils.SECOND_IN_MILLIS;

                Preset preset = new Preset();
                preset.setLabel(label);
                preset.setDuration(duration);

                realm.copyToRealm(preset);
            }
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            LogUtils.e(TAG, "Error importing Presets from old DB", e);
            return false;
        } finally {
            if (realm != null) {
                realm.close();
            }
            if (cursor != null) {
                cursor.close();
            }

        }
    }
}