package com.example.baoding6.myapplication;


import android.support.v7.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.IOError;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    public static Context context;
    public static int IsConnect = -1;//判断网络连接
    public static  String  editClientPort2 = "", editClientIp2 ="";
    private EditText editClientSend, editClientPort, editClientIp;

    private static TcpClient tcpClient = null;
    ExecutorService exec = Executors.newCachedThreadPool();
    private final MyHandler myHandler = new MyHandler(this);
    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    private TextView txtRcv, txt_Value,txtSend;

    private LineChart lineChart;
    private LineDataSet dataset;
    private LineData data ;
    ArrayList<Entry> entries = new ArrayList<Entry>();

    ArrayList<String> labels = new ArrayList<String>();
private int  n= 5;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        lineChart = (LineChart) findViewById(R.id.chart1);
        bindID();
        bindReceiver();

        dataset = new LineDataSet(entries,"蛋白测量仪值");
        entries.add(new Entry(4f,0));
        entries.add(new Entry(-1f,1));
        entries.add(new Entry(3f,2));
        entries.add(new Entry(2f,3));
        entries.add(new Entry(1f,4));
        entries.add(new Entry(1.5f,5));


        labels.add("time1");
        labels.add("time2");
        labels.add("time3");
        labels.add("time4");
        labels.add("time5");
        labels.add("time6");

        data = new LineData(labels, dataset);
        lineChart.setData(data); // set the data and list of lables into chart<br />
        lineChart.setDescription("蛋白测量仪");

        /**
         * 第二种用法
         * 延时1000毫秒后,每隔1000执行一次定时方法
         */
            //创建Timer
            final Timer timer = new Timer();
            //设定定时任务
            timer.schedule(new TimerTask() {
                //定时任务执行方法
                @Override
                public void run() {
                    Test (IsConnect);
                }
            }, 1000, 1000);

    }



    private void Test (int flg)
    {

    }
    // 注意 这里没有 @Override 标签
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_send:
                if(tcpClient !=null)
                {
                    Message message = Message.obtain();
                    message.what = 2;
                    message.obj = editClientSend.getText().toString();
                    myHandler.sendMessage(message);
                    exec.execute(new Runnable() {
                        @Override
                        public void run() {
                            tcpClient.send(editClientSend.getText().toString());
                        }
                    });
                }
                else
                {
                    Toast tst = Toast.makeText(this,"服务器未连接",Toast.LENGTH_SHORT);
                    tst.show();
                }
                break;
            case R.id.btn_tcpClientConn:
                if(tcpClient ==null)
                {
                    try {
                        tcpClient = new TcpClient(getIp(editClientIp2), getPort(editClientPort2));
                        exec.execute(tcpClient);
                        Toast tst = Toast.makeText(this, "正在建立....", Toast.LENGTH_SHORT);
                        tst.show();
                    }
                    catch(IOError ioError)
                    {
                        tcpClient =null;
                        ioError.getMessage();
                    }
                }
                else if (!tcpClient.isRun ){
                    tcpClient =null;
                    Toast tst = Toast.makeText(this,"再次点击重新连接",Toast.LENGTH_SHORT);
                    tst.show();
                }
                else
                {
                    Toast tst = Toast.makeText(this,"网络已连接",Toast.LENGTH_SHORT);
                    tst.show();
                }
                break;
