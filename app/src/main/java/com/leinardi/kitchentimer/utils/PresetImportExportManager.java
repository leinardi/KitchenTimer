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

package com.leinardi.kitchentimer.utils;

import android.content.Context;
import android.os.Environment;
import android.text.format.DateUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.leinardi.kitchentimer.model.Preset;
import com.leinardi.kitchentimer.model.PresetListSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by leinardi on 23/04/15.
 */
public class PresetImportExportManager {
    private static final String TAG = PresetImportExportManager.class.getSimpleName();

    public static final String CSV_FILENAME = "KitchenTimer.csv";
    public static final String JSON_FILENAME = "KitchenTimer.json";
    public static File SD_PATH = Environment.getExternalStorageDirectory();

    public static boolean exportToSD(Context context) {
        Realm realm = null;
        try {
            File csvFile = new File(SD_PATH, CSV_FILENAME);
            if (checkIfFileExist(csvFile)) {
                csvFile.delete();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(new File(SD_PATH, JSON_FILENAME));
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            realm = Preset.getRealmInstance(context);
            RealmResults<Preset> presets = realm.where(Preset.class).findAll();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(RealmResults.class, new PresetListSerializer())
                    .setPrettyPrinting()
                    .create();
            outputStreamWriter.append(gson.toJson(presets));
            outputStreamWriter.close();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            LogUtils.e(TAG, "exportToSD", e);
            return false;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public static boolean importFromSD(Context context) {
        File csvFile = new File(SD_PATH, CSV_FILENAME);
        if (checkIfFileExist(csvFile)) {
            importCSV(csvFile, context);
        }
        Realm realm = null;
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(RealmResults.class, new PresetListSerializer())
                    .setPrettyPrinting()
                    .create();

            Type type = new TypeToken<RealmResults<Preset>>() {
            }.getType();

            List<Preset> presets = gson.fromJson(new FileReader(new File(SD_PATH, JSON_FILENAME)), type);

            realm = Preset.getRealmInstance(context);
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(presets);
            realm.commitTransaction();

            return true;
        } catch (Exception e) {
            LogUtils.e(TAG, "importFromSD", e);
            return false;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private static boolean importCSV(File csvFile, Context context) {
        Realm realm = null;
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(csvFile));
        } catch (FileNotFoundException ex) {
            LogUtils.e(TAG, "importCSV - File Not Found", ex);
            return false;
        }
        try {
            realm = Preset.getRealmInstance(context);

            reader.readNext();

            // Read all the rest of the lines as secrets.

            String[] row;
            while ((row = reader.readNext()) != null) {
                if (row.length == 4) {
                    Preset preset = new Preset();
                    preset.setLabel(row[0]);
                    try {
                        int hours = Integer.parseInt(row[1]);
                        int minutes = Integer.parseInt(row[2]);
                        int seconds = Integer.parseInt(row[3]);

                        long duration = hours * 60 * 60 + minutes * 60 + seconds * DateUtils.SECOND_IN_MILLIS;
                        preset.setDuration(duration);
                    } catch (NumberFormatException nFE) {
                        LogUtils.e("importCSV", "NumberFormat Error", nFE);
                        continue;
                    }
                    realm.beginTransaction();
                    realm.copyToRealm(preset);
                    realm.commitTransaction();
                }
            }
        } catch (Exception ex) {
            LogUtils.e("Utils", "importCSV", ex);
        } finally {
            if (realm != null) {
                realm.close();
            }
            try {
                reader.close();
            } catch (IOException ex) {
            }
        }
        return true;
    }


    private static boolean checkIfFileExist(File file) {
        return file.exists() && !file.isDirectory();
    }

    private static boolean isSdPresent() {
        return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }
}
