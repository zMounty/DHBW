import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


 class SendMessageListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
        String message = ChatGUI.inputField.getText().trim();
        if (!message.isEmpty()) {
            ChatGUI.chatArea.append("Du: " + message + "\n");
            ChatGUI.inputField.setText("");
            // Beispiel: Zugriff auf infoInputField (Name)
            String name = ChatGUI.infoInputField.getText().trim();
            System.out.println("Gesendet von: " + (name.isEmpty() ? "Unbekannt" : name));
        }
    }
}
