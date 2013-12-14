package Server;

import sun.plugin2.gluegen.runtime.CPU;

import java.awt.*;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class sGUI {

	private JFrame frmServer;

	/**
	 * First TAB
	 */
	private JLabel lblDate;
	private JLabel lblCurrentDate;
	private JLabel lblTime;
	private JLabel lblCurrentTime;
	private JLabel lblServer;
	private JLabel lblStatus;
	private JButton btnStart;
	private JButton btnStop;
	
	/**
	 * Second TAB
	 */
	private JLabel lblSystemStatus;
	private JLabel lblCPU;
	private JProgressBar CPUBar;
	private JLabel lblCPUStatus;
	private JLabel lblRAM;
	private JLabel lblRAMStatus;
	private JProgressBar RAMBar;

	/**
	 * Other
	 */
	private Font bFont;
	private Font sFont;
	private boolean server;

	/**
	 * Screen Resolution
	 */
	private final int width;
	private final int height;
	private int main_width;
	private int main_height;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					sGUI window = new sGUI();
					window.frmServer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public sGUI() {
		/**
		 * Get screen resolution from System
		 */
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) screen.getWidth();
		height = (int) screen.getHeight();
		main_width = (width / 2) - 512;
		main_height = (height / 2) - 384;
		checkScreenSize();
		
		/**
		 * Initialize variables
		 */
		bFont = new Font("Lucida Grande", Font.PLAIN, 20);
		sFont = new Font("Lucida Grande", Font.PLAIN, 16);

		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmServer = new JFrame();
		frmServer.setTitle("Server");
		frmServer.setResizable(false);
		frmServer.setBounds(100, 100, 1024, 768);
		frmServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmServer.getContentPane().setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 1024, 746);
		frmServer.getContentPane().add(tabbedPane);

		/**
		 * First TAB
		 */
		JPanel panel1 = new JPanel();
		tabbedPane.addTab("General Information", null, panel1, null);
		panel1.setLayout(null);
		
		lblDate = new JLabel("Date:");
		lblDate.setFont(bFont);
		lblDate.setBounds(25, 20, 55, 30);
		panel1.add(lblDate);
		
		lblCurrentDate = new JLabel("");
		lblCurrentDate.setFont(bFont);
		lblCurrentDate.setBounds(85, 20, 300, 30);
		panel1.add(lblCurrentDate);
		
		lblTime = new JLabel("Time:");
		lblTime.setFont(bFont);
		lblTime.setBounds(500, 20, 60, 30);
		panel1.add(lblTime);
		
		lblCurrentTime = new JLabel("");
		lblCurrentTime.setFont(bFont);
		lblCurrentTime.setBounds(565, 20, 300, 30);
		panel1.add(lblCurrentTime);
		
		lblServer = new JLabel("Server Status:");
		lblServer.setFont(bFont);
		lblServer.setBounds(25, 75, 135, 30);
		panel1.add(lblServer);
		
		lblStatus = new JLabel("Waiting");
		lblStatus.setFont(bFont);
		lblStatus.setBounds(165, 75, 300, 30);
		panel1.add(lblStatus);
		
		btnStart = new JButton("Start");
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				startPressed();
			}
		});
		btnStart.setFont(sFont);
		simplifier(btnStart);
		btnStart.setBounds(500, 75, 150, 30);
		panel1.add(btnStart);
		
		btnStop = new JButton("Stop");
		btnStop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				stopPressed();
			}
		});
		btnStop.setFont(sFont);
		simplifier(btnStop);
		btnStop.setBounds(700, 75, 150, 30);
		panel1.add(btnStop);
		
		table = new JTable();
		table.setBounds(6, 130, 991, 564);
		panel1.add(table);

		/**
		 * Second TAB
		 */
		JPanel panel2 = new JPanel();
		tabbedPane.addTab("System Status", null, panel2, null);
		panel2.setLayout(null);
		
		lblSystemStatus = new JLabel("System Status");
		lblSystemStatus.setFont(new Font("Lucida Grande", Font.PLAIN, 25));
		lblSystemStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblSystemStatus.setBounds(412, 20, 200, 30);
		panel2.add(lblSystemStatus);
		
		lblCPU = new JLabel("CPU Usage");
		lblCPU.setFont(bFont);
		lblCPU.setBounds(30, 100, 200, 30);
		panel2.add(lblCPU);

		lblCPUStatus = new JLabel("");
		lblCPUStatus.setFont(bFont);
		lblCPUStatus.setBounds(250, 100, 500, 30);
		panel2.add(lblCPUStatus);
		
		CPUBar = new JProgressBar();
		CPUBar.setBounds(30, 130, 945, 30);
		panel2.add(CPUBar);

		lblRAM = new JLabel("JVM Ram Usage");
		lblRAM.setBounds(30, 200, 200, 30);
		panel2.add(lblRAM);

		lblRAMStatus = new JLabel("");
		lblRAMStatus.setBounds(250, 200, 500, 30);
		panel2.add(lblRAMStatus);

		RAMBar = new JProgressBar();
		RAMBar.setBounds(30, 230, 945, 30);
		panel2.add(RAMBar);

		/**
		 * Third TAB
 		 */
		JPanel panel3 = new JPanel();
		tabbedPane.addTab("Log", null, panel3, null);
		panel3.setLayout(null);
		
		JTextPane txtLog = new JTextPane();
		txtLog.setText("asdgas\nasdas\nasdg");
		txtLog.setBounds(6, 62, 991, 632);
		panel3.add(txtLog);
		
		JLabel lblLog = new JLabel("Log");
		lblLog.setFont(new Font("Lucida Grande", Font.PLAIN, 25));
		lblLog.setHorizontalAlignment(SwingConstants.CENTER);
		lblLog.setBounds(412, 20, 200, 30);
		panel3.add(lblLog);

		/**
		 * Call Threads
		 */
		(new Thread(new Date_Time(lblCurrentDate, lblCurrentTime))).start();
		(new Thread(new SystemMonitor(CPUBar, lblCPUStatus))).start();
		
		/**
		 * Server Status
		 */
	}

	private void checkScreenSize () {
		if (main_width < 0)
			main_width = 0;
		if (main_height < 0)
			main_height = 0;
	}

	private void serverAction (String str) {
		if (str.equals("START")) {
			btnStart.setEnabled(false);
			btnStop.setEnabled(true);
			lblStatus.setForeground(Color.GREEN);
			lblStatus.setText("Running");
			server = true;
		}
		else {
			btnStart.setEnabled(true);
			btnStop.setEnabled(false);
			lblStatus.setForeground(Color.RED);
			lblStatus.setText("Stopped");
			server = false;
		}
	}

	private void startPressed () {
		serverAction("START");
	}

	private void stopPressed () {
		serverAction("STOP");
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
	
	class SystemMonitor implements Runnable {

		private final OperatingSystemMXBean system;
		private final double cores;
		private double load;
		private final File[] roots;
		private double totalSpace;
		private double usedSpace;
		private final double divide;

		private JProgressBar CPUBar;
		private JLabel CPUStatus;

		SystemMonitor (JProgressBar CPU, JLabel CPUStat) {
			CPUBar = CPU;
			CPUStatus = CPUStat;

			system = ManagementFactory.getOperatingSystemMXBean();
			cores = Runtime.getRuntime().availableProcessors();

			divide = 1000000000;
			totalSpace = 0;
			roots = File.listRoots();
			for (File root : roots) {
				totalSpace += root.getTotalSpace() / divide;
			}
		}

		public void run () {
			while (true) {
				/**
				 * CPU Load
				 */
				load = system.getSystemLoadAverage() / cores * 100;
				CPUBar.setValue((int) load);
				CPUStatus.setText(String.format("%.2f", load) + '%');
				if (load <= 25) {
					CPUStatus.setForeground(Color.GREEN);
				}
				else if (load <= 50) {
					CPUStatus.setForeground(Color.YELLOW);
				}
				else if (load <= 75) {
					CPUStatus.setForeground(Color.ORANGE);
				}
				else {
					CPUStatus.setForeground(Color.RED);
				}

				/**
				 * Usable Space
				 */
				usedSpace = 0;
				for (File root : roots) {
					usedSpace += root.getUsableSpace() / divide;
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class Date_Time implements Runnable {

		private int year;
		private int month_no;
		private int day_no;
		private int date;
		private String time;
		private final String[] day = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
		private final String[] month = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
		private String currentDate;
		private Calendar cal;
		private final SimpleDateFormat s = new SimpleDateFormat("HH:mm:ss");

		private JLabel lblDate;
		private JLabel lblTime;

		Date_Time (JLabel lbl1, JLabel lbl2) {
			lblDate = lbl1;
			lblTime = lbl2;
		}
		public void run () {
			while (true) {
				cal = Calendar.getInstance();
				year = cal.get(Calendar.YEAR);
				month_no = cal.get(Calendar.MONTH);
				date = cal.get(Calendar.DATE);
				day_no = cal.get(Calendar.DAY_OF_WEEK) - 1;
				currentDate = day[day_no] + ", " + month[month_no] + ' ' + date + ", " + year;

				time = s.format(cal.getTime());

				lblDate.setText(currentDate);
				lblTime.setText(time);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
