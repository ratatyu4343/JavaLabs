package SocketUsing;

import Task2CustomerStructure.Customers;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerSocketTask2 extends Thread{
    private final ServerSocket socket;
    private final Customers customers;
    private List<Thread> serverThreads = new ArrayList<>();
    public ServerSocketTask2(int port) throws Exception{
        socket = new ServerSocket(port);
        customers = new Customers(10);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket user = socket.accept();
                ServerThread thread = new ServerThread(user, customers);
                thread.start();
                serverThreads.add(thread);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Thread serverThread : serverThreads) {
            serverThread.interrupt();
        }
    }
}

class ServerThread extends Thread {
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;
    private Customers customers;

    public ServerThread(Socket user, Customers customers) throws Exception{
        outputStream = new ObjectOutputStream(user.getOutputStream());
        inputStream = new ObjectInputStream(user.getInputStream());
        this.customers = customers;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                int n = (Integer)inputStream.readObject();
                switch (n) {
                    case (1) : {
                        outputStream.writeObject(customers.getCustomersByAlfavite());
                        break;
                    }
                    case (2) : {
                        int a = (Integer)inputStream.readObject();
                        int b = (Integer)inputStream.readObject();
                        outputStream.writeObject(customers.getCustomersByCards(a, b));
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
