package GUI;

import javax.swing.*;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.Enumeration;

public class HardwareIDGenerator {

    public static String getShortHardwareID() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();

                byte[] mac = ni.getHardwareAddress();
                if (mac != null && mac.length > 0) {
                    MessageDigest md = MessageDigest.getInstance("SHA-256");
                    byte[] hash = md.digest(mac);

                    StringBuilder sb = new StringBuilder();
                    for (byte b : hash) {
                        sb.append(String.format("%02X", b));
                    }
                    return sb.toString().substring(0,10);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JOptionPane.showConfirmDialog(null, "Hardware ID not available");
        System.exit(1);
        return null;
    }
}