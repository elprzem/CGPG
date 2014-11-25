package com.oa.cgpg.customControls;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Izabela on 2014-11-20.
 */
public class NoConnectionDialog extends DialogFragment {
    private String message;

    public void setMessage(String message){
        this.message = message;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setTitle("Błąd")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                       if(message.equals("Wymagane jest połączenie z Internetem do pobrania danych"))
                            getActivity().onBackPressed();
                        else
                            getFragmentManager().popBackStack();
                    }
                });
        return builder.create();
    }
}