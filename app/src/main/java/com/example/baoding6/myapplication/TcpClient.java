package com.example.baoding6.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpClient extends AppCompatActivity implements Runnable {

    private String TAG = "TcpClient";
    private String  serverIP = "";
    private int serverPort = -1;
    private PrintWriter pw;
    private InputStream is;
    private DataInputStream dis;
    public boolean isRun = true;
    private Socket socket = null;
    byte buff[]  = new byte[4096];
    private String rcvMsg;
    private int rcvLen;
    private boolean close = false; // 关闭连接标志位，true表示关闭，false表示连接

    public TcpClient(String ip , int port){
        this.serverIP = ip;
        this.serverPort = port;
    }

    public void closeSelf(){
        isRun = false;
    }

    public void send(String msg){
        pw.println(msg);
        pw.flush();
    }

    public void run() {
        try {
            socket = new Socket(serverIP,serverPort);
            socket.setSoTimeout(50000);
//            socket.setKeepAlive(true);
            pw = new PrintWriter(socket.getOutputStream(),true);
            is = socket.getInputStream();
            dis = new DataInputStream(is);
        } catch (IOException e) {
            Log.i(TAG, "服务器连接错误！！！！！！！！！！！！"+e.getMessage());
            e.printStackTrace();
        }
        while (isRun){
            try {
                close = isServerClose(socket);//判断是否断开
                if(!close )
                {
                    rcvLen = dis.read(buff);
                    rcvMsg = new String(buff, 0, rcvLen, "utf-8");
                    Intent intent = new Intent();
                    intent.setAction("tcpClientReceiver");
                    intent.putExtra("tcpClientReceiver", rcvMsg);
                    MainActivity.context.sendBroadcast(intent);               //将消息发送给主界面
                    if (rcvMsg.equals("QuitClient")) {                      //服务器要求客户端结束
                        isRun = false;
                    }
                }
                else
                {
//                    isRun = false;
                    Log.i(TAG, "连接失败+++++++++++或数据拥堵++++++++++++++");

                }
                //---------创建连接-------------------------
                while(close){//已经断开，重新建立连接
                    try{
//                        socket.close() ;
//                        while(!socket.isClosed() );
                        socket = new Socket(serverIP,serverPort);//客户端开启太多了。。。。。。暂时不适合大量数据
//                        socket.setKeepAlive(true);
                        socket.setSoTimeout(50000);
                        pw = new PrintWriter(socket.getOutputStream(),true);
                        is = socket.getInputStream();
                        dis = new DataInputStream(is);
//                        close = !Send(socket,"2");
                        close = isServerClose(socket);//判断是否断开
                        Log.i(TAG,"建立连接成功："+serverIP+":"+serverPort);
                    }catch(Exception se){
                        close=true;
                        Log.i(TAG,"创建连接失败:"+serverIP+":"+serverPort);
                    }
                }
            }
            catch (UnknownHostException e) {
                //远程服务器无效
//                isRun = false;
                Log.i(TAG, "服务器连接失败+++++++++++++++"+e.getMessage());
                e.printStackTrace();
            }
            catch (IOException e) {
                // 远程端口无效
//                isRun = false;
                Log.i(TAG, "数据读取超时-----端口重置+++++++++++"+e.getMessage());
                e.printStackTrace();
            }
        }
        try {
            pw.close();
            is.close();
            dis.close();
            socket.close();
        } catch (IOException e) {
            Log.i(TAG, "退出消息****************"+ e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 发送数据，发送失败返回false,发送成功返回true
     * @param csocket
     * @param message
     * @return
     */
    public Boolean Send(Socket csocket,String message){
        try{
            PrintWriter out = new PrintWriter(csocket.getOutputStream(), true);
            out.println(message);
            return true;
        }catch(Exception se){
            se.printStackTrace();
            return false;
        }
    }
    /**
     * 判断是否断开连接，断开返回true,没有返回false
     * @param socket
     * @return
     */
    public Boolean isServerClose(Socket socket){
        try{
            socket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        }catch(Exception se){
            return true;
        }
    }
}
