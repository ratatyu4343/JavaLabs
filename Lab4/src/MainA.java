import java.util.Random;
import java.util.random.RandomGenerator;

public class MainA {
    public static void main(String[] args) throws Exception{
        ReadWriteLock r = new ReadWriteLock();
        DataBase d = new DataBase();
        Thread t1 = new Thread(new Writter(r, d));
        Thread t2 = new Thread(new Writter(r, d));
        Thread t3 = new Thread(new Reader(r, d));
        Thread t4 = new Thread(new Reader(r, d));
        Thread t5 = new Thread(new Reader(r, d));
        t1.start();t2.start();t3.start();t4.start();t5.start();
        t1.join();t2.join();t3.interrupt();t4.interrupt();t5.interrupt();
    }
}

class ReadWriteLock
{
    int readers = 0;
    boolean writers = false;
    public synchronized void readLock() throws Exception
    {
        while (writers) wait();
        readers++;
    }
    public synchronized void readUnlock()
    {
        readers -= readers > 0 ? 1 : 0;
        notifyAll();
    }
    public synchronized void writeLock() throws Exception
    {
        while (writers || readers > 0) wait();
        writers = true;
    }
    public synchronized void writeUnlock() throws Exception
    {
        writers = false;
        notifyAll();
    }
}

class DataBase
{
    String s = "";
    public String readLine()
    {
        return s;
    }
    public void writeLine(String new_s)
    {
        s = new_s;
    }
}

class Reader implements Runnable
{
    ReadWriteLock r;
    DataBase d;
    public Reader(ReadWriteLock r, DataBase d)
    {
        this.r = r;
        this.d = d;
    }
    @Override
    public void run()
    {
        while(true)
        {
            try {
                r.readLock();
                String s = d.readLine();
                System.out.println(Thread.currentThread().getName() + " прочитав " + s);
                r.readUnlock();
                Thread.sleep(1500);
            }catch (Exception e){return;}
        }
    }
}

class Writter implements Runnable
{
    ReadWriteLock r;
    DataBase d;
    public Writter(ReadWriteLock r, DataBase d)
    {
        this.r = r;
        this.d = d;
    }
    @Override
    public void run() {
        for(int i = 0; i < 10; i++) {
            try {
                r.writeLock();
                String s = Thread.currentThread().getName();
                d.writeLine(s);
                System.out.println(Thread.currentThread().getName() + " записав " + s);
                r.writeUnlock();
                Thread.sleep(2000);
            } catch (Exception e) {
            }
        }
    }
}