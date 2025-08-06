package com.rms;

import com.rms.service.AppService;
import com.rms.setting.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Frame1 {

	private JFrame mainFrame;
	private static int count = 0;
	JLabel idLabel;
	JLabel passLabel;
	JLabel headerLabel;
	JLabel devInfo;
	JTextField id;
	JPasswordField pass;
	JButton submit;
	JPanel leftPanel;
	JPanel rightPanel;
	JLabel logoLabel;

	public Frame1(){
		prepareGUI();
	}

	private void prepareGUI(){
		mainFrame = new JFrame("Authorize identity");
		mainFrame.setSize(900,500);
		mainFrame.setResizable(false);
		mainFrame.setLayout(new BorderLayout());
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set window icon
		try{
			// Try loading from resources first (for JAR files)
			InputStream imageStream = getClass().getResourceAsStream(Utils.LOGO_PATH);
			if (imageStream != null) {
				mainFrame.setIconImage(ImageIO.read(imageStream));
				imageStream.close();
			} else {
				// Fallback to file system (for development)
				mainFrame.setIconImage(ImageIO.read(new File(Utils.LOGO_PATH)));
			}
		}
		catch (Exception ex){
			JOptionPane.showMessageDialog(null,Utils.LOGO_NOT_FOUND);
		}

		// Create left panel (40% width)
		leftPanel = new JPanel();
		leftPanel.setLayout(null);
		leftPanel.setBackground(new Color(70, 130, 180)); // Steel blue color
		leftPanel.setPreferredSize(new Dimension(360, 500)); // 40% of 900px width

		// Add company logo to left panel
		logoLabel = new JLabel();
		logoLabel.setBounds(80, 100, 200, 200); // Centered in left panel
		logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		logoLabel.setVerticalAlignment(SwingConstants.CENTER);

		// Load company logo
		try {
			// Try loading from resources first
			InputStream logoStream = getClass().getResourceAsStream(Utils.LOGO_PATH);
			if (logoStream != null) {
				ImageIcon logoIcon = new ImageIcon(ImageIO.read(logoStream));
				// Scale the image to fit the label
				Image scaledImage = logoIcon.getImage().getScaledInstance(180, 140, Image.SCALE_SMOOTH);
				logoLabel.setIcon(new ImageIcon(scaledImage));
				logoStream.close();
			} else {
				// Fallback to file system
				ImageIcon logoIcon = new ImageIcon(Utils.LOGO_PATH);
				if (logoIcon.getIconWidth() > 0) {
					Image scaledImage = logoIcon.getImage().getScaledInstance(230, 180, Image.SCALE_SMOOTH);
					logoLabel.setIcon(new ImageIcon(scaledImage));
				} else {
					// Show company name if logo not found
					logoLabel.setText("<html><div style='text-align: center;'><font color='white' size='6'><b>COMPANY<br>LOGO</b></font></div></html>");
				}
			}
		} catch (Exception ex) {
			// Show company name if logo not found
			logoLabel.setText("<html><div style='text-align: center;'><font color='white' size='6'><b>COMPANY<br>LOGO</b></font></div></html>");
		}

		leftPanel.add(logoLabel);

		// Create right panel (60% width)
		rightPanel = new JPanel();
		rightPanel.setLayout(null);
		rightPanel.setBackground(Color.pink);
		rightPanel.setPreferredSize(new Dimension(540, 500)); // 60% of 900px width

		// Add all existing components to right panel with adjusted positions
		headerLabel = new JLabel();
		headerLabel.setText(""); // Start with empty text for animation
		headerLabel.setBounds(50, 20, 450, 80); // Adjusted for right panel
		headerLabel.setFont(new Font("Geomanist", Font.BOLD, 25));
		headerLabel.setForeground(Color.white);
		headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		rightPanel.add(headerLabel);

		// Start typing animation for header
		startTypingAnimation();

		idLabel = new JLabel();
		idLabel.setText("Username");
		idLabel.setBounds(50, 130, 150, 50);
		idLabel.setFont(new Font(null, Font.BOLD, 20));
		idLabel.setForeground(Color.white);
		rightPanel.add(idLabel);

		passLabel = new JLabel("Password");
		passLabel.setBounds(50, 185, 150, 50);
		passLabel.setFont(new Font(null, Font.BOLD, 20));
		passLabel.setForeground(Color.white);
		rightPanel.add(passLabel);

		id = new JTextField("samim");
		id.setBounds(200, 145, 250, 35);
		id.setFont(new Font(null, Font.PLAIN, 16));
		id.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.WHITE, 2),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)
		));
		rightPanel.add(id);

		pass = new JPasswordField("123456");
		pass.setBounds(200, 195, 250, 35);
		pass.setFont(new Font(null, Font.PLAIN, 16));
		pass.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.WHITE, 2),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)
		));
		rightPanel.add(pass);

		submit = new JButton("Login");
		submit.setBounds(200, 260, 120, 45);
		submit.setFont(new Font(null, Font.BOLD, 18));
		submit.setBackground(new Color(70, 130, 180));
		submit.setForeground(Color.WHITE);
		submit.setBorder(BorderFactory.createRaisedBevelBorder());
		submit.setFocusPainted(false);
		submit.setCursor(new Cursor(Cursor.HAND_CURSOR));
		rightPanel.add(submit);

		devInfo = new JLabel();
		devInfo.setText("© All Rights Reserved, Developed by Arif Hosain");
		devInfo.setBounds(50, 420, 450, 30);
		devInfo.setFont(new Font("Geomanist", Font.PLAIN, 12));
		devInfo.setForeground(Color.white);
		devInfo.setHorizontalAlignment(SwingConstants.CENTER);
		rightPanel.add(devInfo);

		// Add panels to main frame
		mainFrame.add(leftPanel, BorderLayout.WEST);
		mainFrame.add(rightPanel, BorderLayout.CENTER);

		submit.addActionListener(this::submitActionPerformed);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}

	// Typing animation method
	private void startTypingAnimation() {
		String fullText = Utils.TITLE;
		Timer timer = new Timer(100, null); // 100ms delay between characters

		timer.addActionListener(e -> {
			String currentText = headerLabel.getText();
			if (currentText.length() < fullText.length()) {
				// Add next character
				String nextChar = String.valueOf(fullText.charAt(currentText.length()));
				headerLabel.setText(currentText + nextChar);
			} else {
				// Animation complete, stop timer
				((Timer) e.getSource()).stop();

				// Optional: Add a blinking cursor effect after typing is complete
				startBlinkingCursor();
			}
		});

		timer.start();
	}

	// Optional blinking cursor animation
	private void startBlinkingCursor() {
		String originalText = headerLabel.getText();
		Timer blinkTimer = new Timer(500, null); // 500ms blink interval

		blinkTimer.addActionListener(e -> {
			String currentText = headerLabel.getText();
			if (currentText.endsWith("|")) {
				headerLabel.setText(originalText); // Remove cursor
			} else {
				headerLabel.setText(originalText + "|"); // Add cursor
			}
		});

		// Stop blinking after 3 seconds
		Timer stopTimer = new Timer(3000, e -> {
			blinkTimer.stop();
			headerLabel.setText(originalText); // Ensure cursor is removed
		});

		blinkTimer.start();
		stopTimer.start();
	}

	public void submitActionPerformed(java.awt.event.ActionEvent evt){
		AppService service = new AppService();
		if(service.login(id.getText(),new String(pass.getPassword()))){
			mainFrame.setVisible(false);
			Frame2new fn=new Frame2new();
			fn.showButtonDemo();
		} else{
			JOptionPane.showMessageDialog(null, "Invalid credentials!");
		}
	}

	public static void main(String[] a) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Frame2new.setDefaultValues();
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another look and feel.
		}
		Frame1 f = new Frame1();
	}
}