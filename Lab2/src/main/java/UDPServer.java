import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPServer {
    private JFrame frame;
    private JTextArea displayArea;
    private JTextField inputField;
    private JButton sendButton;

    private DatagramSocket socket;
    private InetAddress clientAddress;
    private final int SERVER_PORT = 9001; // Portul pe care ascultă serverul
    private final int CLIENT_PORT = 9002; // Portul către care trimite serverul

    private LinkedBlockingQueue<String> messageQueue;

    public UDPServer() {
        messageQueue = new LinkedBlockingQueue<>();
        try {
            socket = new DatagramSocket(SERVER_PORT);
            clientAddress = InetAddress.getByName("127.0.0.1");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erorr: " + e.getMessage());
            System.exit(1);
        }
        initializeGUI();
        startThreads();
    }

    private void initializeGUI() {
        frame = new JFrame("SERVER - UDP Chat (Port: " + SERVER_PORT + ")");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setLineWrap(true);
        frame.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        ActionListener sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = inputField.getText().trim();
                if (!message.isEmpty()) {
                    try {
                        messageQueue.put(message);
                        displayArea.append("Server : " + message + "\n");
                        inputField.setText("");
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };

        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction);

        frame.setLocation(200, 200); // Poziție fixă pe ecran
        frame.setVisible(true);
    }

    private void startThreads() {
        // Thread pentru PRIMIRE
        new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                    SwingUtilities.invokeLater(() -> displayArea.append("Client: " + receivedMessage + "\n"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Thread pentru TRIMITERE
        new Thread(() -> {
            try {
                while (true) {
                    String messageToSend = messageQueue.take();
                    byte[] data = messageToSend.getBytes();
                    DatagramPacket packet = new DatagramPacket(data, data.length, clientAddress, CLIENT_PORT);
                    socket.send(packet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        new UDPServer();
    }
}