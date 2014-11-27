package com.oa.cgpg.customControls;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.oa.cgpg.LoggedUserInfo;
import com.oa.cgpg.MainActivity;

/**
 * Created by Izabela on 2014-11-27.
 */
public class LogOutDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    // Use the Builder class for convenient dialog construction
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage("Czy na pewno chcesz się wylogować?")
            .setTitle("Wyloguj")
            .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    LoggedUserInfo.getInstance().setUserId(-1);
                    LoggedUserInfo.getInstance().setLoggedIn(false);
                    LoggedUserInfo.getInstance().setUserName("");
                    ((MainActivity)getActivity()).startLoginFragment();
                }
            })
            .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
    return builder.create();
}
}
