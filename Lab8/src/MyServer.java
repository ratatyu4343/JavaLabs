import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class MyServer extends Thread{
    DataBase db;
    ServerSocket socket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;

    public MyServer() {
        db = new DataBase("");

        try {
            socket = new ServerSocket(8888);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            Socket user = socket.accept();
            inputStream = new ObjectInputStream(user.getInputStream());
            outputStream = new ObjectOutputStream(user.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        while(true) {
            try {
                int n = (Integer) inputStream.readObject();
                switch (n) {
                    case 1: {
                        outputStream.writeObject(db.printAll());
                        break;
                    }
                    case 2: {
                        outputStream.writeObject(db.getAuphtor((String) inputStream.readObject()));
                        break;
                    }
                    case 3: {
                        db.addAuphtor((Auphtor) inputStream.readObject());
                        break;
                    }
                    case 4: {
                        db.deleteAuphtor(db.getAuphtor((String) inputStream.readObject()).getId());
                        break;
                    }
                    case 5: {
                        db.updateAuphtor(db.getAuphtor((String) inputStream.readObject()));
                        break;
                    }
                    case 6: {
                        outputStream.writeObject(db.getBook((String) inputStream.readObject()));
                        break;
                    }
                    case 7: {
                        outputStream.writeObject(db.getAuphtor((String) inputStream.readObject()));
                        Auphtor a = (Auphtor) inputStream.readObject();
                        db.updateAuphtor(a);
                        break;
                    }
                    case 8: {
                        String anam = (String) inputStream.readObject();
                        String nam = (String) inputStream.readObject();
                        db.delBook(db.getBook(nam).get(0).getId());
                        break;
                    }
                    case 9: {
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
