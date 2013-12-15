package Client;

import Utility.operationCode;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class cGui {
	private String ip = "127.0.0.1";
	private int port = 4444;

	/**
	 * Main Window
	 */
	private JFrame frmCloudStorage;
	private JLabel lblStatus;
	private JLabel lblCurStatus;
	private JTextField txtUsername;
	private JPasswordField txtPassword;
	private JLabel lblPassword;
	private JLabel lblUsername;
	
	/**
	 * Register Window
	 */
	private JFrame frmRegister;
	private JTextField getUsername;
	private JPasswordField getPassword;
	private JPasswordField confirmPassword;
	private JLabel lblUsername_2;
	private JLabel lblPassword_2;
	private JLabel lblConfirm;

	/**
	 * Others
	 */
	private final Font bFont;
	private final Font sFont;
	private boolean server = false;
	private String username;
	private File path;
	private double spaceused;

	/**
	 * Screen Resolution
	 */
	private final int width;
	private final int height;
	private int main_width;
	private int main_height;
	private int register_width;
	private int register_height;

	/**
	 * Tray Icon
	 */
	private MenuItem space;
	private MenuItem speed;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					cGui window = new cGui();
					//window.frmCloudStorage.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public cGui() {
		/**
		 * Get screen resolution from System
		 */
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) screen.getWidth();
		height = (int) screen.getHeight();
		main_width = (width / 2) - 225;
		main_height = (height / 2) - 150;
		register_width = (width / 2) - 225;
		register_height = (height / 2) - 187;
		checkScreenSize();

		/**
		 * Initialize variables
		 */
		bFont = new Font("Lucida Grande", Font.PLAIN, 20);
		sFont = new Font("Lucida Grande", Font.PLAIN, 16);
		username = "";
		spaceused = 0;

		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmCloudStorage = new JFrame();
		frmCloudStorage.setResizable(false);
		frmCloudStorage.setTitle("Welcom to Cloud Storage");
		frmCloudStorage.getContentPane().setBackground(Color.WHITE);
		frmCloudStorage.setBounds(main_width, main_height, 450, 300);
		frmCloudStorage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmCloudStorage.getContentPane().setLayout(null);
		frmCloudStorage.setVisible(true);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(230, 195, 200, 40);
		btnLogin.setFont(sFont);
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					loginPressed();
				} catch (AWTException e1) {
					e1.printStackTrace();
				}
			}
		});
		simplifier(btnLogin);
		frmCloudStorage.getContentPane().add(btnLogin);
		
		JButton btnRegister = new JButton("Register");
		btnRegister.setBounds(20, 195, 200, 40);
		btnRegister.setFont(sFont);
		btnRegister.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!server)
					JOptionPane.showMessageDialog(frmCloudStorage, "Server is offline!", "Error", JOptionPane.ERROR_MESSAGE);
				else {
					openRegisterWindow();
				}
			}
		});
		simplifier(btnRegister);
		frmCloudStorage.getContentPane().add(btnRegister);

		lblUsername = new JLabel("Username");
		lblUsername.setForeground(Color.LIGHT_GRAY);
		lblUsername.setFont(bFont);
		lblUsername.setBounds(81, 34, 140, 30);
		frmCloudStorage.getContentPane().add(lblUsername);

		lblPassword = new JLabel("Password");
		lblPassword.setForeground(Color.LIGHT_GRAY);
		lblPassword.setFont(bFont);
		lblPassword.setBounds(81, 109, 140, 30);
		frmCloudStorage.getContentPane().add(lblPassword);

		lblStatus = new JLabel("Server Status:");
		lblStatus.setBounds(6, 256, 85, 16);
		frmCloudStorage.getContentPane().add(lblStatus);
		
		lblCurStatus = new JLabel();
		lblCurStatus.setBounds(95, 256, 61, 16);
		setStatus("Offline");
		(new File(System.getProperty("user.home") + "/" + "Cloud Storage")).mkdirs();
		new send(ip, port, System.getProperty("user.home") + "/" + "Cloud Storage");
		send tmp = new send(operationCode.HELO);

		Thread tmpT = new Thread(tmp);

		try {
			tmpT.start();
			tmpT.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!tmp.getStat()) {
			setStatus("Offline");
			server = false;
		} else {
			setStatus("Online");
			server = true;
		}

		frmCloudStorage.getContentPane().add(lblCurStatus);
		
		txtUsername = new JTextField();
		txtUsername.setBounds(75, 25, 300, 50);
		txtUsername.setFont(bFont);
		txtUsername.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				frmCloudStorage.getContentPane().remove(lblUsername);
			}
			@Override
			public void focusLost(FocusEvent e) {
				if (txtUsername.getText().equals("")) {
					generateLabel(frmCloudStorage, txtUsername, lblUsername);
				}
			}
		});
		frmCloudStorage.getContentPane().add(txtUsername);
		txtUsername.setColumns(10);
		
		txtPassword = new JPasswordField();
		txtPassword.setBounds(75, 100, 300, 50);
		txtPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent k) {
				if (k.getKeyCode() == 10) {
					try {
						loginPressed();
					} catch (AWTException e) {
						e.printStackTrace();
					}
				}
			}
		});
		txtPassword.setFont(bFont);
		txtPassword.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				frmCloudStorage.getContentPane().remove(lblPassword);
			}
			@SuppressWarnings("deprecation")
			@Override
			public void focusLost(FocusEvent e) {
				if (txtPassword.getText().equals("")) {
					generateLabel(frmCloudStorage, txtPassword, lblPassword);
				}
			}
		});
		frmCloudStorage.getContentPane().add(txtPassword);
	}

	/**
	 * Register window
	 */
	private void openRegisterWindow () {
		frmCloudStorage.setVisible(false);
		frmRegister = new JFrame();
		frmRegister.getContentPane().setBackground(Color.WHITE);
		frmRegister.getContentPane().setLayout(null);
		frmRegister.setResizable(false);
		frmRegister.setBounds(register_width, register_height, 450, 375);
		frmRegister.setTitle("Register");
		frmRegister.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmRegister.setVisible(true);
		
		JButton btnConfirm = new JButton("Comfirm");
		btnConfirm.setBounds(230, 270, 200, 40);
		btnConfirm.setFont(sFont);
		simplifier(btnConfirm);
		frmRegister.getContentPane().add(btnConfirm);
		btnConfirm.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					confirmPressed();
				} catch (AWTException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JButton btnCancel = new JButton("Cancle");
		btnCancel.setBounds(20, 270, 200, 40);
		btnCancel.setFont(sFont);
		simplifier(btnCancel);
		frmRegister.getContentPane().add(btnCancel);
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				frmRegister.dispose();
				frmCloudStorage.setVisible(true);
			}
		});

		lblUsername_2 = new JLabel("Username");
		lblUsername_2.setForeground(Color.LIGHT_GRAY);
		lblUsername_2.setFont(bFont);
		lblUsername_2.setBounds(81, 34, 140, 30);
		frmRegister.getContentPane().add(lblUsername_2);

		lblPassword_2 = new JLabel("Password");
		lblPassword_2.setForeground(Color.LIGHT_GRAY);
		lblPassword_2.setFont(bFont);
		lblPassword_2.setBounds(81, 109, 140, 30);
		frmRegister.getContentPane().add(lblPassword_2);

		lblConfirm = new JLabel("Retype Password");
		lblConfirm.setForeground(Color.LIGHT_GRAY);
		lblConfirm.setFont(bFont);
		lblConfirm.setBounds(81, 184, 180, 30);
		frmRegister.getContentPane().add(lblConfirm);
		
		getUsername = new JTextField();
		getUsername.setFont(bFont);
		getUsername.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				frmRegister.getContentPane().remove(lblUsername_2);
			}
			@Override
			public void focusLost(FocusEvent e) {
				if (getUsername.getText().equals("")) {
					generateLabel(frmRegister, getUsername, lblUsername_2);
				}
			}
		});
		getUsername.setColumns(10);
		getUsername.setBounds(75, 25, 300, 50);
		frmRegister.getContentPane().add(getUsername);
		
		getPassword = new JPasswordField();
		getPassword.setFont(bFont);
		getPassword.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				frmRegister.getContentPane().remove(lblPassword_2);
			}
			@SuppressWarnings("deprecation")
			@Override
			public void focusLost(FocusEvent e) {
				if (getPassword.getText().equals("")) {
					generateLabel(frmRegister, getPassword, lblPassword_2);
				}
			}
		});
		getPassword.setBounds(75, 100, 300, 50);
		frmRegister.getContentPane().add(getPassword);
		
		confirmPassword = new JPasswordField();
		confirmPassword.setFont(bFont);
		confirmPassword.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				frmRegister.getContentPane().remove(lblConfirm);
			}
			@SuppressWarnings("deprecation")
			@Override
			public void focusLost(FocusEvent e) {
				if (confirmPassword.getText().equals("")) {
					generateLabel(frmRegister, confirmPassword, lblConfirm);
				}
			}
		});
		confirmPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent k) {
				if (k.getKeyCode() == 10) {
					try {
						confirmPressed();
					} catch (AWTException e) {
						e.printStackTrace();
					}
				}
			}
		});
		confirmPassword.setBounds(75, 175, 300, 50);
		frmRegister.getContentPane().add(confirmPassword);
	}
	
	private void checkScreenSize () {
		if (main_width < 0)
			main_width = 0;
		if (main_height < 0)
			main_height = 0;
		if (register_width < 0)
			register_width = 0;
		if (register_height < 0)
			register_height = 0;
	}
	
	private void setStatus (String st) {
		if (st.equals("Offline")) {
			lblCurStatus.setText("Offline");
			lblCurStatus.setForeground(Color.RED);
			server = false;
		}
		else {
			lblCurStatus.setText("Online");
			lblCurStatus.setForeground(Color.GREEN);
			server = true;
		}
	}

	private void setTxtNormal (JFrame frame, JTextField field, JLabel lbl) {
		field.setText("");
		generateLabel(frame, field, lbl);
	}

	private void setPwdNormal (JFrame frame, JPasswordField field, JLabel lbl) {
		field.setText("");
		generateLabel(frame, field, lbl);
	}
	
	private void checkLogin () throws AWTException {
		send tmp = new send(operationCode.LOGIN, txtUsername.getText() + "|" + txtPassword.getText());
		Thread t = new Thread(tmp);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (tmp.getStat()) {
			(new File(System.getProperty("user.home") + "/" + "Cloud Storage" + "/" + txtUsername.getText())).mkdirs();
			new send(ip, port, System.getProperty("user.home") + "/" + "Cloud Storage" + "/" + txtUsername.getText());
			username = txtUsername.getText();
			setFolder();
			gotoTray(false);
		}
		else {
			JOptionPane.showMessageDialog(frmCloudStorage, "Username and Password does not match.", "Error", JOptionPane.ERROR_MESSAGE);
			setTxtNormal(frmCloudStorage, txtUsername, lblUsername);
			setPwdNormal(frmCloudStorage, txtPassword, lblPassword);
			txtUsername.requestFocus();
		}
	}
	
	private void loginPressed () throws AWTException {
		if (!server)
			JOptionPane.showMessageDialog(frmCloudStorage, "Server is offline!", "Error", JOptionPane.ERROR_MESSAGE);
		else if (txtUsername.getText().equals("") && txtPassword.getText().equals("")) {
			JOptionPane.showMessageDialog(frmCloudStorage, "Username and Password are empty!", "Error", JOptionPane.ERROR_MESSAGE);
			txtUsername.requestFocus();
		}
		else if (txtUsername.getText().equals("")) {
			JOptionPane.showMessageDialog(frmCloudStorage, "Username is empty!", "Error", JOptionPane.ERROR_MESSAGE);
			txtUsername.requestFocus();
		}
		else if (txtPassword.getText().equals("")) {
			JOptionPane.showMessageDialog(frmCloudStorage, "Password is empty!" , "Error", JOptionPane.ERROR_MESSAGE);
			txtPassword.requestFocus();
		}
		else {
			checkLogin();
		}		
	}

	private void checkRegister() throws AWTException {
		send tmp = new send(operationCode.REGISTER, getUsername.getText() + "|" + getPassword.getText());
		txtUsername.setText(getUsername.getText());
		txtPassword.setText(getPassword.getText());
		Thread t = new Thread(tmp);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (tmp.getStat()) {
			(new File(System.getProperty("user.home") + "/" + "Cloud Storage" + "/" + txtUsername.getText())).mkdirs();
			new send(ip, port, System.getProperty("user.home") + "/" + "Cloud Storage" + "/" + txtUsername.getText());
			username = getUsername.getText();
			setFolder();
			gotoTray(true);
		} else {
			JOptionPane.showMessageDialog(frmCloudStorage, "Username is unavailable.", "Error", JOptionPane.ERROR_MESSAGE);
			setTxtNormal(frmCloudStorage, txtUsername, lblUsername);
			setPwdNormal(frmCloudStorage, txtPassword, lblPassword);
			txtUsername.requestFocus();
		}
	}

	@SuppressWarnings("deprecation")
	private void confirmPressed() throws AWTException {
		if (getUsername.getText().equals("") && getPassword.getText().equals("")) {
			JOptionPane.showMessageDialog(frmRegister, "Username and Password are empty!", "Error", JOptionPane.ERROR_MESSAGE);
			getUsername.requestFocus();
		}
		else if (getUsername.getText().equals("")) {
			JOptionPane.showMessageDialog(frmRegister, "Username is empty!", "Error", JOptionPane.ERROR_MESSAGE);
			getUsername.requestFocus();
		}
		else if (getPassword.getText().equals("")) {
			JOptionPane.showMessageDialog(frmCloudStorage, "Password is empty!" , "Error", JOptionPane.ERROR_MESSAGE);
			getPassword.requestFocus();
		}
		else if (!getPassword.getText().equals(confirmPassword.getText())) {
			JOptionPane.showMessageDialog(frmRegister, "Password does not match!", "Error", JOptionPane.ERROR_MESSAGE);
			pwdNotMatch();
		}
		else {
			checkRegister();
		}
	}

	private void pwdNotMatch () {
		getPassword.setText("");
		confirmPassword.setText("");
		generateLabel(frmRegister, getPassword, lblPassword_2);
		generateLabel(frmRegister, confirmPassword, lblConfirm);
		getPassword.requestFocus();
	}

	private void generateLabel (JFrame frame, JTextField txtField, JLabel lbl) {
		frame.getContentPane().remove(txtField);
		frame.getContentPane().add(lbl);
		frame.getContentPane().add(txtField);
	}

	private void generateLabel (JFrame frame, JPasswordField pwdField, JLabel lbl) {
		frame.getContentPane().remove(pwdField);
		frame.getContentPane().add(lbl);
		frame.getContentPane().add(pwdField);
	}
	
	private void gotoTray (boolean register) throws AWTException {
		frmCloudStorage.dispose();
		if (register) {
			frmRegister.dispose();
			(new Thread(new send(operationCode.LOGIN, txtUsername.getText() + "|" + txtPassword.getText()))).start();
		}
		generateTray();
		(new Thread(new send(operationCode.ALLHASH))).start();
		try {
			(new Thread(new fileMonitor(Paths.get(System.getProperty("user.home") + "/Cloud Storage/" + txtUsername.getText()), this.space))).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setFolder () {
		System.out.println();
		if (!username.equals("")) {
			path = new File(System.getProperty("user.home") + "/Cloud Storage/" + username);
			if (!path.exists()) {
				path.mkdirs();
			}
		}
	}

	private void generateTray () throws AWTException {
		PopupMenu menu = new PopupMenu();

		/**
		 * Logout
		 */
		Runtime.getRuntime().addShutdownHook(new Thread(new send(operationCode.LOGOUT, "")));

		Menu user = new Menu();
		user.setLabel("Logged in as: " + username);
		user.setEnabled(true);
		space = new MenuItem();
		space.setLabel("Space Used: " + String.format("%.2f", spaceused));
		space.setEnabled(false);
		user.add(space);

		speed = new MenuItem();
		speed.setLabel("DL: 0 KB/s");
		speed.setEnabled(false);
		user.add(speed);
		send.setMenuItem(speed);

		MenuItem folderpath = new MenuItem();
		folderpath.setLabel("Open Cloud Storage folder");
		folderpath.setEnabled(true);
		folderpath.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(path);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		user.add(folderpath);
		menu.add(user);

		Menu about = new Menu();
		about.setLabel("About Cloud Storage");
		MenuItem version = new MenuItem();
		version.setLabel("Version: 2.00");
		version.setEnabled(false);
		about.add(version);
		MenuItem dev = new MenuItem();
		dev.setLabel("Developed By:");
		dev.setEnabled(false);
		about.add(dev);
		MenuItem dev1 = new MenuItem();
		dev1.setLabel("\tPoomkawin Laosirirat");
		dev1.setEnabled(false);
		about.add(dev1);
		MenuItem dev2 = new MenuItem();
		dev2.setLabel("\tThanat Sirithawornsant");
		dev2.setEnabled(false);
		about.add(dev2);
		menu.add(about);

		MenuItem itemExit = new MenuItem("Exit");
		itemExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menu.add(itemExit);

		Image trayimage = Toolkit.getDefaultToolkit().getImage("AOOP Project/AOOP/Icon.png");
		TrayIcon trayicon = new TrayIcon(trayimage, "Cloud Storage", menu);
		SystemTray.getSystemTray().add(trayicon);
		try {
			send tmp = new send(operationCode.SPACE, "");
			Thread t = new Thread(tmp);
			t.start();
			t.join();
			spaceused = Double.parseDouble(tmp.getUsedSpace());
			space.setLabel("Space Used: " + String.format("%.2f", (spaceused / (1000 * 1000))) + " MB");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void simplifier(JButton button) {
		button.setForeground(Color.WHITE);
		button.setBackground(UIManager.getColor("MenuBar.selectionBackground"));
//		  Border line = new LineBorder(null);
		Border margin = new EmptyBorder(5, 15, 5, 15);
		Border compound = new CompoundBorder(null, margin);
		button.setBorder(compound);
		button.setOpaque(true);
		button.setFocusPainted(false);
	}
}
