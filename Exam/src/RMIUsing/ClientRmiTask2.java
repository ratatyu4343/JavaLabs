package RMIUsing;

import Task2CustomerStructure.Customer;
import Task2CustomerStructure.Customers;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class ClientRmiTask2 {
    private ServerRMIImp server;
    public ClientRmiTask2(int port) throws Exception {
        server = (ServerRMIImp) Naming.lookup("//localhost:"+port+"/server");
    }
    public void getSortedCustomers() throws RemoteException {
        System.out.print(server.getByAlf());
    }

    public void getByCardId(int a, int b) throws RemoteException {
        System.out.print(server.getByCardNum(a, b));
    }
}
