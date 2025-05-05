import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatGUI extends JFrame {
    static JTextArea chatArea;
    static JTextField inputField;
    static JButton sendButton;
    static JLabel idLabel;
    static JLabel connectionLabel;
    static JTextField infoInputField;
    static String targetID; // Variable zum Speichern
    static String ownID;
    JPanel combinedInputPanel = new JPanel(new GridLayout(1, 2));
    private JTextField connectedIdField;
    

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
        inputField = new JTextField();
        sendButton = new JButton("Senden");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Info-Bereich (oben)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 1));
        infoPanel.setPreferredSize(new Dimension(500, 100));

        // ID-Anzeige
        ownID= HardwareIDGenerator.getHardwareID();
        String shortID=ownID.substring(0,10);
        System.out.println(shortID);
        idLabel = new JLabel(shortID, JLabel.CENTER);
        idLabel.setFont(new Font("Arial", Font.BOLD, 16));
        idLabel.setOpaque(true);
        idLabel.setBackground(Color.LIGHT_GRAY);

        // Linker Teil: Name + Suchen
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nameLabel = new JLabel("ID:");
        infoInputField = new JTextField(12);
        JButton searchButton = new JButton("Verbinden");
        searchButton.addActionListener(e -> {
            targetID = infoInputField.getText().trim();
            System.out.println("targetId: " + targetID);
        });
        namePanel.add(nameLabel);
        namePanel.add(infoInputField);
        namePanel.add(searchButton);

        /* 
        //  Connectetde ID anzeigen
        connectedID= "...";
        connectionLabel = new JLabel(connectedID, JLabel.CENTER);
        connectionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        connectionLabel.setOpaque(true);
        connectionLabel.setBackground(Color.LIGHT_GRAY);
        */

        
        // Zusätzlicher Eingabebereich (z. B. für Namen)
        JPanel extraInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));


        // Rechter Teil: Verbundene ID
        JPanel connectedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel connectedLabel = new JLabel("Verbundene ID:");
        connectedIdField = new JTextField("XXX", 12);
        connectedIdField.setEditable(false);
        connectedPanel.add(connectedLabel);
        connectedPanel.add(connectedIdField);


        //combinedInputPanel.add(namePanel);
        combinedInputPanel.add(connectedPanel);
        infoPanel.add(idLabel);
        infoPanel.add(combinedInputPanel);
        extraInputPanel.add(infoInputField);
        extraInputPanel.add(searchButton);

        
        infoPanel.add(idLabel);
        infoPanel.add(extraInputPanel);
        //infoPanel.add(connectionLabel);

        // Alles ins Hauptfenster
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(infoPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(inputPanel, BorderLayout.SOUTH);

        // Listener für Senden
        sendButton.addActionListener(new SendMessageListener());
        inputField.addActionListener(new SendMessageListener());
    }
}