/*            case R.id.btn_tcpClientConn:
                //tcpClient = new TcpClient(editClientIp.getText().toString(), getPort(editClientPort.getText().toString()));
                //tcpClient = new TcpClient(editClientIp.toString() ,getPort(editClientPort.getText().toString()));
                tcpClient = new TcpClient("192.168.1.104",8080);
                exec.execute(tcpClient);
                break;*/
            default:
                break;
        }
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(tcpClient !=null)
            {
                tcpClient.closeSelf();
                tcpClient =null;
            }
            this.finish();
        }
        return false;
    }
    private static final int REQUEST_CODE = 99;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE){
            if(resultCode==RESULT_CANCELED)
                setTitle("砂石之旅");
            else if (resultCode==RESULT_OK) {
                Bundle bundle=data.getExtras();
                if(bundle!=null)
                {
                    editClientIp2=bundle.getString("IP");
                    editClientPort2=bundle.getString("Port");
                    setTitle(editClientIp2 +":"+editClientPort2);

                }
            }
        }
    }

    private String getIp(String tmpIP) {
        if (tmpIP.equals("")) {
            tmpIP = "192.168.1.100";
        }
        return tmpIP;
    }

    private int getPort(String msg) {
        if (msg.equals("")) {
            msg = "8080";
        }
        return Integer.parseInt(msg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /**
         * 此方法用于初始化菜单，其中menu参数就是即将要显示的Menu实例。 返回true则显示该menu,false 则不显示;
         * (只会在第一次初始化菜单时调用) Inflate the menu; this adds items to the action bar
         * if it is present.
         */
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        /**
         * 在onCreateOptionsMenu执行后，菜单被显示前调用；如果菜单已经被创建，则在菜单显示前被调用。 同样的，
         * 返回true则显示该menu,false 则不显示; （可以通过此方法动态的改变菜单的状态，比如加载不同的菜单等） TODO
         * Auto-generated method stub
         */
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        /**
         * 每次菜单被关闭时调用. （菜单被关闭有三种情形，menu按钮被再次点击、back按钮被点击或者用户选择了某一个菜单项） TODO
         * Auto-generated method stub
         */
        super.onOptionsMenuClosed(menu);
    }

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        */

    /**
     * 菜单项被点击时调用，也就是菜单项的监听方法。
     * 通过这几个方法，可以得知，对于Activity，同一时间只能显示和监听一个Menu 对象。 TODO Auto-generated
     * method stub
     *//*
        return super.onOptionsItemSelected(item);
    }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Intent intent;
        switch (item.getItemId()) //得到被点击的item的itemId
        {
            case R.id.TCPIP_Set: //这里的Id就是布局文件中定义的Id，在用R.id.XXX的方法获取出来
                intent = new Intent();
                intent.setClass(MainActivity.this, TCPIP_Set.class);
                intent.putExtra("textView11","网络设置");
                startActivityForResult(intent, REQUEST_CODE);
//               MainActivity.this.startActivity(intent);
                break;
            case R.id.WiFi_Set:
                 intent = new Intent();
                intent.setClass(MainActivity.this, Wifi_Option.class);
                MainActivity.this.startActivity(intent);
//                this.finish();
                break;
            case R.id.About:
                intent = new Intent();
                intent.setClass(MainActivity.this, About.class);
                MainActivity.this.startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String mAction = intent.getAction();
            switch (mAction) {
                case "tcpClientReceiver":
                    String msg = intent.getStringExtra("tcpClientReceiver");
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = msg;
                    myHandler.sendMessage(message);
                    break;
                case "IsConnect":
                     msg = intent.getStringExtra("IsConnect");
                    message = Message.obtain();
                    message.what = 3;
                    message.obj = msg;
                    myHandler.sendMessage(message);
                    break;
                default:
                    break;
            }
        }
    }





    private class MyHandler extends android.os.Handler {
        private WeakReference<MainActivity> mActivity;

        MyHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity != null) {
                switch (msg.what) {
                    case 1:
                        String [] data = msg.obj.toString().split(";");
/*                        txtRcv.setText(data[data.length-1].toString());
                        txt_Value.setText(  "当前测量结果："+data[0].toString());*/

//                        labels.remove(0);
//                        entries.remove(0);
//                        entries.add(new Entry((float)Integer.parseInt(data[0].toString()),5));
//                        labels.add("*********");
//                        lineChart.invalidate();


                        String [] mp = {"Card_ID","Value"};
                        for(int i =0;i<data.length;i++)
                        {
                            Pattern p=Pattern.compile(mp[mp.length-2]);
                            Matcher m=p.matcher(data[i]);
                            boolean result = m.find();
                            if (result)
                            txt_Value.setText( "卡号"+data[i].toString());

                             p=Pattern.compile(mp[mp.length-1]);
                             m=p.matcher(data[i]);
                            result = m.find();
                            if (result)
                            {

                                String [] data2 = data[i].toString().split(":");
                                txtRcv.setText(  "测量值："+data2[1].toString());
                                labels.remove(0);
//                                entries.remove(0);

                                for(int r =0 ;r<5 ; r++) {
                                    entries.set(r,new Entry(  entries.get(r+1).getVal() ,r));
//                                    entries.set(r, entries.get(r + 1));
                                }
                                entries.set(5,new Entry(  Float.parseFloat(data2[1].toString()) ,5)) ;
//                                entries.add(new Entry(  Float.parseFloat(data2[1].toString()) ,5));
                                Date now = new Date();
                                SimpleDateFormat DateF = new SimpleDateFormat("HH:mm:ss");
                                String s = DateF.format(now);
                                labels.add(s);
//                                LineData datan = new LineData(labels, dataset);
//                                lineChart.setData(datan); // set the data and list of lables into chart<br />
                                lineChart.invalidate();
                            }


                        }
                      //  txtRcv.append(msg.obj.toString());
                        break;
                    case 2:
                        //txtSend.append(msg.obj.toString());
                        break;
                    case 3:
                        if ( msg.obj.toString() =="0")
                            Toast.makeText(getApplication(),"网络已断开",Toast.LENGTH_SHORT).show();
                        if ( msg.obj.toString() =="1")
                            Toast.makeText(getApplication(),"网络回复",Toast.LENGTH_SHORT).show();
                        //txtSend.append(msg.obj.toString());
                        break;
                }
            }
        }
    }

    private void bindID() {

        editClientSend = (EditText) findViewById(R.id.txt_ClientSend);
        txtRcv = (TextView) findViewById(R.id.txt_ClientRcv);
        txt_Value = (TextView) findViewById(R.id.txt_CardNum);
        txtSend = (TextView) findViewById(R.id.txt_ClientSend);
    }

    private void bindReceiver() {
        IntentFilter intentFilter = new IntentFilter("tcpClientReceiver");
        registerReceiver(myBroadcastReceiver, intentFilter);
    }
    @Override
    protected void onDestroy() {
// TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);//LS:重点！
    }


}


