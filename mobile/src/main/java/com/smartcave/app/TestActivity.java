package com.smartcave.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        radioButton = (RadioButton) this.findViewById(R.id.radioButton);
        Button button = (Button) this.findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button:
                done();
                break;
        }
    }

    private void done() {
        boolean checked = radioButton.isChecked();

        Log.d("TestActivity", "done : checkked :" + checked);
    }
}
