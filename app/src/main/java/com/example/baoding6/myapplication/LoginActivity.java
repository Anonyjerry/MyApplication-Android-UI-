package com.example.baoding6.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    // 注意 这里没有 @Override 标签
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Toast tst;
        switch (v.getId()) {
            case R.id.btn_Login:
                EditText mAccount = (EditText) findViewById(R.id.Account);
                EditText mPassword = (EditText) findViewById(R.id.Password);
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                //向下一个Activity传递数据
                Bundle bundle = new Bundle();
                bundle.putString("account",mAccount.toString() );
                bundle.putString("password",mPassword.toString());
                intent.putExtras(bundle);
/*                对于数据的获取可以采用：
                Bundle bundle=getIntent().getExtras();
                String name=bundle.getString("account");*/
                LoginActivity.this.startActivity(intent);
                break;
            case R.id.btn_Quit:
                tst = Toast.makeText(this, "退出程序", Toast.LENGTH_SHORT);
                tst.show();
                finish();
                System.exit(0);//退出
                break;
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
            exitBy2Click();      //调用双击退出函数
        }
        return false;
    }
    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
            System.exit(0);
        }
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    public native String stringFromJNI();
}
