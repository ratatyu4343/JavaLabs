import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Library implements Serializable {
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
        for (var a : auphtors) {
            if (Objects.equals(a.getName(), nam))
                return a;
        }
        return null;
    }

    public List<Auphtor> getAuphtors() {
        return auphtors;
    }

    public List<Book> getBook(String nam) {
        List<Book> books = new ArrayList<>();
        for (var a : auphtors) {
            List<Book> myBooks = a.getBooks();
            for (var b : myBooks) {
                if (Objects.equals(nam, b.getName())) {
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
