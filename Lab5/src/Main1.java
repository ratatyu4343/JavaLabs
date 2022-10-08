import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main1 {
    public static void main(String[] args) {
        int [] lad = new int[176];
        for (int i = 0; i < lad.length; i++) {
            lad[i] = (Math.abs(new Random().nextInt()))%2;
        }
        List<Thread> t = new ArrayList<>();
        Barier barier = new Barier(4, new Lad(lad, t));
        for(int i = 0; i < lad.length; i+=50){
            var th = new Thread(new PartOfLad(i, (lad.length-i)<50?lad.length:(i+50), lad, barier));
            t.add(th);
            th.start();
        }
        for(int i = 0; i < t.size(); i++){
            try{
                t.get(i).join();
            }catch (Exception e){}
        }
    }
}

class Barier
{
    private Runnable r;
    private final int count;
    private int count_now = 0;
    public Barier(int count, Runnable r) {
        this.count = count;
        this.r = r;
    }
    public synchronized void await() {
        count_now++;
        if(count_now == count) {
            count_now = 0;
            var t = new Thread(r);
            t.start();
            try {
                t.join();
            }catch (Exception e){
                Thread.currentThread().interrupt();
            }
            notifyAll();
        } else {
            try {
                wait();
            }catch (Exception e){
                Thread.currentThread().interrupt();
            }
        }
    }
}

class PartOfLad implements Runnable
{
    private int [] lad;
    private final int start;
    private final int end;
    private final Barier bar;
    public PartOfLad(int s, int e, int [] l, Barier b){
        start = s;
        end = e;
        lad = l;
        bar = b;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            boolean flag = true;
            while(flag){
                flag = false;
                for(int i = start; i < end-1; i++){
                    if(lad[i] == 1 && lad[i+1] == 0) {
                        flag = true;
                        lad[i] = 0;
                        lad[i+1] = 1;
                    }
                }
            }
            bar.await();
        }
    }
}

class Lad implements Runnable
{
    private final int [] lad;
    private final List<Thread> list;
    public Lad(int [] l, List<Thread> list){
        lad = l;
        this.list = list;
    }

    @Override
    public void run() {
        int count = (int)(lad.length / 50) + (lad.length % 50 > 0 ? 1 : 0);
        boolean flag = false;
        for (int n = 1; n < count; n++){
            if(lad[n*50-1] == 1 && lad[n*50] == 0) {
                flag = true;
                lad[n*50-1] = 0;
                lad[n*50] = 1;
            }
        }
        for (int j : lad) {
            System.out.print(j);
        }
        System.out.print("|\n");
        if(!flag) {
            for (Thread thread : list) {
                thread.interrupt();
            }
        }
    }
}