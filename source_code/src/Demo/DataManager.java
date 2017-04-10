package Demo;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import Demo.AppTask.CurrentData;
import Demo.AppTask.HistoryData;
import Demo.AppTask.ServiceInfo;

import java.util.Map;
import java.util.Vector;

public class DataManager extends ManagerModule {
	static void updatePanel(JPanel pManager, AppTask app, LogPrinter log) {
		pManager.removeAll();
		pManager.setBorder(BorderFactory.createTitledBorder("Data Manager"));

		// Layout
		pManager.setLayout(new GridBagLayout());
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.fill = GridBagConstraints.NONE;
		constraint.anchor = GridBagConstraints.WEST;
		constraint.gridx = 0;
		constraint.gridy = 0;
		constraint.gridwidth = 1;
		constraint.gridheight = 1;

		new DataManager(pManager, constraint, app, log);
	}

	int mItem = 0;

	// Constructor
	DataManager(JPanel pManager, GridBagConstraints constraint, AppTask app, LogPrinter log) {
		super(app, log, pManager);

		// Query Data Panel
		createQueryDataPanel(pManager, constraint);
	}

	void createQueryDataPanel(JPanel pManager, GridBagConstraints constraint) {
		// Query Data Panel
		JPanel pQueryData = new JPanel();
		pQueryData.setLayout(new FlowLayout());
		constraint.gridy = mItem;
		++mItem;
		pManager.add(pQueryData, constraint);

		// Add "Get Service List" Item
		addGetServiceListItem(pQueryData);
	}

