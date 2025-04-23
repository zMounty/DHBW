import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) throws Exception {
        String hardwareID = HardwareIDGenerator.getHardwareID();
            System.out.println("Hardware ID: " + hardwareID);
       SwingUtilities.invokeLater(() -> {
            ChatGUI gui = new ChatGUI();
            gui.setVisible(true);
            
        });
    }
    }
