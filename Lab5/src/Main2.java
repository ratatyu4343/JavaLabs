import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;

public class Main2 {
    public static Random rand = new Random();
    public static void main(String[] args) {
        List<Thread> threadList = new ArrayList<>();
        StringBuilder [] strArr = {new StringBuilder("AAAA"),
                                   new StringBuilder("AAAA"),
                                   new StringBuilder("CCCC"),
                                   new StringBuilder("DDDD")};
        CyclicBarrier b = new CyclicBarrier(4, new StrEditorCheck(threadList, strArr));;
        for (int i = 0; i < 4; i++){
            threadList.add(new Thread(new StrEditor(strArr[i], b)));
            threadList.get(i).start();
        }
        for (var thread : threadList){
            try{thread.join();}catch (Exception e){};
        }
    }
}

class StrEditor implements Runnable {
    private StringBuilder s;
    private final CyclicBarrier barrier;

    public StrEditor(StringBuilder str, CyclicBarrier barrier){
        s = str;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()){
            for(int i = 0; i < s.length(); i++){
                if(Main2.rand.nextBoolean()){
                    switch (s.charAt(i)) {
                        case 'A':
                            s.setCharAt(i,'C');
                            break;
                        case 'B' :
                            s.setCharAt(i,'D');
                            break;
                        case 'C' :
                            s.setCharAt(i,'A');
                            break;
                        case 'D' :
                            s.setCharAt(i,'B');
                            break;
                    }
                }
            }
            try {
                barrier.await();
            } catch (Exception e){
                Thread.currentThread().interrupt();
            }
        }
    }
}

class StrEditorCheck implements Runnable {
    private final List<Thread> t;
    private final StringBuilder [] stringArr;
    public StrEditorCheck(List<Thread> t, StringBuilder [] stringArr){
        this.t = t;
        this.stringArr = stringArr;
    }

    @Override
    public void run() {
        for(var s : stringArr){
            System.out.print(s+" ");
        }
        System.out.print("\n");
        int[] aArr = new int[stringArr.length];
        int[] bArr = new int[stringArr.length];
        for (int i = 0; i < stringArr.length; i++) {
            for (int j = 0; j < stringArr[i].length(); j++) {
                switch (stringArr[i].charAt(j)) {
                    case 'A' -> aArr[i]++;
                    case 'B' -> bArr[i]++;
                }
            }
        }
        for (int i = 0; i < stringArr.length; i++) {
            int sumA = 0;
            int sumB = 0;
            for (int j = 0; j < stringArr.length; j++) {
                if(i != j){
                    sumA += aArr[j];
                    sumB += bArr[j];
                }
            }
            if(sumA == sumB){
                for(var thread : t){
                    thread.interrupt();
                }
            }
        }
    }
}