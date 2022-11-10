import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.lang.model.type.NullType;
import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

public class Main {
    static public Library readXML(String path){
        Library l = new Library();
        try {
            var fabryk = DocumentBuilderFactory.newInstance();
            var bilder = fabryk.newDocumentBuilder();
            var doc = bilder.parse(path);
            var auphtors = doc.getElementsByTagName("auphtor");
            for(int i = 0; i < auphtors.getLength(); i++) {
                var auphtorData = auphtors.item(i);
                var aAtributes = auphtorData.getAttributes();
                String aName = aAtributes.getNamedItem("name").getNodeValue();
                int aId = Integer.parseInt(aAtributes.getNamedItem("id").getNodeValue());
                Auphtor auphtor = new Auphtor(aName, aId);
                var books = auphtorData.getChildNodes();
                for (int j = 0; j < books.getLength(); j++) {
                    var bookData = books.item(j);
                    if(bookData.getNodeType() == Node.ELEMENT_NODE) {
                        var bookAtributes = bookData.getAttributes();
                        String name = bookData.getTextContent();
                        name = name.replace(" ", "").replace("\n", "");
                        name = name.replace("\"", "");
                        int year = Integer.parseInt((bookAtributes.getNamedItem("year").getNodeValue()));
                        int id = Integer.parseInt(bookAtributes.getNamedItem("id").getNodeValue());
                        Book b = new Book(name, year, id);
                        auphtor.addBook(b);
                    }
                }
                l.addAuphtor(auphtor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }
    static public void writeXML(String path, Library library) {
        try {
            var fabryk = DocumentBuilderFactory.newInstance();
            var bilder = fabryk.newDocumentBuilder();
            var doc = bilder.newDocument();
            var root = doc.createElement("library");
            var auphtors = library.getAuphtors();
            for (Auphtor item : auphtors) {
                var auphtorXML = doc.createElement("auphtor");
                auphtorXML.setAttribute("id", String.valueOf(item.getId()));
                auphtorXML.setAttribute("name", item.getName());
                var books = item.getBooks();
                for (Book value : books) {
                    var bookXML = doc.createElement("book");
                    bookXML.setTextContent(value.getName());
                    bookXML.setAttribute("id", String.valueOf(value.getId()));
                    bookXML.setAttribute("year", String.valueOf(value.getYear()));
                    auphtorXML.appendChild(bookXML);
                }
                root.appendChild(auphtorXML);
            }
            doc.appendChild(root);
            var tFactory = TransformerFactory.newInstance().newTransformer();
            var src = new DOMSource(doc);
            var fos = new FileOutputStream(path);
            var sres = new StreamResult(fos);
            tFactory.transform(src, sres);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Library XMLlibrary = null;
        DataBase db = null;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Type of work 1-XML\n2-MySQL\n>>>");
        int type = scanner.nextInt();
        if(type == 1) {
            XMLlibrary = readXML("library.xml");
        }
        else {
            db = new DataBase("library");
        }
        while(true) {
            System.out.print("1-SHOW ALL\n2-FIND AUPHTOR\n3-ADD AUPHTOR\n4-DELETE AUPHTOR" +
                    "\n5-UPDATE AUPHTOR\n6-FIND BOOK\n7-ADD BOOK\n8-DELETE BOOK\n9-exit()\n>>>");
            int n = scanner.nextInt();
            switch (n) {
                case 1:{
                    if(type == 1)
                        System.out.print(XMLlibrary);
                    else
                        db.printAll();
                    break;
                }
                case 2:{
                    Auphtor a;
                    System.out.print("Input name: ");
                    scanner.nextLine();
                    String nam = scanner.nextLine();
                    if(type == 1) {
                        a = XMLlibrary.getAuphtor(nam);
                    }
                    else {
                        a = db.getAuphtor(nam);
                    }
                    System.out.print(a);
                    break;
                }
                case 3:{
                    System.out.print("Input name: ");
                    scanner.nextLine();
                    String nam = scanner.nextLine();
                    Auphtor a = new Auphtor(nam, 0);
                    if(type == 1) {
                        XMLlibrary.addAuphtor(a);
                    }
                    else {
                        db.addAuphtor(a);
                    }
                    break;
                }
                case 4:{
                    System.out.print("Input name: ");
                    scanner.nextLine();
                    String nam = scanner.nextLine();
                    Auphtor a = null;
                    if(type == 1) {
                        a = XMLlibrary.getAuphtor(nam);
                        XMLlibrary.delAuphtor(a);
                    }
                    else {
                        a = db.getAuphtor(nam);
                        db.deleteAuphtor(a.getId());
                    }
                    break;
                }
                case 5:{
                    System.out.print("Input name: ");
                    scanner.nextLine();
                    String nam = scanner.nextLine();
                    Auphtor a = null;
                    if(type == 1) {
                        a = XMLlibrary.getAuphtor(nam);
                        XMLlibrary.delAuphtor(a);
                        XMLlibrary.addAuphtor(a);
                    }
                    else {
                        a = db.getAuphtor(nam);
                        db.updateAuphtor(a);
                    }
                    break;
                }
                case 6:{
                    System.out.print("Input book's name: ");
                    scanner.nextLine();
                    String nam = scanner.nextLine();
                    List<Book> books = new ArrayList<>();
                    if(type == 1) {
                        books = XMLlibrary.getBook(nam);
                    }
                    else {
                        books = db.getBook(nam);
                    }
                    System.out.print(books);
                    break;
                }
                case 7:{
                    System.out.print("Input book's name: ");
                    scanner.nextLine();
                    String nam = scanner.nextLine();
                    System.out.print("Input book's year: ");
                    int year = scanner.nextInt();
                    System.out.print("Input book's auphtor name: ");
                    scanner.nextLine();
                    String anam = scanner.nextLine();
                    Auphtor a = null;
                    if(type == 1) {
                        a = XMLlibrary.getAuphtor(anam);
                    } else {
                        a = db.getAuphtor(anam);
                    }
                    if(a != null) {
                        if(type == 1) {
                            int s = a.getBooks().size();
                            Book b = new Book(nam, year, s > 0 ? a.getBooks().get(s - 1).getId() + 1 : year + a.getId());
                            a.addBook(b);
                        } else {
                            Book b = new Book(nam, year, 0);
                            db.addBook(b, a);
                        }
                    }
                    break;
                }
                case 8:{
                    System.out.print("Input book's name: ");
                    scanner.nextLine();
                    String nam = scanner.nextLine();
                    System.out.print("Input book's auphtor name: ");
                    String anam = scanner.nextLine();
                    Auphtor a = null;
                    Book b;
                    if(type == 1) {
                        a = XMLlibrary.getAuphtor(anam);
                        if(a != null) {
                            b = XMLlibrary.getBook(nam).get(0);
                            a.delBook(b);
                        }
                    } else {
                        b = db.getBook(nam).get(0);
                        db.delBook(b.getId());
                    }
                    break;
                }
                case 9:{
                    if(type == 1)
                        writeXML("library.xml", XMLlibrary);
                    return;
                }
            }
        }
    }
}

class DataBase {
    private Connection connection;
    public DataBase(String n) {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "1111");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<Book> getBook(String nam) {
        List<Book> books = new ArrayList<>();
        try{
            var statement = connection.createStatement();
            String sql = "select * from books where name='"+nam+"'";
            var result = statement.executeQuery(sql);
            while(result.next()) {
                String name = result.getString("name");
                int id = result.getInt("id");
                int year = result.getInt("year");
                books.add(new Book(name, year, id));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }
    public void delBook(int id) {
        try{
            var statement = connection.createStatement();
            String sql = "delete from books where id="+id;
            statement.execute(sql);
            sql = "delete from ab where bId="+id;
            statement.execute(sql);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addBook(Book b, Auphtor a) {
        try {
            var statement = connection.createStatement();
            String sql = "insert into books (name, year) values ('"+b.getName()+"','"+b.getYear()+"')";
            statement.execute(sql);
            sql = "select id from books where name='"+b.getName()+"'";
            var res = statement.executeQuery(sql);
            res.next();
            int id = res.getInt("id");
            sql = "insert into AB (aId, bId) values ("+a.getId()+", "+id+")";
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateBook(Book b) {
        try {
            var statement = connection.createStatement();
            String sql = "update books set name='"+b.getName()+"', year="+b.getYear()+" where id="+b.getId();
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Auphtor getAuphtor(String name) {
        try{
            String sql = "select * from auphtors where name='"+name+"'";
            var statement = connection.createStatement();
            var result = statement.executeQuery(sql);
            if(result.next()) {
                String n = result.getString("name");
                int id = result.getInt("id");
                Auphtor a = new Auphtor(n, id);
                var statement2 = connection.createStatement();
                var result2 = statement2.executeQuery("select * from ab where aId="+id);
                while (result2.next()) {
                    int bid = result2.getInt("bId");
                    var statement3 = connection.createStatement();
                    var res2 = statement3.executeQuery("select * from books where id="+bid);
                    if(res2.next()) {
                        String bname = res2.getString("name");
                        int year = res2.getInt("year");
                        a.addBook(new Book(bname, year, bid));
                    }
                }
                return a;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public void deleteAuphtor(int id) {
        try {
            var statement = connection.createStatement();
            String sql = "delete from auphtors where id=" + id;
            statement.execute(sql);
            sql = "delete from books where id in (select bId from ab where aId="+id+")";
            statement.execute(sql);
            sql = "delete from ab where aId="+id;
            statement.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addAuphtor(Auphtor a) {
        try {
            var statement = connection.createStatement();
            String sql = "insert into auphtors (name) values ('"+a.getName()+"')";
            statement.execute(sql);
            var books = a.getBooks();
            for(int i =0; i < books.size(); i++) {
                addBook(books.get(i), a);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void updateAuphtor(Auphtor a) {
        try {
            var statement = connection.createStatement();
            String sql = "update auphtors set name=" + a.getName() + " where id=" + a.getId();
            statement.execute(sql);
            var books = a.getBooks();
            for(var b : books) {
                updateBook(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void printAll() {
        List<Auphtor> lll = new ArrayList<>();
        try{
            var statement = connection.createStatement();
            String sql = "select * from auphtors";
            ResultSet result = statement.executeQuery(sql);
            while(result.next()) {
                String n = result.getString("name");
                int id = result.getInt("id");
                Auphtor a = new Auphtor(n, id);
                var statement2 = connection.createStatement();
                ResultSet result2 = statement2.executeQuery("select * from ab where aId="+id);
                while (result2.next()) {
                    int bid = result2.getInt("bId");
                    var statement3 = connection.createStatement();
                    ResultSet res2 = statement3.executeQuery("select * from books where id="+bid);
                    res2.next();
                    String bname = res2.getString("name");
                    int year = res2.getInt("year");
                    a.addBook(new Book(bname, year, bid));
                }
                lll.add(a);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print(lll);
    }
}

class Library {
    private final List<Auphtor> auphtors;
    public Library() {
        auphtors = new ArrayList<Auphtor>();
    }
    public void addAuphtor(Auphtor a) {
        auphtors.add(a);
    }
    public void delAuphtor(Auphtor a) {
        auphtors.remove(a);
    }
    public Auphtor getAuphtor(String nam) {
        for(var a : auphtors) {
            if(Objects.equals(a.getName(), nam))
                return a;
        }
        return null;
    }
    public List<Auphtor> getAuphtors() {
        return auphtors;
    }
    public List<Book> getBook(String nam) {
        List<Book> books = new ArrayList<>();
        for(var a : auphtors) {
            List<Book> myBooks = a.getBooks();
            for(var b : myBooks) {
                if(Objects.equals(nam, b.getName())){
                    books.add(b);
                }
            }
        }
        return books;
    }
    @Override
    public String toString() {
        return "Library{\n" + auphtors + "\n}";
    }
}

class Auphtor {
    private String name;
    private final int id;
    private final List<Book> books;
    public Auphtor (String n, int id) {
        name = n;
        this.id = id;
        books = new ArrayList<Book>();
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }
    public void addBook(Book b) {
        books.add(b);
    }
    public void delBook(Book b) {
        books.remove(b);
    }
    public List<Book> getBooks() {
        return books;
    }

    @Override
    public String toString() {
        return "\tAuphtor{" +
                "\n\t\tname='" + name + '\'' +
                "\n\t\tid=" + id +
                "\n\t\tbooks=" + books +
                "}\n";
    }
}

class Book {
    private String name;
    private final int id;
    private int year;
    public Book(String n, int y, int id) {
        name = n;
        year = y;
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "\n\t\t\t{name=" + name.replace("\n", "").replace(" ", "") +
                "\n\t\t\tid=" + id +
                "\n\t\t\tyear=" + year +
                "}\n";
    }
}