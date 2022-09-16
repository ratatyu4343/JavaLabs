import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class Main2 {
    public static ArrayBlockingQueue<Product> IvanovNechip = new ArrayBlockingQueue<Product>(5);
    public static ArrayBlockingQueue<Product> NechipPetrov = new ArrayBlockingQueue<Product>(5);
    public static void main(String[] args)
    {
        var IvanovThread = new Thread(new Ivanov(10));
        var PetrovThread = new Thread(new Petrov(10));
        var NechipThread = new Thread(new Nechip(10));
        IvanovThread.start();
        PetrovThread.start();
        NechipThread.start();
        try {
            IvanovThread.join();
            PetrovThread.join();
            NechipThread.join();
        }
        catch (Exception e){};
    }
}

class Product
{

}

class Ivanov implements Runnable
{
    private int n;
    public Ivanov(int N)
    {
        n = N;
    }
    @Override
    public void run()
    {
        var products = new Product[10];
        for(int i = 0; i < 10; i++)
        {
            products[i] = new Product();
        }
        for(int i = 0; i < n; i++)
        {

            try{
                System.out.println(String.format("Іванов виніс товар %d", i));
                Thread.sleep(1000);
            }
            catch (Exception e){};
            try{Main2.IvanovNechip.put(products[i]);}
            catch (Exception e){};

        }
    }
}

class Petrov implements Runnable
{
    private int count;
    public Petrov(int c)
    {
        count = c;
    }
    @Override
    public void run() {
        int c = 0;
        while (c != count)
        {
            Product p = null;
            try{p = Main2.NechipPetrov.take();}
            catch (Exception e){};
            c++;
            try {
                System.out.println(String.format("Петров вигрузив товар %d", c));
                Thread.sleep(1000);
            }
            catch (Exception e){};
        }
    }
}

class Nechip implements Runnable
{
    private int count;
    public Nechip(int c)
    {
        count = c;
    }
    @Override
    public void run() {
        int c = 0;
        while (c != count)
        {
            Product p = null;
            try{p = Main2.IvanovNechip.take();}
            catch (Exception e){};
            try {
                Main2.NechipPetrov.put(p);
            }
            catch (Exception e){};
            c++;
            try {
                System.out.println(String.format("Ничипоренко записав товар %d", c));
                Thread.sleep(2000);
            }
            catch (Exception e){};
        }
    }
}

