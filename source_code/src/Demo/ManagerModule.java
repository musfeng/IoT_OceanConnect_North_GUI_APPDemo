package Demo;

import javax.swing.JPanel;

public abstract class ManagerModule {
	// App Task
	AppTask mAppTask;
	// Log Printer;
	LogPrinter mLogPrinter;
	// Manager Panel
	JPanel mManager;

	// Constructor
	ManagerModule(AppTask app, LogPrinter log) {
		mAppTask = app;
		mLogPrinter = log;
		mManager = null;
	}

	// Constructor
	ManagerModule(AppTask app, LogPrinter log, JPanel win) {
		mAppTask = app;
		mLogPrinter = log;
		mManager = win;
	}
}
