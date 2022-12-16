package SocketUsing;

import Task2CustomerStructure.Customer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientSocketTask2 {
    private final Socket socket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;

    public ClientSocketTask2(String proxy, int port) throws Exception{
        socket = new Socket(proxy, port);
        inputStream = new ObjectInputStream(socket.getInputStream());
        outputStream = new ObjectOutputStream(socket.getOutputStream());
    }
    public void getSortedCustomers() throws Exception {
        outputStream.writeObject((Integer) 1);
        System.out.print((List<Customer>) inputStream.readObject());
    }

    public void getByCardId(int a, int b) throws Exception{
        outputStream.writeObject((Integer) 2);
        outputStream.writeObject((Integer) a);
        outputStream.writeObject((Integer) b);
        System.out.println((List<Customer>) inputStream.readObject());
    }
}
