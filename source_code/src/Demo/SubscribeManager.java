package Demo;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class SubscribeManager extends ManagerModule {
	static void updatePanel(JPanel pManager, AppTask app, LogPrinter log) {
		pManager.removeAll();
		pManager.setBorder(BorderFactory.createTitledBorder("Subscribe Manager"));

		// Layout
		pManager.setLayout(new GridBagLayout());
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.fill = GridBagConstraints.NONE;
		constraint.anchor = GridBagConstraints.WEST;
		constraint.gridx = 0;
		constraint.gridy = 0;
		constraint.gridwidth = 1;
		constraint.gridheight = 1;

		new SubscribeManager(pManager, constraint, app, log);
	}

	int mItem = 0;

	// Constructor
	SubscribeManager(JPanel pManager, GridBagConstraints constraint, AppTask app, LogPrinter log) {
		super(app, log);

		// Subscribe Panel
		createSubscribePanel(pManager, constraint);
	}

	void createSubscribePanel(JPanel pManager, GridBagConstraints constraint) {
		// Subscribe Panel
		JPanel pSubscribe = new JPanel();
		pSubscribe.setLayout(new FlowLayout());
		constraint.gridy = mItem;
		++mItem;
		pManager.add(pSubscribe, constraint);

		// Add "Subscribe Notification" Item
		addSubscribeItem(pSubscribe);
	}

	void addSubscribeItem(JPanel pCurPanel) {
		// Subscribe
		JPanel pSubscribe = new JPanel();
		pSubscribe.setLayout(new FlowLayout());
		pSubscribe.setBorder(BorderFactory.createTitledBorder("Subscribe Notification:"));
		pCurPanel.add(pSubscribe);

		// Notify Type
		pSubscribe.add(new JLabel("Notify Type:"));
		String[] aNotifyType = {"bindDevice", "deviceAdded", "deviceInfoChanged", "deviceDataChanged", "deviceDeleted",
									"deviceEvent", "messageConfirm", "commandRsp", "serviceInfoChanged", "ruleEvent"};
		JComboBox<String> boxSubscribe = new JComboBox<String>(aNotifyType);
		pSubscribe.add(boxSubscribe);
		// Callback URL
		pSubscribe.add(new JLabel("Callback URL:"));
		JTextField tfCallbackUrl = new JTextField(MainWin.TEXT_FIELD_LONG * 2);
		pSubscribe.add(tfCallbackUrl);
		// Subscribe Button
		JButton subscribeButton = new JButton("Subscribe");
		pSubscribe.add(subscribeButton);

		// Button Action
		subscribeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Subscribe");
				// Get Input Data
				String strCallbackUrl = tfCallbackUrl.getText();
				String strNotifyType = boxSubscribe.getSelectedItem().toString();
				mLogPrinter.printlnAsParam("Notify Type:" + strNotifyType + ", Callback Url: " + strCallbackUrl);
				// Application Operation
				try {
					boolean bRet = mAppTask.subscribe(strNotifyType, strCallbackUrl);
					if (bRet) {
						mLogPrinter.printlnAsResult("Subscribe Success.");
					} else {
						mLogPrinter.printlnAsError("Subscribe Failed.");
					}
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Subscribe.");
				}
				tfCallbackUrl.setText("");
			}
		});
	}
}
