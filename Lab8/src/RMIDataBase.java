import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RMIDataBase extends UnicastRemoteObject implements DB {
    private Connection connection;

    public RMIDataBase(String n) throws RemoteException {
        super();
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "1111");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Book> getBook(String nam) {
        List<Book> books = new ArrayList<>();
        try {
            var statement = connection.createStatement();
            String sql = "select * from books where name='" + nam + "'";
            var result = statement.executeQuery(sql);
            while (result.next()) {
                String name = result.getString("name");
                int id = result.getInt("id");
                int year = result.getInt("year");
                books.add(new Book(name, year, id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }

    public void delBook(int id) {
        try {
            var statement = connection.createStatement();
            String sql = "delete from books where id=" + id;
            statement.execute(sql);
            sql = "delete from ab where bId=" + id;
            statement.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addBook(Book b, Auphtor a) {
        try {
            var statement = connection.createStatement();
            String sql = "insert into books (name, year) values ('" + b.getName() + "','" + b.getYear() + "')";
            statement.execute(sql);
            sql = "select id from books where name='" + b.getName() + "'";
            var res = statement.executeQuery(sql);
            res.next();
            int id = res.getInt("id");
            sql = "insert into AB (aId, bId) values (" + a.getId() + ", " + id + ")";
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBook(Book b) {
        try {
            var statement = connection.createStatement();
            String sql = "update books set name='" + b.getName() + "', year=" + b.getYear() + " where id=" + b.getId();
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Auphtor getAuphtor(String name) {
        try {
            String sql = "select * from auphtors where name='" + name + "'";
            var statement = connection.createStatement();
            var result = statement.executeQuery(sql);
            if (result.next()) {
                String n = result.getString("name");
                int id = result.getInt("id");
                Auphtor a = new Auphtor(n, id);
                var statement2 = connection.createStatement();
                var result2 = statement2.executeQuery("select * from ab where aId=" + id);
                while (result2.next()) {
                    int bid = result2.getInt("bId");
                    var statement3 = connection.createStatement();
                    var res2 = statement3.executeQuery("select * from books where id=" + bid);
                    if (res2.next()) {
                        String bname = res2.getString("name");
                        int year = res2.getInt("year");
                        a.addBook(new Book(bname, year, bid));
                    }
                }
                return a;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAuphtor(int id) {
        try {
            var statement = connection.createStatement();
            String sql = "delete from auphtors where id=" + id;
            statement.execute(sql);
            sql = "delete from books where id in (select bId from ab where aId=" + id + ")";
            statement.execute(sql);
            sql = "delete from ab where aId=" + id;
            statement.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addAuphtor(Auphtor a) {
        try {
            var statement = connection.createStatement();
            String sql = "insert into auphtors (name) values ('" + a.getName() + "')";
            statement.execute(sql);
            var books = a.getBooks();
            for (int i = 0; i < books.size(); i++) {
                addBook(books.get(i), a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAuphtor(Auphtor a) {
        try {
            var statement = connection.createStatement();
            String sql = "update auphtors set name='" + a.getName() + "' where id=" + a.getId();
            statement.execute(sql);
            var books = a.getBooks();
            for (var b : books) {
                if (getBook(b.getName()).size() == 0)
                    addBook(b, a);
                else
                    updateBook(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String printAll() {
        List<Auphtor> lll = new ArrayList<>();
        try {
            var statement = connection.createStatement();
            String sql = "select * from auphtors";
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                String n = result.getString("name");
                int id = result.getInt("id");
                Auphtor a = new Auphtor(n, id);
                var statement2 = connection.createStatement();
                ResultSet result2 = statement2.executeQuery("select * from ab where aId=" + id);
                while (result2.next()) {
                    int bid = result2.getInt("bId");
                    var statement3 = connection.createStatement();
                    ResultSet res2 = statement3.executeQuery("select * from books where id=" + bid);
                    res2.next();
                    String bname = res2.getString("name");
                    int year = res2.getInt("year");
                    a.addBook(new Book(bname, year, bid));
                }
                lll.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lll.toString();
    }
}
