import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient {
    public static void main(String[] args) {
        final int CLIENT_PORT = 9002;
        final int SERVER_PORT = 9001;

        try {
            DatagramSocket socket = new DatagramSocket(CLIENT_PORT);
            InetAddress serverAddress = InetAddress.getByName("127.0.0.1");

            System.out.println("=== CLIENT PORNIT (Port " + CLIENT_PORT + ") ===");
            System.out.println("Scrie un mesaj și apasă ENTER pentru a trimite.\n");

            // THREAD PENTRU PRIMIRE MESAJE
            Thread receiverThread = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    while (true) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet); // Așteaptă mesaje
                        String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("\n[Server]: " + receivedMessage);
                    }
                } catch (Exception e) {
                    System.out.println("Eroare la primire: " + e.getMessage());
                }
            });
            receiverThread.start();

            // THREAD PRINCIPAL PENTRU TRIMITERE MESAJE
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String messageToSend = scanner.nextLine();
                if (!messageToSend.trim().isEmpty()) {
                    byte[] data = messageToSend.getBytes();
                    DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, SERVER_PORT);
                    socket.send(packet);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
