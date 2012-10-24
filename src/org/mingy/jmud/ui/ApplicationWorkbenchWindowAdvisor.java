package org.mingy.jmud.ui;

import java.io.PrintStream;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(800, 600));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(false);
		configurer.setTitle("Java Mud Client"); //$NON-NLS-1$
	}

	public void postWindowOpen() {
		initLogger(getWindowConfigurer().getWindow());
	}

	private void initLogger(IWorkbenchWindow workbenchWindow) {
		final PrintStream sysout = System.out;
		final PrintStream syserr = System.err;
		MessageConsole console = new MessageConsole("Console", null);
		MessageConsoleStream stream = console.newMessageStream();
		final PrintStream out = new PrintStream(stream);
		ConsolePlugin.getDefault().getConsoleManager()
				.addConsoles(new IConsole[] { console });
		workbenchWindow.getPartService().addPartListener(new PartAdapter() {
			@Override
			public void partOpened(IWorkbenchPart part) {
				if (part instanceof IConsoleView) {
					System.setOut(out);
					System.setErr(out);
				}
			}

			@Override
			public void partClosed(IWorkbenchPart part) {
				if (part instanceof IConsoleView) {
					System.setOut(sysout);
					System.setErr(syserr);
				}
			}
		});
	}
}
