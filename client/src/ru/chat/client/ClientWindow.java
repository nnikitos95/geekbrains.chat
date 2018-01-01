package ru.chat.client;

import ru.network.TCPConnection;
import ru.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String IP_ADDR = "localhost";
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    private static int counter = 0;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldName = new JTextField();
    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;

    private ClientWindow() {

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEnabled(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);

        fieldName.setText("Аноним#");
        add(fieldName, BorderLayout.NORTH);

        setVisible(true);
        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            printMessage("TCPConnection exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if (msg.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(fieldName.getText() + " : " + msg);
    }

    @Override
    public void onConnectionReady(TCPConnection connection) {
        printMessage("Connection ready");
    }

    @Override
    public void onReceiveString(TCPConnection connection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection connection) {
        printMessage("Connection close");
    }

    @Override
    public void onException(TCPConnection connection, Exception e) {
        printMessage("TCPConnection exception: " + e);
    }

    private synchronized void printMessage(String value) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(value + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
