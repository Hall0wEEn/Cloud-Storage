package Client;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class cGui {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					cGui window = new cGui();
					window.frame.setVisible(true);
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
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JButton btnTest = new JButton("Test");
		simplifier(btnTest, 46, 204, 113);

		btnTest.setBounds(171, 201, 117, 29);

		frame.getContentPane().add(btnTest);
	}

	private static void simplifier(JButton button, int r, int g, int b) {
		button.setForeground(Color.WHITE);
		button.setBackground(new Color(r, g, b));
//		  Border line = new LineBorder(null);
		Border margin = new EmptyBorder(5, 15, 5, 15);
		Border compound = new CompoundBorder(null, margin);
		button.setBorder(compound);
		button.setOpaque(true);
		button.setFocusPainted(false);
	}
}
