package Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ServerThread extends Thread{
    /** TODO:分别处理：1、发送过来的Socket
     *               2、发送过来的InetAddress
     *               3、处理Server中的多个sockets
     */

    private Socket socket;
    private InetAddress inetAddress;
    private List<Socket> socketList;

    //构造方法


    public ServerThread(Socket socket, InetAddress inetAddress, List<Socket> socketList) {
        this.socket = socket;
        this.inetAddress = inetAddress;
        this.socketList = socketList;
    }

    //客户端和服务端交换线程
    @Override
    public void run() {
        //输入流
        BufferedInputStream bis = null;
        //输出流
        BufferedOutputStream bos = null;



        try{
            bis = new BufferedInputStream(socket.getInputStream());
            bos = new BufferedOutputStream(socket.getOutputStream());
            int i = 0;
            while (true){
                byte[] by = new byte[1024+2];
                int res = 0;
                res = bis.read(by);
                by[0] = (byte) socketList.indexOf(socket);
                if (by[1] == 2){
                    String receive = new String(by,2,res-2);
                    if (receive.equalsIgnoreCase("bye"))
                    {
                        // 如果客户端发送的是bye, 说明其下线，则从listSockets里删除对应的socket.
                        bos.write(receive.getBytes());  // 把bye给客户端的读取线程，从而可以关闭掉读取线程
                        bos.flush();
                        System.out.printf("用户%d下线, ", socketList.indexOf(socket));
                        socketList.remove(socket);
                        System.out.printf("目前聊天室仍有%d人\n", socketList.size());
                    }
                }
                System.out.println("i" + i + "res = " + res);
                System.out.println("by.length: " + by.length);
                System.out.println("Socket[]: " + Arrays.toString(socketList.toArray()));
                // 调用函数，将接受到的消息发送给所有客户端
                BroadCast(socket, by, res);

            }

        } catch (IOException e) {
                e.printStackTrace();
        }finally {
            try {
                if(bos!=null){
                    bos.close();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            try{
                if (bis != null){
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        }



    private void BroadCast(Socket s, byte[] by, int res) {
        // 将服务器接收到的消息发送给除了发送方以外的其他客户端
        int i = 0;
        for (Socket socket: socketList)
        {
            if (s!=socket)  // 判断不是当前发送的客户端
            {
                System.out.println("发送给用户: " + socketList.indexOf(socket));
                BufferedOutputStream ps = null;
                try {
                    ps = new BufferedOutputStream(socket.getOutputStream());
                    ps.write(by, 0, res);   // 写入输出流，将内容发送给客户端的输入流
                    ps.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
