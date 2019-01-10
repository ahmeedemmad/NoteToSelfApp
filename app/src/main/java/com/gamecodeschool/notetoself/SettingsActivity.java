package com.gamecodeschool.notetoself;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences mPrefs; ;
    private  SharedPreferences.Editor meditor ;
    private boolean mSound ;

    public static final int FAST = 0;
    public static final int SLOW = 1;
    public static final int NONE = 2;

    private int mAnimOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        mPrefs = getSharedPreferences("Note to self",MODE_PRIVATE);
        meditor = mPrefs.edit();
        mSound = mPrefs.getBoolean("sound",true);
        CheckBox checkBoxSound = (CheckBox)findViewById(R.id.checkBoxSound);
        if (mSound){
            checkBoxSound.setChecked(true);
        }else{
            checkBoxSound.setChecked(false);
        }
        checkBoxSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("sound =",""+mSound);
                Log.i("isChecked =",""+isChecked);

                mSound = !mSound ;
                meditor.putBoolean("sound",mSound);
            }
        });

        mAnimOption = mPrefs.getInt("anim option",FAST);
        final RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioGroup.clearCheck();
        // Which radio button should be selected?
        switch (mAnimOption){
            case  FAST :
                radioGroup.check(R.id.radioFast);
                break;
            case SLOW :
                radioGroup.check(R.id.radioSlow);
                break;
            case NONE :
                radioGroup.check(R.id.radioNone);
                break;
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = radioGroup.findViewById(checkedId);
                switch (rb.getId()){
                    case R.id.radioFast :
                        mAnimOption = FAST ;
                        break;
                    case R.id.radioSlow :
                        mAnimOption = SLOW ;
                        break;
                    case R.id.radioNone :
                        mAnimOption = NONE ;
                        break;
                }
                meditor.putInt("anim option",mAnimOption);
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        meditor.commit();
    }
}
