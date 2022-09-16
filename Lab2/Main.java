
public class Main {
    public static int [][] mapa = new int[100][100];
    public static void main(String[] args)
    {
        mapa[(int) (Math.random() * (99))][(int) (Math.random() * (99))] = 1;
        for (int i = 0; i < 100; i++)
        {
            System.out.print(Integer.toString(i) + ") ");
            for(int j = 0; j < 100; j++)
                System.out.print(mapa[i][j]);
            System.out.print("\n");
        }
        Producer producer = new Producer(10);
        producer.findVini();
    }
}

class Que
{
    public Que(int N)
    {
        ids = new int[N];
        n = N;
    }
    private int lastId = -1;
    private int n = 0;
    private int[] ids;
    private boolean isFounded = false;
    public synchronized boolean pushId(int id)
    {
        if(lastId == n-1)
        {
            return false;
        }
        lastId += 1;
        ids[lastId] = id;
        return true;
    }
    public synchronized int popId()
    {
        if(lastId != -1)
        {
            lastId -= 1;
            return ids[lastId + 1];
        }
        return -1;
    }
    public boolean isFoundedItem()
    {
        return isFounded;
    }
    public void SetFounded()
    {
        isFounded = true;
    }
}

class Producer
{
    Producer(int count)
    {
       q = new Que(count);
       this.count = count;
    }
    public void findVini()
    {
        Thread[] threads = new Thread[count];
        for(int i = 0; i < count; i++)
        {
            threads[i] = new Thread(new Consumer(q));
            threads[i].start();
        }
        int id = 0;
        while (id != 100)
        {
            if(q.isFoundedItem()) break;
            if(!q.pushId(id)) {continue;}
            id++;
        }
        for (int i = 0; i < count; i++)
        {
            try {
                threads[i].join();
            } catch (Exception e)
            {}
        }
    }
    private int count;
    private Que q;
}

class Consumer implements Runnable
{
    private Que q;
    public Consumer(Que Q)
    {
        q = Q;
    }
    @Override
    public void run()
    {
        while (true)
        {
            if(q.isFoundedItem())
                return;
            int id = q.popId();
            if(id == -1)
                continue;
            for(int i = 0; i < 100; i++)
            {
                if (Main.mapa[id][i] == 1)
                {
                    q.SetFounded();
                    System.out.println(String.format("Vini is in (%d, %d)!!!", id, i));
                    return;
                }
            }
        }
    }
}