package RMIUsing;

import Task2CustomerStructure.Customer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerRMIImp extends Remote {
    public List<Customer> getByAlf() throws RemoteException;
    public List<Customer> getByCardNum(int a, int b) throws RemoteException;
}
