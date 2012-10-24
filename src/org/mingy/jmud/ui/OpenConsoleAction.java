package org.mingy.jmud.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsoleConstants;

public class OpenConsoleAction extends Action {

	private static final Log logger = LogFactory
			.getLog(OpenConsoleAction.class);

	private IWorkbenchWindow window;

	public OpenConsoleAction(IWorkbenchWindow window, String text) {
		super(text);
		this.window = window;
		setId(ICommandIds.CMD_OPEN_CONSOLE);
		setActionDefinitionId(ICommandIds.CMD_OPEN_CONSOLE);
		setImageDescriptor(ConsolePlugin
				.getImageDescriptor(IConsoleConstants.IMG_VIEW_CONSOLE));
	}

	@Override
	public void run() {
		try {
			window.getActivePage().showView(IConsoleConstants.ID_CONSOLE_VIEW,
					null, IWorkbenchPage.VIEW_VISIBLE);
		} catch (PartInitException e) {
			if (logger.isErrorEnabled()) {
				logger.error("error on open console", e);
			}
			MessageDialog.openError(window.getShell(), "Error",
					"Error opening console:" + e.getMessage());
		}
	}
}
