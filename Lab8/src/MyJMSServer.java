import com.ibm.mq.*;
import com.ibm.msg.client.wmq.compat.base.internal.MQC;

public class MyJMSServer extends Thread {
    private DataBase db;
    MQQueueManager manager;
    private MQQueue QInp = null;
    private MQQueue QOut = null;

    public MyJMSServer() throws MQException {
        manager = new MQQueueManager("QM1");
        QInp = manager.accessQueue("Q_OUT", MQC.MQOO_INPUT_EXCLUSIVE);
        QOut = manager.accessQueue("Q_INP", MQC.MQOO_OUTPUT);
        db = new DataBase("");
    }

    @Override
    public void run() {
        while(true) {
            try {
                MQGetMessageOptions gmo = new MQGetMessageOptions();
                gmo.options = MQC.MQGMO_WAIT;
                gmo.waitInterval = MQC.MQEI_UNLIMITED;

                MQMessage message = new MQMessage();
                QInp.get(message, gmo);

                int n = message.readInt();
                MQMessage answer = new MQMessage();
                switch (n) {
                    case 1: {
                        answer.writeObject(db.printAll());
                        QOut.put(answer);
                        break;
                    }
                    case 2: {
                        String s = (String) message.readObject();
                        answer.writeObject(db.getAuphtor(s));
                        QOut.put(answer);
                        break;
                    }
                    case 3: {
                        db.addAuphtor(new Auphtor((String) message.readObject(), 0));
                        break;
                    }
                    case 4: {
                        db.deleteAuphtor(db.getAuphtor((String) message.readObject()).getId());
                        break;
                    }
                    case 5: {
                        answer.writeObject(db.getAuphtor((String) message.readObject()));
                        QOut.put(answer);
                        message.clearMessage();
                        QInp.get(message, gmo);
                        db.updateAuphtor(db.getAuphtor((String) message.readObject()));
                        break;
                    }
                    case 6: {
                        String name = (String) message.readObject();
                        answer.writeObject(db.getBook(name));
                        QOut.put(answer);
                        break;
                    }
                    case 7: {
                        Auphtor a = db.getAuphtor((String) message.readObject());
                        answer.writeObject(a);
                        QOut.put(answer);
                        if (a != null) {
                            message.clearMessage();
                            QInp.get(message, gmo);
                            a.addBook((Book) message.readObject());
                            db.updateAuphtor(a);
                        }
                        break;
                    }
                    case 8: {
                        String nam = (String) message.readObject();
                        db.delBook(db.getBook(nam).get(0).getId());
                        break;
                    }
                    case 9: {
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }

        try {
            QInp.close();
            QOut.close();
        } catch (MQException e) {
            e.printStackTrace();
        }
    }
}
