package Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public static void main(String[] args) {
        //监听器：服务器端口
        ServerSocket ss = null;
        //服务器的主要访问形式：IP 和 端口
        Socket s = null;

        //创建一个List来存放Socket
        ArrayList<Socket> socketList = new ArrayList<>();
        try{
            //1、创建端口号为9999的监听器
            ss = new ServerSocket(9999);
            //2、等待Client进行连接请求，调用accept方法
            //并采用多线程方式，允许多个用户进行操作
            int i = 0;
            while (true){
                System.out.println("等待客户端响应---------");
                s = ss.accept();
                //将Client的响应存入List中
                socketList.add(s);
                i++;
                System.out.println("欢迎用户"+i+"接入频道！");
                System.out.println("目前频道有"+socketList.size()+"人！");
                InetAddress inetAddress = s.getInetAddress();
                System.out.println("客户端"+inetAddress+"连接成功！");
                //调用多线程来处理不同用户
                //为啥要加start
                //我认为：首先创建该ServerThread对象并实例化 .start()是直接执行该进程？
                ServerThread serverThread = new ServerThread(s, inetAddress, socketList);
                serverThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关闭流
            try{
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
