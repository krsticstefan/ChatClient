package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame implements ActionListener {

    /* TODO:
     * odvojiti grafiku
     * moguciti unos IP-a. (?)
     */
    private static final int PORT = 9000;
    private InetAddress host;
    private Socket connection;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private String username, message;
    private boolean listening;
    private Thread t;

    private JPanel loginView, chatView;
    private JTextField usernameInput, msgInput, ipInput;
    private JButton loginBtn, sendBtn, exitBtn;
    private JTextArea chatMsgs;

    public Client() {
        username = "not set";
        initLoginView();
        initMainView();
        listening = true;
    }

    private void initLoginView() {
        loginView = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginBtn = new JButton("Log in");
        ipInput = new JTextField(30);
        usernameInput = new JTextField(30);
        loginView.add(new Label("Ukucajte vaše ime: "));
        loginView.add(usernameInput);
//        loginView.add(new Label("Ukucajte IP adresu servera: "));
//        loginView.add(ipInput);
        loginView.add(loginBtn);
        this.add(loginView);
        loginView.setVisible(true);
        loginBtn.addActionListener(this);
    }

    private void initMainView() {
        setSize(400, 600);
        setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initChatView() {
        chatView = new JPanel(new FlowLayout(FlowLayout.CENTER));
        chatMsgs = new JTextArea(25, 30);
        msgInput = new JTextField(30);
        sendBtn = new JButton("Send");
        exitBtn = new JButton("Exit");

        chatView.add(new Label("CHAT - Konektovani ste kao '" + username + "'."));
        chatView.add(chatMsgs);
        chatView.add(new JScrollPane(chatMsgs), BorderLayout.CENTER);
        chatView.add(new Label("Napišite poruku:"));
        chatView.add(msgInput);
        chatView.add(sendBtn);
        chatView.add(exitBtn);
        sendBtn.addActionListener(this);
        exitBtn.addActionListener(this);
    }

    private void connectToServer() {
        try {
            host = InetAddress.getLocalHost();
            connection = new Socket(host, PORT);
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
            chatMsgs.append("\n" + "Connected.");
            System.out.println("Connected.");
        } catch (IOException ex) {
            chatMsgs.append("\n" + "Server not running.");
            System.out.println("IOException in connectToServer: " + ex);
        } catch (NullPointerException npe) {
            chatMsgs.append("\n" + "Server not running.");
            System.out.println("NullPointer on connectToServer: " + npe);
        } catch (Exception ex) {
            System.out.println("Unkown exception in connectToServer: " + ex);
        }
    }

    private void listen() {
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (listening) {
                        message = (String) in.readObject();
                        chatMsgs.append("\n" + message);
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    chatMsgs.append("\n" + "Server is down.");
                    System.out.println("Error receving message: " + ex);
                } catch (NullPointerException npe) {
                    chatMsgs.append("\n" + "Server is down.");
                    System.out.println("NullPointer at Client-listen(): " + npe);
                }
            }
        });
        t.start();
    }

    private void sendMessage(final String msg) {
        message = username + msg;
        try {
            out.writeObject(message);
        } catch (IOException ex) {
            System.out.println("Error sending message: " + ex);
            chatMsgs.append("\n" + "Server is down, message not sent.");
        } catch (NullPointerException npe) {
            chatMsgs.append("\n" + "Server not running.");
            System.out.println("Null pointer at send: " + npe);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("Log in")) {
            String uname = usernameInput.getText();
            if (uname != null && !uname.isEmpty()) {
                username = uname;
                initChatView();
                connectToServer();
                sendMessage("");
                loginView.setVisible(false);
                this.remove(loginView);
                this.add(chatView);
                chatView.setVisible(true);
                listen();
            }
        }
        if (e.getActionCommand().equalsIgnoreCase("Exit")) {
            try {
                sendMessage("end123<>!");
                listening = false;
                in.close();
                out.close();
                connection.close();
                t.interrupt();
            } catch (IOException ex) {
                System.out.println("Error closing connection: " + ex);
                chatMsgs.append("\n" + "Greška u zatvaranju stream-ova ili socket-a.");
            } finally {
                System.exit(0);
            }
        } else if (e.getActionCommand().equalsIgnoreCase("Send")) {
            String msg = msgInput.getText();
            if (msg != null && !msg.isEmpty()) {
                sendMessage(": " + msgInput.getText());
                msgInput.setText("");
            }
        }
    }
}
