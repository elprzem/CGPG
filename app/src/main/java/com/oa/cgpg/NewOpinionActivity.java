package com.oa.cgpg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.oa.cgpg.models.opinionNetEntity;

import java.util.Date;


public class NewOpinionActivity extends Activity {
    private int poiId;
    private RadioButton positive;
    private RadioButton negative;
    private final int POSITIVE = 0;
    private final int NEGATIVE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_opinion);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        poiId = intent.getIntExtra("poiNr", 0);
        final Button addOpinion = (Button) findViewById(R.id.addOpinion);
        positive = (RadioButton) findViewById(R.id.radioPlus);
        negative = (RadioButton) findViewById(R.id.radioMinus);

        final EditText opinionText = (EditText) findViewById(R.id.newOpinionText);
        opinionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() != 0){
                    addOpinion.setEnabled(true);
                }else{
                    addOpinion.setEnabled(false);
                }
            }
        });
        opinionText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER){
                    sendOpinion(opinionText.getText().toString());
                    return true;
                }
                return false;
            }
        });
        addOpinion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOpinion(opinionText.getText().toString());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_opinion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_update:
                // TODO aktualizacja bazy

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void sendOpinion(String text){
        int type = positive.isChecked()? POSITIVE : NEGATIVE;
        opinionNetEntity opinionEntity = new opinionNetEntity(0,text,LoggedUserInfo.getInstance().getUserName(),poiId,0,0,0,type, new Date());
        //TODO - wysłanie opinii
    }
}
