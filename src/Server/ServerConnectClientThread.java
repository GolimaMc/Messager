package Server;

import Common.Message;
import Common.Message_type;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;

public class ServerConnectClientThread extends Thread {
    private Socket socket;
    private String userId;

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {

        while (true) {
            try {
                System.out.println("Server are connecting with" + userId);
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                if (message.getMessage_type().equals(Message_type.MESSAGE_SHOW_ONLINE_USER)) {
                    System.out.println(message.getSender() + " want show online users");
                    String onlineUser = ManageClientThreads.getOnlineUser();
                    Message message2 = new Message();
                    message2.setMessage_type(Message_type.MESSAGE_RET_ONLINE_USER);
                    message2.setContent(onlineUser);
                    message2.setReceiver(message.getSender());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);

                } else if (message.getMessage_type().equals(Message_type.MESSAGE_SEND_WORDS)) {
                    ServerConnectClientThread serverConnectClientThread =
                            ManageClientThreads.getServerConnectClientThread(message.getReceiver());
                    ObjectOutputStream oos =
                            new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);

                } else if (message.getMessage_type().equals(Message_type.MESSAGE_SEND_ALL)) {
                    HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();

                    Iterator<String> iterator = hm.keySet().iterator();
                    while (iterator.hasNext()) {

                        String onLineUserId = iterator.next().toString();

                        if (!onLineUserId.equals(message.getSender())) {

                            ObjectOutputStream oos =
                                    new ObjectOutputStream(hm.get(onLineUserId).getSocket().getOutputStream());
                            oos.writeObject(message);
                        }

                    }

                }else if (message.getMessage_type().equals(Message_type.MESSAGE_CLIENT_EXIT)) {

                    System.out.println(message.getSender() + " logout");
                    ManageClientThreads.removeServerConnectClientThread(message.getSender());
                    socket.close();
                    break;

                } else {
                    System.out.println("Others");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

}
