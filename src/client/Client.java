package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame implements ActionListener {

    private static final int PORT = 9000;
    private InetAddress host;
    private Socket connection;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private String username;
    private Message message;
    private boolean listening;

    JPanel loginView;
    private JTextField usernameText;
    private JButton loginBtn;

    JPanel chatView;
    private JTextArea msgs;
    private JTextField userMsg;
    private JButton send;
    private JButton exitBtn;

    public Client() {
        username = "not set";
        initLoginView();
//        initChatView(); //init se u action zbog username
        initMainView();
        connectToServer();
        listening = true;

//        listen(); //pomereno u performAction->login
    }

    private void initMainView() {
        setSize(400, 600);
        setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initLoginView() {
        loginView = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginBtn = new JButton("Log in");
        usernameText = new JTextField(25);
        loginView.add(new Label("Ukucajte vaše ime: "));
        loginView.add(usernameText);
        loginView.add(loginBtn);
        this.add(loginView);
        loginView.setVisible(true);
        loginBtn.addActionListener(this);
    }

    private void initChatView() {
        chatView = new JPanel(new FlowLayout(FlowLayout.CENTER));
        msgs = new JTextArea(25, 30);
        userMsg = new JTextField(30);
        send = new JButton("Send");
        exitBtn = new JButton("Exit");

        chatView.add(new Label("Konektovani ste kao: " + username + " napišite poruku:"));
        chatView.add(msgs);
        chatView.add(new JScrollPane(msgs), BorderLayout.CENTER);
        chatView.add(new Label("Napišite poruku:"));
        chatView.add(userMsg);
        chatView.add(send);
        chatView.add(exitBtn);
        send.addActionListener(this);
        exitBtn.addActionListener(this);
    }

    private void connectToServer() {
        try {
            host = InetAddress.getLocalHost();
            connection = new Socket(host, PORT);
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException ex) {
            System.out.println(ex);
        }
        System.out.println("Connected.");
    }

    private void listen() {
        do {
            try {
                message = (Message) in.readObject();
                msgs.append("\n" + message.toString());
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Error receving message: " + ex);
                msgs.append("\n" + "Greška u prijemu poruke!");
            } catch (NullPointerException npe) {
                System.out.println("NullPointer at listen: " + npe);
            }
        } while (listening);
    }

    private void sendMessage(final String msg) {
        String text = msg;
        message = new Message(username, text);
        try {
            out.writeObject(message);
        } catch (IOException ex) {
            System.out.println("Error sending message: " + ex);
            msgs.append("\n" + "Greška! Poruka nije poslata.");
        } catch (NullPointerException npe) {
            System.out.println("Null pointer at send: " + npe);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("Log in")) {
            String uname = usernameText.getText();
            if (uname != null && !uname.isEmpty()) {
                username = uname;
                initChatView();
                loginView.setVisible(false);
                this.remove(loginView);
                this.add(chatView);
                chatView.setVisible(true);
                chatView.repaint();
                listen();
            }
        }
        if (e.getActionCommand().equalsIgnoreCase("Exit")) {
            try {
                listening = false;
                in.close();
                out.close();
                connection.close();
            } catch (IOException ex) {
                System.out.println("Error closing connection: " + ex);
                msgs.append("\n" + "Greška u zatvaranju stream-ova ili socket-a.");
            } finally {
                System.exit(0);
            }
        } else if (e.getActionCommand().equalsIgnoreCase("Send")) {
            String msg = userMsg.getText();
            if (msg != null && !msg.isEmpty()) {
                sendMessage(userMsg.getText());
            }
        }
    }
}
