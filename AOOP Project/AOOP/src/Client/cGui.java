package Client;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;

public class cGui {

	private JFrame frmCloudStorage;
	private JLabel lblStatus;
	private JLabel lblCurStatus;
	private boolean server = false;
	private JTextField txtUsername;
	private JPasswordField txtPassword;
	
	/**
	 * Register Window
	 */
	private final JFrame frmRegister = new JFrame();
	private JTextField getUsername;
	private JPasswordField getPassword;
	private JPasswordField confirmPassword;

	
	/**
	 * Get screen resolution
	 */
	private final int width;
	private final int height;
	private int main_width;
	private int main_height;
	private int register_width;
	private int register_height;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					cGui window = new cGui();
					window.frmCloudStorage.setVisible(true);
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
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) screen.getWidth();
		height = (int) screen.getHeight();
		main_width = (width / 2) - 225;
		main_height = (height / 2) - 150;
		register_width = (width / 2) - 225;
		register_height = (height / 2) - 187;
		checkScreenSize();
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
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(230, 195, 200, 40);
		btnLogin.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
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
		frmCloudStorage.getContentPane().setLayout(null);
		simplifier(btnLogin);
		frmCloudStorage.getContentPane().add(btnLogin);
		
		JButton btnRegister = new JButton("Register");
		btnRegister.setBounds(20, 195, 200, 40);
		btnRegister.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		btnRegister.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (server == false)
					JOptionPane.showMessageDialog(frmCloudStorage, "Server is offline!", "Error", JOptionPane.ERROR_MESSAGE);
				else {
					openRegisterWindow();
				}
			}
		});
		simplifier(btnRegister);
		frmCloudStorage.getContentPane().add(btnRegister);
		
		lblStatus = new JLabel("Server Status:");
		lblStatus.setBounds(6, 256, 85, 16);
		frmCloudStorage.getContentPane().add(lblStatus);
		
		lblCurStatus = new JLabel();
		lblCurStatus.setBounds(95, 256, 61, 16);
		setStatus("Online");
		frmCloudStorage.getContentPane().add(lblCurStatus);
		
		txtUsername = new JTextField();
		txtUsername.setBounds(75, 25, 300, 50);
		txtUsername.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		txtUsername.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				txtUsername.setForeground(Color.BLACK);
				txtUsername.setText("");
			}
			@Override
			public void focusLost(FocusEvent e) {
				if (txtUsername.getText().equals("")) {
					setTxtNormal(txtUsername);
				}
			}
		});
		setTxtNormal(txtUsername);
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
		txtPassword.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		txtPassword.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				txtPassword.setForeground(Color.BLACK);
				txtPassword.setText("");
			}
			@SuppressWarnings("deprecation")
			@Override
			public void focusLost(FocusEvent e) {
				if (txtPassword.getText().equals("")) {
					setPwdNormal(txtPassword);
				}
			}
		});
		setPwdNormal(txtPassword);
		frmCloudStorage.getContentPane().add(txtPassword);
	}
	
	private void openRegisterWindow () {
		frmCloudStorage.setVisible(false);
		frmRegister.getContentPane().setBackground(Color.WHITE);
		frmRegister.getContentPane().setLayout(null);
		frmRegister.setResizable(false);
		frmRegister.setBounds(register_width, register_height, 450, 375);
		
		JButton btnConfirm = new JButton("Comfirm");
		btnConfirm.setBounds(230, 270, 200, 40);
        btnConfirm.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		simplifier(btnConfirm);
		frmRegister.getContentPane().add(btnConfirm);
		btnConfirm.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				confirmPressed();
			}
		});
		
		JButton btnCancel = new JButton("Cancle");
		btnCancel.setBounds(20, 270, 200, 40);
        btnCancel.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		simplifier(btnCancel);
		frmRegister.getContentPane().add(btnCancel);
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				frmRegister.dispose();
				frmCloudStorage.setVisible(true);
			}
		});
		
		getUsername = new JTextField();
		setTxtNormal(getUsername);
		getUsername.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		getUsername.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				getUsername.setForeground(Color.BLACK);
				getUsername.setText("");
			}
			@Override
			public void focusLost(FocusEvent e) {
				if (getUsername.getText().equals("")) {
					setTxtNormal(getUsername);
				}
			}
		});
		getUsername.setColumns(10);
		getUsername.setBounds(75, 25, 300, 50);
		frmRegister.getContentPane().add(getUsername);
		
		getPassword = new JPasswordField();
		setPwdNormal(getPassword);
		getPassword.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		getPassword.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				getPassword.setForeground(Color.BLACK);
				getPassword.setText("");
			}
			@SuppressWarnings("deprecation")
			@Override
			public void focusLost(FocusEvent e) {
				if (getPassword.getText().equals("")) {
					setPwdNormal(getPassword);
				}
			}
		});
		getPassword.setBounds(75, 100, 300, 50);
		frmRegister.getContentPane().add(getPassword);
		
		confirmPassword = new JPasswordField();
		setPwdNormal(confirmPassword);
		confirmPassword.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		confirmPassword.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				confirmPassword.setForeground(Color.BLACK);
				confirmPassword.setText("");
			}
			@SuppressWarnings("deprecation")
			@Override
			public void focusLost(FocusEvent e) {
				if (confirmPassword.getText().equals("")) {
					setPwdNormal(confirmPassword);
				}
			}
		});
		confirmPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent k) {
				if (k.getKeyCode() == 10) {
					confirmPressed();
				}
			}
		});
		confirmPassword.setBounds(75, 175, 300, 50);
		frmRegister.getContentPane().add(confirmPassword);
		
		frmRegister.setTitle("Register");
		frmRegister.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmRegister.setVisible(true);
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
	
	private void setTxtNormal (JTextField field) {
		field.setForeground(Color.LIGHT_GRAY);
		field.setText("Username");		
	}
	
	private void setPwdNormal (JPasswordField field) {
		field.setForeground(Color.LIGHT_GRAY);
		field.setText("Password");		
	}
	
	private void checkLogin () throws AWTException {
		if (true) {
			gotoTray();
		}
		else {
			JOptionPane.showMessageDialog(frmCloudStorage, "Username and Password does not match.", "Error", JOptionPane.ERROR_MESSAGE);
			setTxtNormal(txtUsername);
			setPwdNormal(txtPassword);
			txtUsername.requestFocus();
		}
	}
	
	private void loginPressed () throws AWTException {
		if (server == false)
			JOptionPane.showMessageDialog(frmCloudStorage, "Server is offline!", "Error", JOptionPane.ERROR_MESSAGE);
		else if (txtUsername.getText().equals("") && txtPassword.getText().equals(""))
			JOptionPane.showMessageDialog(frmCloudStorage, "Username and Password are empty!", "Error", JOptionPane.ERROR_MESSAGE);
		else if (txtUsername.getText().equals(""))
			JOptionPane.showMessageDialog(frmCloudStorage, "Username is empty!", "Error", JOptionPane.ERROR_MESSAGE);
		else if (txtPassword.getText().equals(""))
			JOptionPane.showMessageDialog(frmCloudStorage, "Password is empty!" , "Error", JOptionPane.ERROR_MESSAGE);
		else {
			checkLogin();
		}		
	}
	
	@SuppressWarnings("deprecation")
	private void confirmPressed () {
		if (getUsername.getText().equals("") && getPassword.getText().equals(""))
			JOptionPane.showMessageDialog(frmRegister, "Username and Password are empty!", "Error", JOptionPane.ERROR_MESSAGE);
		else if (getUsername.getText().equals(""))
			JOptionPane.showMessageDialog(frmRegister, "Username is empty!", "Error", JOptionPane.ERROR_MESSAGE);
		else if (getPassword.getText().equals(""))
			JOptionPane.showMessageDialog(frmCloudStorage, "Password is empty!" , "Error", JOptionPane.ERROR_MESSAGE);
		else if (!getPassword.getText().equals(confirmPassword.getText())) {
			JOptionPane.showMessageDialog(frmRegister, "Password does not match!", "Error", JOptionPane.ERROR_MESSAGE);
		}
		else {
			
		}
	}
	
	private void gotoTray () throws AWTException {
		frmCloudStorage.dispose();
        generateTray();
	}

    private void generateTray () throws AWTException {
        PopupMenu menu = new PopupMenu();

        MenuItem itemName = new MenuItem();
        itemName.setLabel("Mon");
        itemName.setEnabled(false);
        menu.add(itemName);

        MenuItem itemExit = new MenuItem("Exit");
        itemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });
        menu.add(itemExit);

        Image trayimage = Toolkit.getDefaultToolkit().getImage("/Users/Mon/Desktop/Icon.png");
        TrayIcon trayicon = new TrayIcon(trayimage, "Cloud Storage", menu);
        SystemTray.getSystemTray().add(trayicon);
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
