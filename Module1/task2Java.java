package modul;

import java.util.*;
import java.util.concurrent.*;

public class Main {
	
	public static void main(String args[]) {
		String [] bookName = {"Book1", "Book2", "Book3", "Book4", "Book5", "Book6"};
		List<Book> books = new ArrayList<Book>();
		for(int i = 0; i < bookName.length; i++) {
			books.add(new Book(bookName[i], (int)(Math.random()*2) == 0));
		}
		Thread [] t = new Thread[3];
		Library lib = new Library(books);
		Semaphore s = new Semaphore(1);
		for(int i = 0; i < 3; i++) {
			t[i] = new Thread(new Reader(lib, s));
			t[i].setName("Reader" + i);
			t[i].start();
		}
		for(int i = 0; i < 3; i++) {
			try{t[i].join();}catch (Exception e){};
		}
	}
}

class Book {
    private final String name;
    private boolean isforHome;
    private boolean readingNow = false;
    public Book(String s, boolean f) {
        name = s;
        isforHome = f;
    }
    public String getName() {
        return name;
    }
    public boolean forHome() {
        return isforHome;
    }
    public void readBook() {
        readingNow = true;
    }
    public  void stopRead() {
        readingNow = false;
    }
    public boolean isReading() {
        return readingNow;
    }
}

class Library {
    private List<Book> list;
    public Library(List<Book> l) {
        list = l;
    }
    public boolean getBookforHome(String name) {
    	for(int i = 0; i < list.size(); i++) {
    		if(list.get(i).getName() == name) {
    			if(list.get(i).isReading() != true) {
    				if(list.get(i).forHome() == true) {
    					list.get(i).readBook();
    					System.out.println(Thread.currentThread().getName()+" take for home: "+name);
    				} else {
    					System.out.println(Thread.currentThread().getName()+" can`t take for home(only room): "+name);
    					return false;
    				}
    				return true;
    			} else {
    				System.out.println(Thread.currentThread().getName()+" can't take for home: "+name);
    				return true;
    			}
    		}
    	}
    	return false;
    }
    public void getBookforRoom(String name) {
    	for(int i = 0; i < list.size(); i++) {
    		if(list.get(i).getName() == name) {
    			if(list.get(i).isReading() != true) {
    				list.get(i).readBook();
    				System.out.println(Thread.currentThread().getName()+" take for room: "+name);
    				return;
    			}
    			else {
    				System.out.println(Thread.currentThread().getName()+ " can't take for room: "+name);
    				return;
    			}
    		}
    	}
    }
    public void returnBook(String name) {
    	for(int i = 0; i < list.size(); i++) {
    		if(list.get(i).getName() == name && list.get(i).isReading() == true) {
    			list.get(i).stopRead();
    			System.out.println(Thread.currentThread().getName()+" return: "+name);
    			return;
    		}
    	}
    }
}

class Reader implements Runnable {
	private Semaphore s;
	private Library lib;
	public Reader(Library lib, Semaphore s) {
		this.lib = lib;
		this.s = s;
	}
	public boolean ReadBook(String name, int status) {
		try{
			s.acquire();
		}catch (Exception e){}
		boolean flag;
		if(status == 0) {
			flag = lib.getBookforHome(name);
		} else {
			lib.getBookforRoom(name);
			flag =  true;
		}
		s.release(1);
		return flag;
	}
	public void ReturnBook(String name) {
		try{
			s.acquire();
		}catch (Exception e){}
		lib.returnBook(name);
		s.release(1);
	}
	
	@Override
	public void run() {
		String [] bookName = {"Book1", "Book2", "Book3", "Book4", "Book5", "Book6"};
		List<String> mybooks = new ArrayList<String>();
		for(int i = 0; i < (int)(Math.random()*10+1); i++) {
			String k = bookName[(int)(Math.random()*6)];
			if(mybooks.contains(k) == false) {
				if(ReadBook(k, (int)(Math.random()*2)))
					mybooks.add(k);
				try{
					Thread.sleep(500*(int)(Math.random()*5));
				}
				catch (Exception e){};
			}
		}
		for(int i = 0; i < mybooks.size(); i++) {
			ReturnBook(mybooks.get(i));
			try{
				Thread.sleep(500*(int)(Math.random()*2));
			}
			catch (Exception e){};
		}
	}
}

