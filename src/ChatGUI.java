import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatGUI extends JFrame {
    static JTextArea chatArea;
    static JTextField inputField;
    static JButton sendButton;
    static JLabel idLabel;
    static JTextField infoInputField;
    static String targetID; // Variable zum Speichern
    static String ownID;
    

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

        // Zusätzlicher Eingabebereich (z. B. für Namen)
        JPanel extraInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("Verbinden mit:");
        infoInputField = new JTextField(20);

        // Suchen-Button
        JButton searchButton = new JButton("verbinden");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                targetID = infoInputField.getText().trim();
                System.out.println("Ziel ID: " + targetID);
                // Du kannst hier z. B. auch weitere Aktionen starten
            }
        });

        extraInputPanel.add(infoLabel);
        extraInputPanel.add(infoInputField);
        extraInputPanel.add(searchButton);

        infoPanel.add(idLabel);
        infoPanel.add(extraInputPanel);

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
