package client;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Client extends JFrame implements ActionListener {

    private static final int PORT = 9000;
    private final String host = "127.0.0.1";
    private Socket connection;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private final String username;

    private JTextField usernameText;
    private JButton loginBtn;

    JPanel loginView;
    ChatView chatView;

    public Client() {
        username = "not set";
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loginView = new JPanel();
        loginView.setLayout(new GridLayout(3, 1));
        loginBtn = new JButton("Log in");
        usernameText = new JTextField();
        loginView.add(usernameText);
        loginView.add(loginBtn);
        this.add(loginView);
        loginView.setVisible(true);
        loginBtn.addActionListener(this);

        chatView = new ChatView();
//        this.add(chatView);
//        chatView.setVisible(false);

        setSize(350, 600);
        setVisible(true);

//        connectToServer();
//        sendMessage();
    }

    private void connectToServer() {
        try {
            connection = new Socket(host, PORT);
            in = new ObjectInputStream(connection.getInputStream());
            out = new ObjectOutputStream(connection.getOutputStream());
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private void sendMessage() {
        String msg = "";
        do {

        } while (!msg.equals("logout"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String uname = usernameText.getText();
        if (uname != null && !uname.isEmpty()) {
            loginView.setVisible(false);
            this.remove(loginView);
            this.add(chatView);
            chatView.setVisible(true);
        }
    }
}
