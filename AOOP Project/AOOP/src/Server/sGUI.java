package Server;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class sGUI {

	private JFrame frmServer;

	/**
	 * Server Info
	 */
	private String ip = "127.0.0.1";
	private int port = 4444;
	private ServerSocket socket;
	private boolean running = true;
	private Thread t;

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
	private JTable table;
	
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
	 * Third TAB
	 */
	private JTextArea txtLog;
	private JLabel lblLog;

	/**
	 * Table Data
	 */
	private DefaultTableModel model;
	private String[] colName;
	private String[][] data;
	private ArrayList<String> username;
	private ArrayList<String> IP;
	private ArrayList<String> status;
	private ArrayList<String> space;

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
	private JLabel lblHDD;
	private JLabel lblHDDStatus;
	private JProgressBar HDDBar;
	private JScrollBar scrollLog;

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

		/**
		 * Initialize Table Data
		 */
		colName = new String[]{"Client Username", "IP Address", "Status", "Space Used"};
		data = new String[][]{{"TEST", "1111", "IDLE", "0.00"}};
		username = new ArrayList<String>();
		IP = new ArrayList<String>();
		status = new ArrayList<String>();
		space = new ArrayList<String>();

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


		model = new DefaultTableModel();
		clientHandler.setModel(model);
		table = new JTable(model);
		model.addColumn("Client Username");
		model.addColumn("IP Address");
		model.addColumn("Status");
		model.addColumn("Space Used");
		table.setEnabled(false);
		table.setFont(sFont);
		table.setSurrendersFocusOnKeystroke(true);
		//table.setBounds(6, 130, 991, 564);                    // Do not need, use JScrollPane bounds instead
		//panel1.add(table);

		JScrollPane scrollTable = new JScrollPane(table);
		scrollTable.setBounds(6, 130, 991, 564);
		panel1.add(scrollTable);



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
		lblCPUStatus.setBounds(250, 100, 700, 30);
		panel2.add(lblCPUStatus);
		
		CPUBar = new JProgressBar();
		CPUBar.setBounds(30, 130, 945, 30);
		panel2.add(CPUBar);

		lblRAM = new JLabel("JVM Ram Usage");
		lblRAM.setFont(bFont);
		lblRAM.setBounds(30, 200, 200, 30);
		panel2.add(lblRAM);

		lblRAMStatus = new JLabel("");
		lblRAMStatus.setFont(bFont);
		lblRAMStatus.setBounds(250, 200, 700, 30);
		panel2.add(lblRAMStatus);

		RAMBar = new JProgressBar();
		RAMBar.setBounds(30, 230, 945, 30);
		panel2.add(RAMBar);

		lblHDD = new JLabel("Hard Disk Space");
		lblHDD.setFont(bFont);
		lblHDD.setBounds(30, 300, 200, 30);
		panel2.add(lblHDD);

		lblHDDStatus = new JLabel("");
		lblHDDStatus.setFont(bFont);
		lblHDDStatus.setBounds(250, 300, 700, 30);
		panel2.add(lblHDDStatus);

		HDDBar = new JProgressBar();
		HDDBar.setBounds(30, 330, 945, 30);
		panel2.add(HDDBar);

		/**
		 * Third TAB
 		 */
		JPanel panel3 = new JPanel();
		tabbedPane.addTab("Log", null, panel3, null);
		panel3.setLayout(null);
		
		txtLog = new JTextArea();
		txtLog.setEditable(false);
		clientHandler.setText(txtLog);
		//txtLog.setBounds(6, 62, 991, 632);            // Do not need, setBounds to the JScrollPane instead
		//panel3.add(txtLog);

		JScrollPane scrollLog = new JScrollPane(txtLog);
		//sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollLog.setBounds(6, 62, 991, 632);
		panel3.add(scrollLog);
		
		lblLog = new JLabel("Log");
		lblLog.setFont(new Font("Lucida Grande", Font.PLAIN, 25));
		lblLog.setHorizontalAlignment(SwingConstants.CENTER);
		lblLog.setBounds(412, 20, 200, 30);
		panel3.add(lblLog);

		/**
		 * Call Threads
		 */
		(new Thread(new Date_Time(lblCurrentDate, lblCurrentTime))).start();
		(new Thread(new SystemMonitor(CPUBar, lblCPUStatus, RAMBar, lblRAMStatus, HDDBar, lblHDDStatus))).start();
		
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
		t = new Thread(new Runnable() {
			public void run() {
				running = true;
				try {
					socket = new ServerSocket(port);
					System.out.println("Bound to port: " + port);
				} catch (IOException e) {
					System.out.println("Cannot bind to port: " + port);
					System.exit(0);
				}

				sessionManager sm = new sessionManager();
				System.out.println(System.getProperty("user.home"));
				while (running) {
					try {
						Socket s = socket.accept();
						System.out.println("New Client: " + s.getInetAddress().toString());
						(new Thread(new clientHandler(s, sm, "/Users/Touch/Desktop/cloud"))).start();
					} catch (Exception e) {
						System.out.println("Failed to accept client");
					}
				}
			}
		});
		t.start();
	}

	private void stopPressed() {
		serverAction("STOP");
		t.stop();
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
		private double CPUload;
		private final File[] roots;
		private double totalSpace;
		private double usedSpace;
		private double freeSpace;
		private double HDDload;
		private final double divide;
		private double totalMemory;
		private double usedMemory;
		private double RAMload;
		private double freeMemory;

		private JProgressBar CPUBar;
		private JLabel CPUStatus;
		private JProgressBar RAMBar;
		private JLabel RAMStatus;
		private JProgressBar HDDBar;
		private JLabel HDDStatus;

		SystemMonitor (JProgressBar CPU, JLabel CPUStat, JProgressBar RAM, JLabel RAMStat, JProgressBar HDD, JLabel HDDStat) {
			CPUBar = CPU;
			CPUStatus = CPUStat;
			RAMBar = RAM;
			RAMStatus = RAMStat;
			HDDBar = HDD;
			HDDStatus = HDDStat;

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
				CPUload = system.getSystemLoadAverage() / cores * 100;
				CPUBar.setValue((int) CPUload);
				CPUStatus.setText(String.format("%.2f", CPUload) + '%');
				if (CPUload <= 25) {
					CPUStatus.setForeground(Color.GREEN);
				}
				else if (CPUload <= 50) {
					CPUStatus.setForeground(Color.YELLOW);
				}
				else if (CPUload <= 75) {
					CPUStatus.setForeground(Color.ORANGE);
				}
				else {
					CPUStatus.setForeground(Color.RED);
				}

				/**
				 * JVM RAM Usage
				 */
				totalMemory = Runtime.getRuntime().totalMemory() / 1000000.00;
				freeMemory = Runtime.getRuntime().freeMemory() / 1000000.00;
				usedMemory = totalMemory - freeMemory;
				RAMload = usedMemory / totalMemory * 100.00;
				RAMBar.setValue((int) RAMload);
				RAMStatus.setText("Available " + String.format("%.2f", freeMemory) + "MB, Used " + String.format("%.2f", usedMemory) + "MB, Total " + String.format("%.2f", totalMemory) + "MB");
				if (RAMload <= 25) {
					RAMStatus.setForeground(Color.GREEN);
				}
				else if (RAMload <= 50) {
					RAMStatus.setForeground(Color.YELLOW);
				}
				else if (RAMload <= 75) {
					RAMStatus.setForeground(Color.ORANGE);
				}
				else {
					RAMStatus.setForeground(Color.RED);
				}


				/**
				 * Usable Space
				 */
				freeSpace = 0;
				for (File root : roots) {
					freeSpace += root.getUsableSpace() / divide;
				}
				usedSpace = totalSpace - freeSpace;
				HDDload = usedSpace / totalSpace * 100.00;
				HDDBar.setValue((int) HDDload);
				HDDStatus.setText("Available " + String.format("%.2f", freeSpace) + "GB, Used " + String.format("%.2f", usedSpace) + "GB, Total " + String.format("%.2f", totalSpace) + "GB");
				if (HDDload <= 25) {
					HDDStatus.setForeground(Color.GREEN);
				}
				else if (HDDload <= 50) {
					HDDStatus.setForeground(Color.YELLOW);
				}
				else if (HDDload <= 75) {
					HDDStatus.setForeground(Color.ORANGE);
				}
				else {
					HDDStatus.setForeground(Color.RED);
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
