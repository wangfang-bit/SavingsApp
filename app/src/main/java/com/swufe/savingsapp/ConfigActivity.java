package com.swufe.savingsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class ConfigActivity extends AppCompatActivity {
    public final String TAG = "ConfigActivity";

    EditText liLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Intent intent = getIntent();//获取传递的数据
        float rate2 = intent.getFloatExtra("bank_rate",0.0f);

        Log.i(TAG,"onCreate rate2="+rate2);

        liLv = (EditText) findViewById(R.id.liLv);

        //显示数据到控件
        liLv.setText(String.valueOf(rate2));


    }
    public void save(View btn){
        Log.i("TAG","save");
        //获取新的输入数据
        float newRate = Float.parseFloat(liLv.getText().toString());

        Log.i(TAG,"save:获取到新的值");
        Log.i(TAG,"onCreate:newRate="+newRate);

        //保存到Bundle或放入到Extra
        Intent intent = getIntent();
        Bundle bdl = new Bundle();
        bdl.putFloat("newBank_rate",newRate);
        intent.putExtras(bdl);
        setResult(2,intent);

        //返回调用页面
        finish();
    }

}
