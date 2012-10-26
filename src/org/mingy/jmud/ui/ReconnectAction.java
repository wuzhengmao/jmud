package org.mingy.jmud.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.mingy.jmud.Activator;
import org.mingy.jmud.client.IMudClient;
import org.mingy.jmud.model.Session;

/**
 * 重新连接。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class ReconnectAction extends Action {

	private IWorkbenchWindow window;

	public ReconnectAction(IWorkbenchWindow window, String text) {
		super(text);
		this.window = window;
		setId(ICommandIds.CMD_RECONNECT);
		setImageDescriptor(Activator.getImageDescriptor("/icons/online_16.gif"));
		setDisabledImageDescriptor(Activator
				.getImageDescriptor("/icons/online_disabled_16.gif"));
		setEnabled(false);
	}

	@Override
	public void run() {
		IEditorPart part = window.getActivePage().getActiveEditor();
		if (part != null) {
			SessionEditorInput input = (SessionEditorInput) part
					.getEditorInput();
			Session session = input.getSession();
			IMudClient client = input.getClient();
			if (client.isConnected()) {
				if (!MessageDialog.openQuestion(
						window.getShell(),
						"Confirm",
						"Reconnect to " + session.getHost() + ":"
								+ session.getPort() + " ?"))
					return;
			}
			if (!client.isDisconnected())
				client.disconnect();
			client.connect();
		}
	}
}
