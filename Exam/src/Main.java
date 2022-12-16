import RMIUsing.ClientRmiTask2;
import RMIUsing.ServerRmiTask2;
import SocketUsing.ClientSocketTask2;
import SocketUsing.ServerSocketTask2;

import java.util.Scanner;

public class Main{
    public static String proxy = "localhost";
    public static int port = 122;

    public static void main(String[] args) throws Exception{
        Scanner scanner = new Scanner(System.in);
        System.out.print("1 - Socket\n2 - RMI\n>>> ");
        int nn = scanner.nextInt();
        if(nn == 1) {
            ServerSocketTask2 server = new ServerSocketTask2(port);
            server.setDaemon(true);
            server.start();

            ClientSocketTask2 client = new ClientSocketTask2(proxy, port);

            int n = 0;
            while (n != 3) {
                System.out.print("\n1 - Get by Alphavit\n2 - Get by CardNumber\n3 - Exit()\n>>>");
                n = scanner.nextInt();
                if (n == 1) {
                    client.getSortedCustomers();
                } else if (n == 2) {
                    System.out.print("Print a: ");
                    int a = scanner.nextInt();
                    System.out.print("Print b: ");
                    int b = scanner.nextInt();
                    client.getByCardId(a, b);
                }
            }
        }
        else if (nn == 2) {
            ServerRmiTask2 server = new ServerRmiTask2(port);
            ClientRmiTask2 client = new ClientRmiTask2(port);

            int n = 0;
            while (n != 3) {
                System.out.print("\n1 - Get by Alphavit\n2 - Get by CardNumber\n3 - Exit()\n>>>");
                n = scanner.nextInt();
                if (n == 1) {
                    client.getSortedCustomers();
                } else if (n == 2) {
                    System.out.print("Print a: ");
                    int a = scanner.nextInt();
                    System.out.print("Print b: ");
                    int b = scanner.nextInt();
                    client.getByCardId(a, b);
                }
            }
            System.exit(0);
        }
    }
}