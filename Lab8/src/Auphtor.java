import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Auphtor implements Serializable {
    private String name;
    private final int id;
    private final List<Book> books;

    public Auphtor(String n, int id) {
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