	void addGetServiceListItem(JPanel pCurPanel) {
		// Query Data
		JPanel pQueryData = new JPanel();
		pQueryData.setLayout(new FlowLayout());
		pQueryData.setBorder(BorderFactory.createTitledBorder("Query Data:"));
		pCurPanel.add(pQueryData);

		// Device ID
		pQueryData.add(new JLabel("Device ID:"));
		JTextField tfDeviceId = new JTextField(MainWin.TEXT_FIELD_LONG);
		pQueryData.add(tfDeviceId);
		// Get Service Button
		JButton bGetService = new JButton("Get Service");
		pQueryData.add(bGetService);

		// Button Action
		bGetService.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Get Device Service");
				// Get Input Data
				String strDeviceId = tfDeviceId.getText();
				mLogPrinter.printlnAsParam("Device ID: " + strDeviceId);
				// Application Operation
				try {
					Vector<ServiceInfo> vServiceList = mAppTask.getServiceList(strDeviceId);
					// Update Query Device Data Panel
					updateQueryDataItem(pQueryData, strDeviceId, vServiceList);
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Get Device Service.");
				}
			}
		});
	}

	void updateQueryDataItem(JPanel pQueryData, String strDeviceId, Vector<ServiceInfo> vServiceList) {
		// Check Param
		if (vServiceList == null || vServiceList.size() == 0) {
			mLogPrinter.printlnAsError("Get Device Service Failed.");
			return;
		}

		// Update Query Device Data Panel While Service is RawData
		if (vServiceList.size() == 1) {
			String strServiceId = vServiceList.elementAt(0).mStrServiceId;
			String strServiceType = vServiceList.elementAt(0).mStrServiceType;
			if (strServiceId.equalsIgnoreCase("RawData") || strServiceType.equalsIgnoreCase("RawData")) {
				updateQueryDataItemWithRawData(pQueryData, strDeviceId);
				return;
			}
		}

		// Update Query Device Data Panel in Normal Case
		updateQueryDataItemNormal(pQueryData, strDeviceId, vServiceList);
	}

	void updateQueryDataItemWithRawData(JPanel pQueryData, String strDeviceId) {
		// Update Main Frame
		mManager.setVisible(false);
		pQueryData.removeAll();

		// Device ID
		pQueryData.add(new JLabel("Device ID:"));
		JTextField tfDeviceId = new JTextField(MainWin.TEXT_FIELD_LONG);
		pQueryData.add(tfDeviceId);
		tfDeviceId.setText(strDeviceId);
		// Service Label
		pQueryData.add(new JLabel("Service:"));
		// Service List Box
		JComboBox<String> boxService = new JComboBox<String>();
		boxService.addItem("RawData");
		pQueryData.add(boxService);

		// Query Current Data Button
		JButton bQueryCurData = new JButton("Query Data");
		pQueryData.add(bQueryCurData);
		// Query History Data Button
		JButton bQueryHistoryData = new JButton("Query History Data");
		pQueryData.add(bQueryHistoryData);
		// Add Refresh Service List Button
		JButton bRefreshService = new JButton("Refresh");
		pQueryData.add(bRefreshService);

		// Button Action
		bQueryCurData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Query Device Current Raw Data");
				// Get Input Data
				String strDeviceId = tfDeviceId.getText();
				mLogPrinter.printlnAsParam("Device ID: " + strDeviceId);
				// Application Operation
				try {
					CurrentData objCurData = mAppTask.queryRawData(strDeviceId);
					// Print Device Current Data
					mLogPrinter.printCurrentDataWithProperty(objCurData, "rawData");
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Query Device Current Data.");
				}
			}
		});

		// Query History Data Button Action
		bQueryHistoryData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Query Device History Raw Data");
				// Get Input Data
				String strDeviceId = tfDeviceId.getText();
				mLogPrinter.printlnAsParam("Device ID: " + strDeviceId);
				// Application Operation
				try {
					Map<String, Vector<HistoryData>> mHistoryData = mAppTask.queryHistoryRawData(strDeviceId);
					// Print Device Histroy Data
					mLogPrinter.printHistoryDataWithProperty(mHistoryData, "rawData");
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Query Device History Data.");
				}
			}
		});

		bRefreshService.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Refresh Device Service");
				// Get Input Data
				String strDeviceId = tfDeviceId.getText();
				mLogPrinter.printlnAsParam("Device ID: " + strDeviceId);
				// Application Operation
				try {
					Vector<ServiceInfo> vServiceList = mAppTask.getServiceList(strDeviceId);
					// Update Query Device Data Panel
					updateQueryDataItem(pQueryData, strDeviceId, vServiceList);
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Refresh Device Service.");
				}
			}
		});

		// Update Main Frame
		mManager.setVisible(true);
	}

	void updateQueryDataItemNormal(JPanel pQueryData, String strDeviceId, Vector<ServiceInfo> vServiceList) {
		// Update Main Frame
		mManager.setVisible(false);
		pQueryData.removeAll();

		// Device ID
		pQueryData.add(new JLabel("Device ID:"));
		JTextField tfDeviceId = new JTextField(MainWin.TEXT_FIELD_LONG);
		pQueryData.add(tfDeviceId);
		tfDeviceId.setText(strDeviceId);
		// Service Label
		pQueryData.add(new JLabel("Service:"));
		// Service List Box
		JComboBox<String> boxService = new JComboBox<String>();
		pQueryData.add(boxService);
		for (int i = 0; i < vServiceList.size(); ++i) {
			boxService.addItem(vServiceList.elementAt(i).mStrServiceId);
		}
		// Property List Box
		JComboBox<String> boxProperty = new JComboBox<String>();
		pQueryData.add(boxProperty);
		Vector<String> vProperty = mAppTask.getPropertyList(vServiceList, boxService.getSelectedItem().toString());
		if (vProperty != null) {
			boxProperty.addItem("ALL");
			for (int i = 0; i < vProperty.size(); ++i) {
				boxProperty.addItem(vProperty.elementAt(i));
			}
		}

		// Add Query Current Data Button
		JButton bQueryCurData = new JButton("Query Data");
		pQueryData.add(bQueryCurData);
		// Add Query History Data Button
		JButton bQueryHistoryData = new JButton("Query History Data");
		pQueryData.add(bQueryHistoryData);
		// Add Get Service List Button
		JButton bRefreshService = new JButton("Refresh");
		pQueryData.add(bRefreshService);

		// Service Box Action
		boxService.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Remove Previous Content
				boxProperty.removeAllItems();
				// Add New Property
				Vector<String> vProperty = mAppTask.getPropertyList(vServiceList, boxService.getSelectedItem().toString());
				if (vProperty != null) {
					boxProperty.addItem("ALL");
					for (int i = 0; i < vProperty.size(); ++i) {
						boxProperty.addItem(vProperty.elementAt(i));
					}
				}
			}
		});

		// Button Action
		bQueryCurData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Query Device Current Data");
				// Get Input Data
				String strDeviceId = tfDeviceId.getText();
				String strServiceId = boxService.getSelectedItem().toString();
				String strProperty = boxProperty.getSelectedItem().toString();
				mLogPrinter.printlnAsParam("Device ID: " + strDeviceId + ", Service ID: " + strServiceId + ", Property Type: " + strProperty);
				// Application Operation
				try {
					CurrentData objCurData = mAppTask.queryData(strDeviceId, strServiceId);
					// Print Current Data
					if (strProperty.equalsIgnoreCase("ALL")) {
						Vector<String> vProperty = mAppTask.getPropertyList(vServiceList, strServiceId);
						// Print Current Data with All Property
						mLogPrinter.printCurrentData(objCurData, vProperty);
					} else {
						// Print Current Data with Specific Property
						mLogPrinter.printCurrentDataWithProperty(objCurData, strProperty);
					}
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Query Device Current Data.");
				}
			}
		});

		bQueryHistoryData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Query Device History Data");
				// Get Input Data
				String strDeviceId = tfDeviceId.getText();
				String strServiceId = boxService.getSelectedItem().toString();
				String strProperty = boxProperty.getSelectedItem().toString();
				if (strServiceId == null) {
					mLogPrinter.printlnAsError("Can't find this service.");
					return;
				}
				Vector<String> vProperty = mAppTask.getPropertyList(vServiceList, boxService.getSelectedItem().toString());
				if (vProperty == null) {
					mLogPrinter.printlnAsError("Can't get property.");
					return;
				}
				mLogPrinter.printlnAsParam("Device ID: " + strDeviceId + ", Service ID: " + strServiceId + ", Property Type: " + strProperty);
				// Application Operation
				try {
					Map<String, Vector<HistoryData>> mHistoryData = mAppTask.queryHistoryData(strDeviceId, strServiceId, vProperty, strProperty);
					// Print History Data
					if (strProperty.equalsIgnoreCase("ALL")) {
						// Print History Data will All Property
						mLogPrinter.printHistoryData(mHistoryData, vProperty);
					} else {
						// Print History Data will Specific Property
						mLogPrinter.printHistoryDataWithProperty(mHistoryData, strProperty);
					}
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Query Device History Data.");
				}
			}
		});

		bRefreshService.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mLogPrinter.printlnAsTitle("Refresh Device Service");
				// Get Input Data
				String strDeviceId = tfDeviceId.getText();
				mLogPrinter.printlnAsParam("Device ID: " + strDeviceId);
				// Application Operation
				try {
					Vector<ServiceInfo> vServiceList = mAppTask.getServiceList(strDeviceId);
					// Update Query Device Data Panel
					updateQueryDataItem(pQueryData, strDeviceId, vServiceList);
				} catch (Exception e) {
					mLogPrinter.printExceptionTrace(e);
					mLogPrinter.printlnAsError("Exception Was Caught While Refresh Device Service.");
				}
			}
		});

		// Update Main Frame
		mManager.setVisible(true);
	}
}
