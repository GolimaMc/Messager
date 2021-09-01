package Server;

import Common.Message;
import Common.Message_type;
import Common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerConnectClient {
    private ServerSocket serverSocket = null;
    private static HashMap<String, User> validUsers = new HashMap<>();

    static {

        validUsers.put("1", new User("1", "123"));
        validUsers.put("2", new User("2", "123"));
        validUsers.put("3", new User("3", "123"));
        validUsers.put("4", new User("4", "123"));
        validUsers.put("5", new User("5", "123"));
        validUsers.put("6", new User("6", "123"));

    }
    private boolean checkUser(String userId, String passwd) {

        User user = validUsers.get(userId);
        if(user == null) {
            return  false;
        }
        if(!user.getPassword().equals(passwd)) {
            return false;
        }
        return  true;
    }

    public ServerConnectClient() {
        try {
            System.out.println("Server is prepared");
            serverSocket = new ServerSocket(9999);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Server is waiting");
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                User u = (User) objectInputStream.readObject();
                Message message=new Message();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                if (checkUser(u.getUserid(), u.getPassword())) {
                    message.setMessage_type(Message_type.MESSAGE_LOGIN_SUCCEED);
                    objectOutputStream.writeObject(message);
                    ServerConnectClientThread serverConnectClientThread =
                            new ServerConnectClientThread(socket, u.getUserid());
                    serverConnectClientThread.start();
                    ManageClientThreads.addClientThread(u.getUserid(), serverConnectClientThread);
                } else{
                    message.setMessage_type(Message_type.MESSAGE_LOGIN_FAILED);
                    objectOutputStream.writeObject(message);
                    socket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
