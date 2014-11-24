package com.oa.cgpg.customControls;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.oa.cgpg.R;

/**
 * Created by NieJa on 11/7/2014.
 */
public class PlaceDialog extends Dialog {//} implements
    // android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button showListButton;
    public String title;
    public String description;

    public PlaceDialog(Activity a, String title, String description) {
        super(a);
        this.title = title;
        this.description = description;
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.place_dialog);
        showListButton = (Button) findViewById(R.id.btn_show_list);
        ((TextView) findViewById(R.id.txt_title)).setText(title);
        ((TextView) findViewById(R.id.txtDescription)).setText(description);
    }

/*    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show_list:

                this.dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }*/
}
