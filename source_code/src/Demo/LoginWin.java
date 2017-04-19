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
	static final int WIDTH = 350;
	static final int HEIGHT = 150;

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

	// Error Dialog
	JDialog mErrorDialog;
	JLabel mErrorMsg;

	// Read Config Error Dialog
	JDialog mReadConfigErrorDialog;

	// Item Counter
	Integer mItemCounter = 0;

	// Add Component to Login Frame
	public void addComp(Component compA, Component compB, GridBagConstraints constraints) {
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		if (compA != null) {
			constraints.gridx = 0;
			constraints.gridy = mItemCounter;
			add(compA, constraints);
		}
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

	// Create Error Dialog
	public void createErrorDialog() {
		// Dialog for Login Fail
		mErrorDialog = new JDialog();
		mErrorDialog.setLayout(new FlowLayout());
		mErrorDialog.setTitle("Login Fail");

		// Set Size And Location
		mErrorDialog.setSize(ERROR_DIALOG_WIDTH, ERROR_DIALOG_HEIGHT);
		mErrorDialog.setLocation(mScreenSize.width / 2, mScreenSize.height / 2);

		// Default Error Message
		mErrorMsg = new JLabel("Login Failed, AppId or Password is Wrong.");
		mErrorDialog.add(mErrorMsg);

		// Try Again Button
		JButton bTryAgain = new JButton("Try Again");
		mErrorDialog.add(bTryAgain);

		// Button Action
		bTryAgain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Disable Error Dialog, Enable Login Frame
				mErrorDialog.setVisible(false);
				mLoginFrame.setVisible(true);
			}
		});
	}

	// Create Read Config Error Dialog
	public void createReadConfigErrorDialog() {
		// Dialog for Read Config Fail
		mReadConfigErrorDialog = new JDialog();
		mReadConfigErrorDialog.setLayout(new FlowLayout());
		mReadConfigErrorDialog.setTitle("Read Config Fail");

		// Set Size and Location
		mReadConfigErrorDialog.setSize(ERROR_DIALOG_WIDTH, ERROR_DIALOG_HEIGHT);
		mReadConfigErrorDialog.setLocation(mScreenSize.width / 2, mScreenSize.height / 2);

		// Default Error Message
		mErrorMsg = new JLabel("Read Config File Failed, Check Config.json.");
		mReadConfigErrorDialog.add(mErrorMsg);

		// Return
		JButton bReturn = new JButton("Return");
		mReadConfigErrorDialog.add(bReturn);

		// Button Action
		bReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mReadConfigErrorDialog.setVisible(false);
			}
		});
	}

	// Constructor
	LoginWin() {
		// Login Frame
		mLoginFrame = new JFrame("Demo Login");
		mLoginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mLoginFrame.setResizable(false);

		// Set Size and Location
		mLoginFrame.setSize(WIDTH, HEIGHT);
		Toolkit kit = Toolkit.getDefaultToolkit();
		mScreenSize = kit.getScreenSize();
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
		bReadConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
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
						}
						platformConfig(strIP, strPort, strAppId, strPassword);
					}
				} catch (Exception e) {
					mReadConfigErrorDialog.setVisible(true);
				}
			}
		});

		// Login Button
		JButton bLogin = new JButton("Login");
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
						mErrorMsg.setText("Create Application Failed.");
						// Enable Error Dialog, Disable Login Frame
						mErrorDialog.setVisible(true);
						mLoginFrame.setVisible(false);
					} else {
						// Authentication
						boolean bRet = myApp.authentication();
						if (bRet) {
							// Login Success
							mainWin.init();
							// Enable Main Frame, Disable Login Frame
							mainWin.setVisible(true);
							mLoginFrame.setVisible(false);
						} else {
							// Login Fail
							// Enable Error Dialog, Disable Login Frame
							mErrorDialog.setVisible(true);
							mLoginFrame.setVisible(false);
						}
					}
				} catch (Exception e) {
					// Connect Fail
					mErrorMsg.setText("Connect Failed, Please Check Your Network.");
					// Enable Error Dialog, Disable Login Frame
					mErrorDialog.setVisible(true);
					mLoginFrame.setVisible(false);
				}
			}
		});

		// Add Components to Login Frame
		addComp(new JLabel("Platform IP: "), mIPTextField, constraints);
		addComp(new JLabel("Port: "), mPortTextField, constraints);
		addComp(new JLabel("App ID: "), mAppIDTextField, constraints);
		addComp(new JLabel("Password: "), mPasswordTextField, constraints);
		addComp(bReadConfig, bLogin, constraints);

		// Create Error Dialog
		createErrorDialog();
		// Create Read Config Error Dialog
		createReadConfigErrorDialog();

		// Set Login Frame Visible
		mLoginFrame.setVisible(true);
	}

	String readConfigFile(String strConfigFile) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(CONFIG_FILE)));
		String strTotal = new String();
		String strCurLine = null;
		while((strCurLine = reader.readLine()) != null)
			strTotal += strCurLine;
		reader.close();
		return strTotal;
	}
}
