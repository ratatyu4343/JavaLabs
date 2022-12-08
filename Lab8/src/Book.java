import java.io.Serializable;

public class Book implements Serializable {
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
