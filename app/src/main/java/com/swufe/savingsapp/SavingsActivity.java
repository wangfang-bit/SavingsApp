package com.swufe.savingsapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SavingsActivity extends AppCompatActivity implements Runnable{//实现接口开启子线程

    private final String TAG = "Rate";
    private float rate = 0.1f;
    private String str = "";
    private String updateData = "";

    EditText sum;
    EditText time;
    //EditText rate;
    TextView inter;
    Spinner bank;
    ArrayAdapter<CharSequence> arrayadapter;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings);

        sum = (EditText) findViewById(R.id.sum);
        time = (EditText) findViewById(R.id.time);
        //rate =  (EditText)findViewById(R.id.rate);
        inter = (TextView) findViewById(R.id.inter);
        bank = (Spinner) findViewById(R.id.bank);

        bank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String[] newBank= getResources().getStringArray(R.array.bank);
                Toast.makeText(SavingsActivity.this, "你点击的是:"+newBank[position], Toast.LENGTH_SHORT).show();
                str = newBank[position];

                Log.i(TAG,"run:"+str+"的利率为"+rate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Another interface callback
            }
        });

        //获取sp里保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myRate", Activity.MODE_PRIVATE);
        rate = sharedPreferences.getFloat("sp_rate",0.0f);
        updateData = sharedPreferences.getString("update_data","");

        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
        final String todayStr = sdf.format(today);



        Log.i(TAG,"onCreate: sp rate="+rate);
        Log.i(TAG,"onCreate: sp updateData="+updateData);

        //判断时间
        if(!todayStr.equals(updateData)){
            Log.i(TAG,"onCreate:需要更新");
            //开启子线程
            Thread t = new Thread(this);
            t.start();
        }else{
            Log.i(TAG,"onCreate:不需要更新");
        }


        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==5){
                    Bundle bdl = (Bundle) msg.obj;
                    if(str.equals("工商银行")){
                        rate = bdl.getFloat("gs-rate");
                    }
                    else if(str.equals("建设银行")){
                        rate = bdl.getFloat("js-rate");
                    }
                    else if(str.equals("农业银行")){
                        rate = bdl.getFloat("ny-rate");
                    }
                    else if(str.equals("交通银行")){
                        rate = bdl.getFloat("jt-rate");
                    }
                    //保存更新的信息
                    SharedPreferences sharedPreferences = getSharedPreferences("myRate", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("update_data",todayStr);
                    editor.putFloat("sp_rate",rate);
                    editor.apply();


                    Toast.makeText(SavingsActivity.this,"利率已更新",Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
                Log.i(TAG,"handleMessage:rate="+rate);
            }
        };


    }


    public void onClick(View btn){
        //获取用户输入
        String str1 = sum.getText().toString();
        String str2 = time.getText().toString();
        //String str3 = rate.getText().toString();
        float r1 = 0;
        float r2 = 0;

        if(str1.length()>0&&str2.length()>0) {
            r1 = Float.parseFloat(str1);
            r2 = Float.parseFloat(str2);
        }else{
            Toast.makeText(this,"输入内容不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(btn.getId()==R.id.rate){
            float val = r1*r2*rate;
            inter.setText(String.valueOf(val));
        }
    }

    public void openOne(View btn){
        //打开一个窗口
        Log.i("open","openOne");
        Intent config = new Intent(this,ConfigActivity.class);
        config.putExtra("bank_rate",rate);//传递参数到下一页面

        Log.i(TAG,"openOne:bank_rate"+rate);

        startActivityForResult(config,1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.open_list){
            //打开列表窗口
            Intent list = new Intent(this,RateListActivity.class);
            startActivity(list);


        }
        return super.onOptionsItemSelected(item);
    }

    //处理返回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1&&resultCode==2){
            Bundle bundle = data.getExtras();
            rate = bundle.getFloat("newBank_rate",0.1f);
            Log.i(TAG,"onActivityResult: rate="+rate);

            //将新设置的利率写到sp里
            SharedPreferences sharedPreferences = getSharedPreferences("myRate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("sp_rate",rate);
            editor.commit();//保存

            Log.i(TAG,"onActivityResult:数据已保存到sharedPreferences");

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void run() {
        Log.i(TAG,"run()....");
        for(int i=1;i<6;i++){
            Log.i(TAG,"run:i="+i);
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }

        //用于保存获取的汇率
        Bundle bundle = new Bundle();



        //获取网络数据
        /*URL url = null;
        try{
            url = new URL("http://data.bank.hexun.com/ll/ckll.aspx");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream in = http.getInputStream();

            String html = inputStream2String(in);//输入流转化为字符串
            Log.i(TAG,"run:html="+html);
            Document doc = Jsoup.parse(html);

        } catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }*/

        Document doc = null;
        try {
            doc = Jsoup.connect("http://data.bank.hexun.com/ll/ckll.aspx").get();
            //doc = Jsoup.parse(html);
            Log.i(TAG,"run:"+doc.title());
            Elements tables = doc.getElementsByTag("table");
            /*int i = 1;
            for(Element table: tables){
                Log.i(TAG,"run: table["+i+"]="+ table);
                i++;
            */
                Element table2 = tables.get(1);
                //Log.i(TAG,"run:table2="+table2);
                //获取td中的数据
                Elements tds = table2.getElementsByTag("td");

                float j=tds.size()/4;
                for(int i = 15;i<j;i+=14){
                    Element td1 = tds.get(i);
                    Element td2 = tds.get(i+1);
                    Log.i(TAG,"run:"+td1.text()+"==>"+td2.text());

                    String bank = td1.text();
                    String rate = td2.text();

                    if("工商银行".equals(bank)){
                        bundle.putFloat("gs-rate",Float.parseFloat(rate));
                    }else if("建设银行".equals(bank)){
                        bundle.putFloat("js-rate",Float.parseFloat(rate));
                    }else if("交通银行".equals(bank)){
                        bundle.putFloat("jt-rate",Float.parseFloat(rate));
                    }else if("农业银行".equals(bank)){
                        bundle.putFloat("ny-rate",Float.parseFloat(rate));
                    }


                }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //bundle中保存所回去的利率

        //获取msg对象用于返回主线程
        Message msg = handler.obtainMessage(5);
        //msg.obj = "Hello from run()";
        msg.obj = bundle;
        handler.sendMessage(msg);


    }
    private String inputStream2String(InputStream inputStream) throws IOException{
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream,"UTF-8");
        for(;;){
            int rsz = in.read(buffer,0,buffer.length);
            if(rsz<0)
                break;
            out.append(buffer,0,rsz);
        }
        return out.toString();
    }

}
