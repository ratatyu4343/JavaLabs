package Task2CustomerStructure;
import java.io.Serializable;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import static java.lang.Math.abs;

public class Customers{
    private List<Customer> customers = new ArrayList<>();

    public Customers(int n) throws Exception {
        super();
        setRandoms(n);
    }

    public List<Customer> getCustomersByAlfavite() {
        List<Customer> alfCustomers = new ArrayList<>(customers);
        Comparator<Customer> comparatorName = new Comparator<Customer>() {
            @Override
            public int compare(Customer o1, Customer o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        Comparator<Customer> comparatorSurname = new Comparator<Customer>() {
            @Override
            public int compare(Customer o1, Customer o2) {
                return o1.getSurname().compareTo(o2.getSurname());
            }
        };
        alfCustomers.sort(comparatorName.thenComparing(comparatorSurname));
        return alfCustomers;
    }

    public List<Customer> getCustomersByCards(int a, int b) {
        List<Customer> new_customers = new ArrayList<>();
        for(Customer customer : customers) {
            int creditNum = customer.getCreditNum();
            if (creditNum >= a && creditNum <= b)
                new_customers.add(customer);
        }
        return new_customers;
    }
    public void addCustomer(Customer c) {
        customers.add(c);
    }

    public void delCustomer(Customer c) {
        customers.remove(c);
    }
    private void setRandoms(int n) {
        for (int i = 0; i < n; i++) {
            Customer customer = new Customer(
                    getRandomInt(), getRandomString(), getRandomString(), getRandomString(),
                    getRandomString(), getRandomInt(), getRandomInt()
            );
            addCustomer(customer);
        }
    }
    private Random random = new Random();
    private String getRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        return  random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
    private int getRandomInt() {
        Random random = new Random();
        return abs(random.nextInt()%1000);
    }
}
