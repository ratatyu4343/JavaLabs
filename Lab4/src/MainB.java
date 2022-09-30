import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.nio.file.Files;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MainB {
    final static int N = 10;
    final static Random random = new Random();
    public static void main(String[] args) throws Exception{
        int[][] arr = new int[N][N];
        for(int i = 0; i < N; i++)
        {
            for(int j = 0; j < N; j++)
            {
                arr[i][j] = 0;
            }
        }
        var r = new ReentrantReadWriteLock(true);
        Lock readL = r.readLock();
        Lock wtireL = r.writeLock();
        Thread nature = new Thread(new Nature(arr, wtireL));
        Thread sadivnyk = new Thread(new Sadyvnyk(arr, wtireL));
        Thread monitor1 = new Thread(new Monitor1(arr, readL));
        Thread monitor2 = new Thread(new Monitor2(arr, readL));
        nature.start();sadivnyk.start();monitor1.start();monitor2.start();
        Thread.sleep(1000*10);
        nature.interrupt();sadivnyk.interrupt();monitor1.interrupt();monitor2.interrupt();
    }
}

class Sadyvnyk implements Runnable
{
    int[][] arr;
    Lock writeLock;
    public Sadyvnyk(int[][] arr, Lock writeLock)
    {
        this.writeLock = writeLock;
        this.arr = arr;
    }

    @Override
    public void run() {
        while (true)
        {
            try
            {
                writeLock.lock();
                for(int i = 0; i < MainB.N; i++)
                {
                    for(int j = 0; j < MainB.N; j++)
                    {
                        if(arr[i][j] == 1)
                        {
                            arr[i][j] = 0;
                        }
                    }
                }
                writeLock.unlock();
                Thread.sleep(1500);
            }catch (Exception e){return;}
        }
    }
}

class Nature implements Runnable
{
    int[][] arr;
    Lock writeLock;
    public Nature(int[][] arr, Lock writeLock)
    {
        this.writeLock = writeLock;
        this.arr = arr;
    }

    @Override
    public void run() {
        while (true)
        {
            try
            {
                writeLock.lock();
                for(int i = 0; i < MainB.random.nextInt(MainB.N); i++)
                {
                    for(int j = 0; j < MainB.random.nextInt(MainB.N); j++)
                    {
                        if(MainB.random.nextBoolean())
                        {
                            arr[i][j] = 1;
                        }
                    }
                }
                writeLock.unlock();
                Thread.sleep(500);
            }catch (Exception e){return;}
        }
    }
}

class Monitor1 implements Runnable
{
    int[][] arr;
    Lock readeLock;
    public Monitor1(int[][] arr, Lock readeLock)
    {
        this.readeLock = readeLock;
        this.arr = arr;
    }

    @Override
    public void run() {
        try{Files.write(Paths.get("myfile12345.txt"),"".getBytes());}catch (Exception e){}
        while (true)
        {
            try
            {
                String s = "";
                readeLock.lock();
                for(int i = 0; i < MainB.N; i++)
                {
                    for(int j = 0; j < MainB.N; j++)
                    {
                        s += arr[i][j];
                        s += " ";
                    }
                    s += "\n";
                }
                s += "==================\n";
                Files.write(Paths.get("myfile12345.txt"), s.getBytes(), StandardOpenOption.APPEND);
                readeLock.unlock();
                Thread.sleep(4000);
            }catch (Exception e){return;}
        }
    }
}

class Monitor2 implements Runnable
{
    int[][] arr;
    Lock readeLock;
    public Monitor2(int[][] arr, Lock readeLock)
    {
        this.readeLock = readeLock;
        this.arr = arr;
    }

    @Override
    public void run() {
        while (true)
        {
            try
            {
                String s = "";
                readeLock.lock();
                for(int i = 0; i < MainB.N; i++)
                {
                    for(int j = 0; j < MainB.N; j++)
                    {
                        s += arr[i][j];
                        s += " ";
                    }
                    s += "\n";
                }
                System.out.println(s);
                System.out.println("====================");
                readeLock.unlock();
                Thread.sleep(1000);
            }catch (Exception e){return;}
        }
    }
}
