package com.vpipl.kalpamrit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.vpipl.kalpamrit.R;

/**
 * Created by admin on 22-07-2017.
 */
public class CloseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        finish();
        overridePendingTransition(0, 0);

    }
}
