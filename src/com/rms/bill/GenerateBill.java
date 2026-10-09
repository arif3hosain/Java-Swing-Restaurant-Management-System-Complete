package com.rms.bill;

import com.rms.setting.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class GenerateBill extends JFrame {

    public GenerateBill() {
        super("Bill Generator");

        // Maximized full screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            setIconImage(ImageIO.read(new File(Utils.LOGO_PATH)));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, Utils.LOGO_NOT_FOUND);
        }

        setLayout(new BorderLayout());
        add(new POSBillingPanel(this), BorderLayout.CENTER);

        setVisible(true);
    }
}
