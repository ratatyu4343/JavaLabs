import com.ibm.mq.*;
import com.ibm.msg.client.wmq.compat.base.internal.MQC;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Work with \n1-Socket\n2-RMI\n3-JMS\n>>> ");
        int nn = scanner.nextInt();
        if(nn == 1) {
        Thread server = new MyServer();
        server.start();

        Socket socket = new Socket("127.0.0.1", 8888);
        ObjectInputStream inputStream;
        ObjectOutputStream outputStream;

        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream  = new ObjectInputStream(socket.getInputStream());

        while(true) {
            System.out.print("1-SHOW ALL\n2-FIND AUPHTOR\n3-ADD AUPHTOR\n4-DELETE AUPHTOR" +
                    "\n5-UPDATE AUPHTOR\n6-FIND BOOK\n7-ADD BOOK\n8-DELETE BOOK\n9-exit()\n>>>");
            int n = scanner.nextInt();
            outputStream.writeObject((Integer) n);
            Object objBuffer;
            switch (n) {
                case 1:{
                    objBuffer = (String) inputStream.readObject();
                    System.out.println(objBuffer);
                    break;
                }
                case 2:{
                    System.out.print("Input name: ");
                    scanner.nextLine();
                    String nam = scanner.nextLine();
                    outputStream.writeObject(nam);
                    System.out.print((Auphtor) inputStream.readObject());
                    break;
                }
                case 3:{
                    System.out.print("Input name: ");
                    scanner.nextLine();
                    String nam = scanner.nextLine();
                    Auphtor a = new Auphtor(nam, 0);
                    outputStream.writeObject(a);
                    break;
                }
                case 4:{
                    System.out.print("Input name: ");
                    scanner.nextLine();
                    String nam = scanner.nextLine();
                    outputStream.writeObject(nam);
                    break;
                }
                case 5:{
                    System.out.print("Input name to update: ");
                    scanner.nextLine();
                    String nam = scanner.nextLine();
                    outputStream.writeObject(nam);
                    break;
                }
                case 6:{
                    System.out.print("Input book's name: ");
                    scanner.nextLine();
                    String nam = scanner.nextLine();
                    outputStream.writeObject(nam);
                    List<Book> books = (List<Book>) inputStream.readObject();
                    System.out.print(books);
                    break;
                }
                case 7:{
                    System.out.print("Input book's name: ");
                    scanner.nextLine();
                    String nam = scanner.nextLine();
                    System.out.print("Input book's year: ");
                    int year = scanner.nextInt();
                    System.out.print("Input book's auphtor name: ");
                    scanner.nextLine();
                    String anam = scanner.nextLine();
                    outputStream.writeObject(anam);
                    Auphtor a = (Auphtor) inputStream.readObject();
                    if(a != null) {
                        Book b = new Book(nam, year, 0);
                        a.addBook(b);
                        outputStream.writeObject(a);
                    }
                    break;
                }
                case 8:{
                    System.out.print("Input book's name: ");
                    scanner.nextLine();
                    String nam = scanner.nextLine();
                    System.out.print("Input book's auphtor name: ");
                    String anam = scanner.nextLine();
                    outputStream.writeObject(anam);
                    outputStream.writeObject(nam);
                    break;
                }
                case 9:{
                    return;
                }
            }
        }
    }
        else if(nn == 2) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Registry r = LocateRegistry.createRegistry(122);
                        RMIDataBase db = new RMIDataBase("");
                        r.rebind("DB", db);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            while (true)
                try {
                    DB db = (DB) Naming.lookup("//localhost:122/DB");
                    while(true) {
                        System.out.print("1-SHOW ALL\n2-FIND AUPHTOR\n3-ADD AUPHTOR\n4-DELETE AUPHTOR" +
                                "\n5-UPDATE AUPHTOR\n6-FIND BOOK\n7-ADD BOOK\n8-DELETE BOOK\n9-exit()\n>>>");
                        int n = scanner.nextInt();
                        switch (n) {
                            case 1: {
                                System.out.println(db.printAll());
                                break;
                            }
                            case 2: {
                                Auphtor a;
                                System.out.print("Input name: ");
                                scanner.nextLine();
                                String nam = scanner.nextLine();
                                a = db.getAuphtor(nam);
                                System.out.print(a);
                                break;
                            }
                            case 3: {
                                System.out.print("Input name: ");
                                scanner.nextLine();
                                String nam = scanner.nextLine();
                                Auphtor a = new Auphtor(nam, 0);
                                db.addAuphtor(a);
                                break;
                            }
                            case 4: {
                                System.out.print("Input name: ");
                                scanner.nextLine();
                                String nam = scanner.nextLine();
                                Auphtor a = null;
                                a = db.getAuphtor(nam);
                                db.deleteAuphtor(a.getId());
                                break;
                            }
                            case 5: {
                                System.out.print("Input name: ");
                                scanner.nextLine();
                                String nam = scanner.nextLine();
                                Auphtor a = null;
                                a = db.getAuphtor(nam);
                                db.updateAuphtor(a);
                                break;
                            }
                            case 6: {
                                System.out.print("Input book's name: ");
                                scanner.nextLine();
                                String nam = scanner.nextLine();
                                List<Book> books = new ArrayList<>();
                                books = db.getBook(nam);
                                System.out.print(books);
                                break;
                            }
                            case 7: {
                                System.out.print("Input book's name: ");
                                scanner.nextLine();
                                String nam = scanner.nextLine();
                                System.out.print("Input book's year: ");
                                int year = scanner.nextInt();
                                System.out.print("Input book's auphtor name: ");
                                scanner.nextLine();
                                String anam = scanner.nextLine();
                                Auphtor a = null;
                                a = db.getAuphtor(anam);
                                if (a != null) {
                                    Book b = new Book(nam, year, 0);
                                    db.addBook(b, a);
                                }
                                break;
                            }
                            case 8: {
                                System.out.print("Input book's name: ");
                                scanner.nextLine();
                                String nam = scanner.nextLine();
                                System.out.print("Input book's auphtor name: ");
                                String anam = scanner.nextLine();
                                Auphtor a = null;
                                Book b;
                                b = db.getBook(nam).get(0);
                                db.delBook(b.getId());
                                break;
                            }
                            case 9: {
                                t.interrupt();
                                return;
                            }
                        }
                    }
                } catch (Exception e) {

                }
        } else if (nn == 3) {
            Thread server = new MyJMSServer();
            server.start();
            MQQueueManager manager = new  MQQueueManager("QM1");
            MQQueue out = manager.accessQueue("Q_OUT", MQC.MQOO_OUTPUT);
            MQQueue inp = manager.accessQueue("Q_INP", MQC.MQIA_DEF_INPUT_OPEN_OPTION);

            while(true) {
                MQMessage message = new MQMessage();
                MQMessage answer = new MQMessage();
                MQGetMessageOptions gmo = new MQGetMessageOptions();
                gmo.options = MQC.MQGMO_WAIT;
                gmo.waitInterval = MQC.MQEI_UNLIMITED;
                System.out.print("1-SHOW ALL\n2-FIND AUPHTOR\n3-ADD AUPHTOR\n4-DELETE AUPHTOR" +
                        "\n5-UPDATE AUPHTOR\n6-FIND BOOK\n7-ADD BOOK\n8-DELETE BOOK\n9-exit()\n>>>");
                int n = scanner.nextInt();
                message.writeInt(n);
                switch (n) {
                    case 1: {
                        out.put(message);
                        inp.get(answer, gmo);
                        String s = (String) answer.readObject();
                        System.out.println(s);
                        break;
                    }
                    case 2: {
                        System.out.print("Input name: ");
                        scanner.nextLine();
                        String nam = scanner.nextLine();
                        message.writeObject(nam);
                        out.put(message);
                        inp.get(answer, gmo);
                        Auphtor a = (Auphtor) answer.readObject();
                        System.out.print(a);
                        break;
                    }
                    case 3: {
                        System.out.print("Input name: ");
                        scanner.nextLine();
                        String nam = scanner.nextLine();
                        message.writeObject(nam);
                        out.put(message);
                        break;
                    }
                    case 4: {
                        System.out.print("Input name to delete: ");
                        scanner.nextLine();
                        String nam = scanner.nextLine();
                        message.writeObject(nam);
                        out.put(message);
                        break;
                    }
                    case 5: {
                        System.out.print("Input name to update: ");
                        scanner.nextLine();
                        String nam = scanner.nextLine();
                        message.writeObject(nam);
                        out.put(message);
                        inp.get(answer, gmo);
                        Auphtor a = (Auphtor) answer.readObject();
                        message.clearMessage();
                        message.writeObject(a);
                        out.put(message);
                        break;
                    }
                    case 6: {
                        System.out.print("Input book's name: ");
                        scanner.nextLine();
                        String nam = scanner.nextLine();
                        message.writeObject(nam);
                        out.put(message);
                        inp.get(answer, gmo);
                        List<Book> books = (List<Book>) answer.readObject();
                        System.out.print(books);
                        break;
                    }
                    case 7: {
                        System.out.print("Input book's name: ");
                        scanner.nextLine();
                        String nam = scanner.nextLine();
                        System.out.print("Input book's year: ");
                        int year = scanner.nextInt();
                        System.out.print("Input book's auphtor name: ");
                        scanner.nextLine();
                        String anam = scanner.nextLine();
                        message.writeObject(anam);
                        out.put(message);
                        inp.get(answer, gmo);
                        Auphtor a = (Auphtor) answer.readObject();
                        if (a != null) {
                            Book b = new Book(nam, year, 0);
                            message.clearMessage();
                            message.writeObject(b);
                            out.put(message);
                        }
                        break;
                    }
                    case 8: {
                        System.out.print("Input book's name: ");
                        scanner.nextLine();
                        String nam = scanner.nextLine();
                        System.out.print("Input book's auphtor name: ");
                        String anam = scanner.nextLine();
                        message.writeObject(nam);
                        out.put(message);
                        break;
                    }
                    case 9: {
                        return;
                    }
                }
            }
        }
    }
}

