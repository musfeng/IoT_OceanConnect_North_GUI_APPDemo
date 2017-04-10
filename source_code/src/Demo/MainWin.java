package Demo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import Utils.JsonUtil;

public class MainWin extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Windows Size
	static final int WIDTH = 1250;
	static final int HEIGHT = 600;
	// Log Text Area Size
	static final int LOG_WIDTH = 8;
	static final int LOG_HEIGHT = 100;
	// Text Field Length
	static final int TEXT_FIELD_SHORT = 5;
	static final int TEXT_FIELD_LONG = 10;
	// Module List
	static final String[] MODULE_LIST = {"Application Manager", "Device Manager",
											"Data Manager", "Command Manager",
											"Rule Manager", "Subscribe Manager"};
	// Solution List
	static final String[] SOLUTION_LIST = {"NB-IoT", "Agent"};

	// Main Frame
	JFrame mMainFrame;
	// Layout Constraint
	GridBagConstraints mConstraints;
	// Item Counter
	int mItemCounter = 0;

	// Log Printer
	LogPrinter mLogPrinter;
	JTextArea mLogTextArea;

	// Application
	AppTask mMyApp;

	// Add Component to Main Frame
	public void addComp(Component component) {
		mConstraints.gridx = 0;
		mConstraints.gridy = mItemCounter;
		mConstraints.gridwidth = 1;
		mConstraints.gridheight = 1;
		add(component, mConstraints);
		++mItemCounter;
	}

	// Create Application
	public AppTask createApp(String strIP, String strPort, String strAppId, String strPassword) throws Exception {
		// Create App Task
		mMyApp = new AppTask(strIP, strPort, strAppId, strPassword);
		return mMyApp;
	}

	// Set Main Frame Visible
	public void setVisible(boolean bVisible) {
		mMainFrame.setVisible(bVisible);
	}

	// Constructor
	public boolean init() {
		// Main Frame
		mMainFrame = new JFrame("GUI North Demo");
		mMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mMainFrame.setResizable(false);

		// Set Size and Location
		mMainFrame.setSize(WIDTH, HEIGHT);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		mMainFrame.setLocation((screenSize.width - WIDTH) / 2, (screenSize.height - HEIGHT) / 2);

		// Layout
		setLayout(new GridBagLayout());
		mMainFrame.add(this, BorderLayout.PAGE_START);
		mConstraints = new GridBagConstraints();
		mConstraints.fill = GridBagConstraints.NONE;
		mConstraints.anchor = GridBagConstraints.WEST;

		// Text Area
		JTextArea taLog = new JTextArea(LOG_WIDTH, LOG_HEIGHT);
		taLog.setLineWrap(true);
		taLog.setWrapStyleWord(true);
		// Log Printer
		mLogPrinter = new LogPrinter(taLog);
		mLogPrinter.printlnAsTitle("Log Content");
		mMyApp.setLogPrinter(mLogPrinter);
		// Log Panel
		JPanel pLog = new JPanel();
		pLog.setLayout(new FlowLayout());
		pLog.setBorder(BorderFactory.createTitledBorder("Debug:"));
		// Scroll Pane
		pLog.add(new JScrollPane(taLog));
		// Add to Main Win
		addComp(pLog);

		// Choose Module Panel
		JPanel pModuleChoose = new JPanel();
		pModuleChoose.setBorder(BorderFactory.createTitledBorder("Module Choose:"));
		addComp(pModuleChoose);
		pModuleChoose.add(new JLabel("Solution"));
		JComboBox<String> boxSolution = new JComboBox<String>(SOLUTION_LIST);
		pModuleChoose.add(boxSolution);
		pModuleChoose.add(new JLabel("Module:"));
		JComboBox<String> boxModule = new JComboBox<String>(MODULE_LIST);
		pModuleChoose.add(boxModule);

		// Manager Panel
		JPanel pManager = new JPanel();
		addComp(pManager);

		// Box Action
		boxModule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String strModule = boxModule.getSelectedItem().toString();
				mMainFrame.setVisible(false);
				if (strModule.equalsIgnoreCase("Application Manager")) {
					// Display Application Manager
					AppManager.updatePanel(pManager, mMyApp, mLogPrinter);
				} else if (strModule.equalsIgnoreCase("Device Manager")) {
					// Display Device Manager
					DeviceManager.updatePanel(pManager, mMyApp, mLogPrinter);
				} else if (strModule.equalsIgnoreCase("Data Manager")) {
					// Display Data Manger
					DataManager.updatePanel(pManager, mMyApp, mLogPrinter);
				} else if (strModule.equalsIgnoreCase("Command Manager")) {
					String strSolution = boxSolution.getSelectedItem().toString();
					if (strSolution.equalsIgnoreCase("NB-IoT")) {
						// Display Command Manager for NB-IoT
						CommandManager.updatePanel(pManager, mMyApp, mLogPrinter);
					} else if (strSolution.equalsIgnoreCase("Agent")) {
						CommandManagerForAgent.updatePanel(pManager, mMyApp, mLogPrinter);
						// Display Command Manager for Agnet
					} else {
						// Shouldn't be here
					}
				} else if (strModule.equalsIgnoreCase("Rule Manager")) {
					RuleManager.updatePanel(pManager, mMyApp, mLogPrinter);
					//updateAsRuleManager(pManager);
				} else if (strModule.equalsIgnoreCase("Subscribe Manager")) {
					SubscribeManager.updatePanel(pManager, mMyApp, mLogPrinter);
					//updateAsSubscribeManager(pManager);
				} else {
					// Shouldn't be here
				}
				mMainFrame.setVisible(true);
			}
		});

		boxSolution.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String strModule = boxModule.getSelectedItem().toString();
				if (strModule.equalsIgnoreCase("Command Manager")) {
					mMainFrame.setVisible(false);
					String strSolution = boxSolution.getSelectedItem().toString();
					if (strSolution.equalsIgnoreCase("NB-IoT")) {
						// Display Command Manager for NB-IoT
						CommandManager.updatePanel(pManager, mMyApp, mLogPrinter);
					} else if (strSolution.equalsIgnoreCase("Agent")) {
						CommandManagerForAgent.updatePanel(pManager, mMyApp, mLogPrinter);
						// Display Command Manager for Agnet
					} else {
						// Shouldn't be here
					}
					mMainFrame.setVisible(true);
				}
			}
		});

		return true;
	}
}
