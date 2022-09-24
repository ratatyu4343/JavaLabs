import java.sql.Array;

class Bear implements Runnable
{
    private Gorschik g;
    public Bear(Gorschik G)
    {
        g = G;
    }
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted())
        {
            g.Drink();
        }
    }
}

class Bee implements Runnable
{
    private Gorschik g;
    public Bee(Gorschik G)
    {
        g = G;
    }
    @Override
    public void run()
    {
        for(int i = 0; i < 2; i++)
        {
            g.PutHoney();
        }
    }
}

class Gorschik
{
    private int MaxN;
    private int N = 0;
    public Gorschik(int MaxN)
    {
        this.MaxN = MaxN;
    }
    public synchronized void PutHoney()
    {
        while(N >= MaxN)
        {
            try {
                wait();
            } catch (Exception e) {
            }
        }
        N += 1;
        if(N == MaxN)
        {
            notifyAll();
        }
        try {
            Thread.sleep(100);
        }catch (Exception e){}
        System.out.println("Бджола " + Thread.currentThread().getName() + " внесла одиницю меду!");
    }
    public synchronized void  Drink()
    {
        while (N != MaxN)
        {
            try {
                wait();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
        N = 0;
        System.out.println("Ведмідь випив мед!");
        notifyAll();
    }
}

public class MainA {
    static public void main(String argc[])
    {
        Gorschik g = new Gorschik(3);
        Thread bee[] = new Thread[6];
        for(int i = 0; i < 6; i++)
        {
            bee[i] = new Thread(new Bee(g));
        }
        Thread bear = new Thread(new Bear(g));
        bear.start();
        for(int i = 0; i < 6; i++)
        {
            bee[i].start();
        }
        for(int i = 0; i < 6; i++)
        {
            try{bee[i].join();}catch (Exception e){};
        }
        try {
            for(int i = 0; i < 6; i++)
            {
                bee[i].interrupt();
            }
            bear.interrupt();
        }catch (Exception e){};
    }
}

