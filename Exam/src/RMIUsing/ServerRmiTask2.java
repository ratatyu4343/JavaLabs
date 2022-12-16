package RMIUsing;

import Task2CustomerStructure.Customer;
import Task2CustomerStructure.Customers;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ServerRmiTask2 extends UnicastRemoteObject implements ServerRMIImp {
    private Customers customers;
    public ServerRmiTask2(int port) throws Exception{
        super();
        customers = new Customers(10);
        Registry r = LocateRegistry.createRegistry(port);
        r.rebind("server", this);
    }
    @Override
    public synchronized List<Customer> getByAlf(){
        return customers.getCustomersByAlfavite();
    }
    @Override
    public synchronized List<Customer> getByCardNum(int a, int b) {
        return customers.getCustomersByCards(a, b);
    }
}
