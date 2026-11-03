import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            InetAddress address = InetAddress.getByName("127.0.0.1");
            Socket socket = new Socket(address, 1900);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            ClientGUI gui = new ClientGUI(in, out);

            JFrame frame = new JFrame("Client Calculator");
            frame.setContentPane(gui.getPanel1());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        out.println("END");
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            frame.setVisible(true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}