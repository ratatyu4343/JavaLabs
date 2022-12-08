import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface DB extends Remote {
    List<Book> getBook(String nam) throws RemoteException;
    void delBook(int id)  throws RemoteException ;
    void addBook(Book b, Auphtor a)  throws RemoteException;
    void updateBook(Book b)  throws RemoteException;
    Auphtor getAuphtor(String name)  throws RemoteException;
    void deleteAuphtor(int id)  throws RemoteException;
    void addAuphtor(Auphtor a)  throws RemoteException;
    void updateAuphtor(Auphtor a)  throws RemoteException;
    String printAll()  throws RemoteException;
}
