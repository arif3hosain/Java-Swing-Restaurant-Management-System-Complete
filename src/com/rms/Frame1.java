package com.rms;

import com.rms.setting.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

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

	public Frame1(){
		prepareGUI();
	}

	private void prepareGUI(){
		mainFrame = new JFrame("Authorize identity");
		mainFrame.setSize(700,400);
		mainFrame.setResizable(false);
		mainFrame.setLayout(null);
		mainFrame.getContentPane().setBackground(Color.pink);
		try{
			mainFrame.setIconImage(ImageIO.read(new File(Utils.logoPath)));
		}
		catch (Exception ex){
			JOptionPane.showMessageDialog(null, "Logo not found!");
		}
		//====
		headerLabel = new JLabel();
		headerLabel.setText("Pizza Captaion Secured Login");
		headerLabel.setBounds(150,1,500,100);
		headerLabel.setFont(new Font("Geomanist", Font.BOLD, 25));
		headerLabel.setForeground(Color.white);
		mainFrame.add(headerLabel);


		idLabel = new JLabel();
		idLabel.setText("Username");
		idLabel.setBounds(150,110,150,50);
		idLabel.setFont(new Font(null, Font.BOLD, 20));
		idLabel.setForeground(Color.white);
		mainFrame.add(idLabel);

		passLabel=new JLabel("Password");
		passLabel.setBounds(150,165,150,50);
		passLabel.setFont(new Font(null, Font.BOLD, 20));
		passLabel.setForeground(Color.white);
		mainFrame.add(passLabel);

		devInfo = new JLabel();
		devInfo.setText("© all Rights Reserved, developed by www.officesolution.com");
		devInfo.setBounds(160,300,1000,30);
		devInfo.setFont(new Font("Geomanist", Font.PLAIN, 10));
		devInfo.setForeground(Color.white);
		mainFrame.add(devInfo);

		id=new JTextField();
		id.setBounds(300,125,200,30);
		//id.setText("palki");
		mainFrame.add(id);

		pass=new JPasswordField();
		//pass.setText("palki011998");
		mainFrame.add(pass);
		pass.setBounds(300,175,200,30);
		submit=new JButton("Login");
		submit.setBounds(400,230,100,25);
		mainFrame.add(submit);

		submit.addActionListener(this::submitActionPerformed);
		mainFrame.setVisible(true);
	}
	public void submitActionPerformed(java.awt.event.ActionEvent evt){
		
		if(id.getText().equals("rms") && pass.getText().equals("123456")){
			mainFrame.setVisible(false);
			Frame2new fn=new Frame2new();
			fn.showButtonDemo();
		} else{
			JOptionPane.showMessageDialog(null, "Invalid password!");
		}
	}
	public static void main(String[] a) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
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
	//palki
	//palki011998