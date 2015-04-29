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

package com.leinardi.kitchentimer.ui.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.leinardi.kitchentimer.R;
import com.leinardi.kitchentimer.model.Countdown;
import com.leinardi.kitchentimer.ui.MainActivity;
import com.leinardi.kitchentimer.utils.LogUtils;

import io.realm.Realm;

/**
 * Created by leinardi on 24/04/15.
 */
public class DeleteAllCountdownDialogFragment extends DialogFragment {
    private static final String TAG = DeleteAllCountdownDialogFragment.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogTheme)
                .setTitle(R.string.delete_timers_title)
                .setMessage(R.string.delete_timers_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            Realm realm = Countdown.getRealmInstance(getActivity());
                            realm.beginTransaction();
                            realm.clear(Countdown.class);
                            realm.commitTransaction();
                            realm.close();
                            ((MainActivity) getActivity()).notifyDataSetChangedCountdownList();
                        } catch (RuntimeException e) {
                            LogUtils.e(TAG, "Error cleaning Presets", e);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }
}
