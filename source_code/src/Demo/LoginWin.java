package Demo;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import Utils.JsonUtil;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.HashMap;

// Login Window
public class LoginWin extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Window Size
	static final int LOGIN_WIDTH = 350;
	static final int LOGIN_HEIGHT = 150;

	// Error Dialog Size
	static final int ERROR_DIALOG_WIDTH = 300;
	static final int ERROR_DIALOG_HEIGHT = 100;

	// Text Field Length
	static final int TEXT_FIELD_LENGTH = 20;

	// Config File Path
	static String CONFIG_FILE = "Config.json";

	// Screen Dimension
	Dimension mScreenSize;

	// Login Frame
	JFrame mLoginFrame;

	// IP \ Port
	JTextField mIPTextField;
	JTextField mPortTextField;
	// AppID \ Password
	JTextField mAppIDTextField;
	JTextField mPasswordTextField;

	// Item Counter
	Integer mItemCounter = 0;

	// Add Component to Login Frame
	public void addComp(Component compA, Component compB, GridBagConstraints constraints) {
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		// Component A
		if (compA != null) {
			constraints.gridx = 0;
			constraints.gridy = mItemCounter;
			add(compA, constraints);
		}
		// Component B
		if (compB != null) {
			constraints.gridx = 1;
			constraints.gridy = mItemCounter;
			add(compB, constraints);
		}
		++mItemCounter;
	}

	// Set IP \ Port \ AppID \ Password
	public void platformConfig(String strIp, String strPort, String strAppId, String strPassword) {
		mIPTextField.setText(strIp);
		mPortTextField.setText(strPort);
		mAppIDTextField.setText(strAppId);
		mPasswordTextField.setText(strPassword);
	}

	// Constructor
	LoginWin() {
		// Login Frame
		mLoginFrame = new JFrame("Demo Login");
		mLoginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mLoginFrame.setResizable(false);

		// Set Size and Location
		mLoginFrame.setSize(LOGIN_WIDTH, LOGIN_HEIGHT);
		mScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
		mLoginFrame.setLocation((mScreenSize.width - WIDTH) / 2, (mScreenSize.height - HEIGHT) / 2);

		// Layout
		setLayout(new GridBagLayout());
		mLoginFrame.add(this, BorderLayout.WEST);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;

		// Main Win
		MainWin mainWin = new MainWin();

		// Text Field
		mIPTextField = new JTextField(TEXT_FIELD_LENGTH);
		mPortTextField = new JTextField(TEXT_FIELD_LENGTH);
		mAppIDTextField = new JTextField(TEXT_FIELD_LENGTH);
		mPasswordTextField = new JTextField(TEXT_FIELD_LENGTH);

		// Read Config
		JButton bReadConfig = new JButton("Read Config");
		// Login Button
		JButton bLogin = new JButton("Login");

		// Add Components to Login Frame
		addComp(new JLabel("Platform IP: "), mIPTextField, constraints);
		addComp(new JLabel("Port: "), mPortTextField, constraints);
		addComp(new JLabel("App ID: "), mAppIDTextField, constraints);
		addComp(new JLabel("Password: "), mPasswordTextField, constraints);
		addComp(bReadConfig, bLogin, constraints);
		
		// Set Login Frame Visible
		mLoginFrame.setVisible(true);

		// Button Action
		bReadConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					// Read Platform Info From Config File
					String strConfig = readConfigFile(CONFIG_FILE);
					if (strConfig != null && strConfig.length() != 0) {
						Map<String, String> mConfig = new HashMap<String, String>();
						mConfig = JsonUtil.jsonString2SimpleObj(strConfig, mConfig.getClass());
						String strIP = mConfig.get("IP");
						String strPort = mConfig.get("Port");
						String strAppId = mConfig.get("AppId");
						String strPassword = mConfig.get("Password");
						if (strIP == null || strIP.length() == 0 || strPort == null || strPort.length() == 0
								|| strAppId == null || strAppId.length() == 0 || strPassword == null || strPassword.length() == 0) {
							throw new Exception();
						} else {
							// Set Platform Info
							platformConfig(strIP, strPort, strAppId, strPassword);
						}
					} else {
						throw new Exception();
					}
				} catch (Exception e) {
					// Error Happened While Reading Config File
					JDialog error = new JDialog();
					error.setLayout(new FlowLayout());
					error.setTitle("Read Config File Error");
					error.add(new JLabel("Error Happened While Reading Config File."));

					error.setSize(ERROR_DIALOG_WIDTH, ERROR_DIALOG_HEIGHT);
					error.setLocation(mScreenSize.width / 2, mScreenSize.height / 2);
					
					JButton bReturn = new JButton("Return");
					error.add(bReturn);
					bReturn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							error.setVisible(false);
						}
					});

					error.setVisible(true);
				}
			}
		});

		// Button Action
		bLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Get Input Data
				String strIP = mIPTextField.getText();
				String strPort = mPortTextField.getText();
				String strAppId = mAppIDTextField.getText();
				String strPassword = mPasswordTextField.getText();
				try {
					// Create Application
					AppTask myApp = mainWin.createApp(strIP, strPort, strAppId, strPassword);
					if (myApp == null) {
						throw new Exception("Create App Failed.");
					} else {
						// Authentication
						boolean bRet = myApp.authentication();
						if (bRet == false) {
							// Login Fail
							throw new Exception("Auth Failed, Check AppID and Password.");
						} else {
							bRet = mainWin.init();
							if (bRet == false) {
								// Init Fail
								throw new Exception("Init Failed, Unknown Reason.");
							} else {
								mainWin.setVisible(true);
								mLoginFrame.setVisible(false);
							}
						}
					}
				} catch (Exception e) {
					// Error Happened While Authentication
					JDialog error = new JDialog();
					error.setLayout(new FlowLayout());
					error.setTitle("Authentication Error");
					if (e.getMessage() == null || e.getMessage().length() == 0) {
						error.add(new JLabel("Connect Failed, Check Your Network."));
					} else {
						error.add(new JLabel(e.getMessage()));
					}

					error.setSize(ERROR_DIALOG_WIDTH, ERROR_DIALOG_HEIGHT);
					error.setLocation(mScreenSize.width / 2, mScreenSize.height / 2);

					JButton bReturn = new JButton("Return");
					error.add(bReturn);
					bReturn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							error.setVisible(false);
						}
					});

					error.setVisible(true);
				}
			}
		});
	}

	// Read Platform Info From Config File
	String readConfigFile(String strConfigFile) throws Exception {
		// Create BufferReader
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(CONFIG_FILE)));

		// Read Line by Line
		String strTotal = new String();
		String strCurLine = null;
		while((strCurLine = reader.readLine()) != null)
			strTotal += strCurLine;
		reader.close();

		// Return Whole Content
		return strTotal;
	}
}
