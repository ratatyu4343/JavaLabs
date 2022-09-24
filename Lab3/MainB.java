import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

class Salone
{
    private Semaphore s;
    private Semaphore s1;
    private Thread perukar;
    public Salone()
    {
        s = new Semaphore(1);
        s1 = new Semaphore(1);
        try {
            s1.acquire();
        }catch (Exception e){}
        perukar = new Thread(new Perukar(s, s1));
        perukar.start();
    }
    public void strizka()
    {
        try{s.acquire();}catch (Exception e){}
        if(perukar.getState() == Thread.State.WAITING)
        {
            System.out.println("Відвідувач розбудив перукаря!");
        }
        System.out.println("Відвідувач сів у крісло!");
        s1.release();
    }
}

class Perukar implements Runnable
{
    private Semaphore s;
    private Semaphore s1;
    Perukar(Semaphore s, Semaphore s1)
    {
        this.s = s;
        this.s1 = s1;
    }
    @Override
    public void run()
    {
        while (true) {
            try {
            s1.acquire();
            System.out.println("Перукар стриже!");
            Thread.sleep(1500);
            s.release();
            Thread.sleep(100);
            if(!s.hasQueuedThreads() && s.availablePermits() == 1)
            {
                System.out.println("Перукар заснув...");
            }
            } catch (Exception e) {}
        }
    }
}

class Vidviduvach implements Runnable
{
    Salone s;
    Vidviduvach(Salone s)
    {
        this.s = s;
    }
    @Override
    public void run()
    {
        s.strizka();
    }
}

public class MainB {
    public static void main(String argc[])
    {
        Salone salone = new Salone();
        for(int i = 0; i < 4; i++)
        {
            var vidvid = new Thread(new Vidviduvach(salone));
            vidvid.start();
            try{Thread.sleep((long)(Math.random() * 1000));}
            catch(Exception e){}
        }
        try{Thread.sleep(10000);}
        catch(Exception e){}
        var vidvid = new Thread(new Vidviduvach(salone));
        vidvid.start();
    }
}
