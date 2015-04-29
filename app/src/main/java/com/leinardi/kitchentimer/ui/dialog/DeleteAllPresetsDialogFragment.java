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

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.leinardi.kitchentimer.R;
import com.leinardi.kitchentimer.model.Preset;
import com.leinardi.kitchentimer.utils.LogUtils;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import io.realm.Realm;

/**
 * Created by leinardi on 24/04/15.
 */
public class DeleteAllPresetsDialogFragment extends DialogFragment {
    private static final String TAG = DeleteAllPresetsDialogFragment.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogTheme)
                .setTitle(R.string.delete_preset_title)
                .setMessage(R.string.delete_preset_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            Realm realm = Preset.getRealmInstance(getActivity());
                            realm.beginTransaction();
                            realm.clear(Preset.class);
                            realm.commitTransaction();
                            realm.close();
                            getActivity().setResult(Activity.RESULT_OK);
                            SnackbarManager.show(Snackbar.with(getActivity())
                                            .text(R.string.presets_delete_all_success)
                            );
                        } catch (RuntimeException e) {
                            LogUtils.e(TAG, "Error cleaning Presets", e);
                            SnackbarManager.show(Snackbar.with(getActivity())
                                            .text(R.string.presets_delete_all_error)
                                            .type(SnackbarType.MULTI_LINE)
                                            .textColorResource(R.color.red)
                                            .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                            );
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }
}
