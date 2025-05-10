package GUI;

import chat.ChatMessage;

import javax.swing.*;
import java.awt.*;

public class ChatGUI extends JFrame { //VIEW

    private JTextArea chatArea;
    private JTextField messageInputField;
    private JButton sendButton;
    private JLabel idLabel;
    private JTextField targetIDInputField;
    private String targetID; // Variable zum Speichern
    private String ownHardwareId;

    public ChatGUI() {
        setTitle("Chatprogramm");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // zentriert das Fenster

        // Chat-Anzeige
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        // Eingabefeld + Senden Button
        messageInputField = new JTextField();
        sendButton = new JButton("Senden");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageInputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Info-Bereich (oben)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 1));
        infoPanel.setPreferredSize(new Dimension(500, 100));

        // ID-Anzeige
        ownHardwareId = HardwareIDGenerator.getShortHardwareID();
        idLabel = new JLabel(ownHardwareId, JLabel.CENTER);
        idLabel.setFont(new Font("Arial", Font.BOLD, 16));
        idLabel.setOpaque(true);
        idLabel.setBackground(Color.LIGHT_GRAY);

        // Zusätzlicher Eingabebereich (z. B. für Namen)
        JPanel extraInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("Verbinden mit:");
        targetIDInputField = new JTextField(20);

        // Suchen-Button
        JButton searchButton = new JButton("Verbinden");
        searchButton.addActionListener((_) -> App.getApp().selectPeer(targetIDInputField.getText().trim()));

        extraInputPanel.add(infoLabel);
        extraInputPanel.add(targetIDInputField);
        extraInputPanel.add(searchButton);

        infoPanel.add(idLabel);
        infoPanel.add(extraInputPanel);

        // Alles ins Hauptfenster
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(infoPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(inputPanel, BorderLayout.SOUTH);

        // Listener für Senden
        sendButton.addActionListener((_) -> App.getApp().sendMessage(messageInputField.getText().trim()));
        messageInputField.addActionListener((_) -> App.getApp().sendMessage(messageInputField.getText().trim()));
    }

    public void resetMessageInputField() {
        messageInputField.setText("");
    }

    public void setChatMessages(java.util.List<ChatMessage> messageHistory) {
        String userId = App.getApp().userId();
        StringBuilder chatAreaText = new StringBuilder();
        for (ChatMessage chatMessage : messageHistory) {
            chatAreaText.append(chatMessage.getSender().userId().equals(userId) ? "Du" : chatMessage.getSender().username());
            chatAreaText.append(": ");
            chatAreaText.append(chatMessage.getMessage());
            chatAreaText.append("\n");
        }
        chatArea.setText(chatAreaText.toString());
    }
}