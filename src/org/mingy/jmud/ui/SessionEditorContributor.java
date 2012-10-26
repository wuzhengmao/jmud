package org.mingy.jmud.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

public class SessionEditorContributor implements IEditorActionBarContributor {

	private IAction reconnectAction;
	private IAction disconnectAction;

	@Override
	public void init(IActionBars bars, IWorkbenchPage page) {
		// nothing to do
	}

	@Override
	public void setActiveEditor(IEditorPart editor) {
		if (editor != null) {
			SessionEditorInput input = (SessionEditorInput) editor
					.getEditorInput();
			if (reconnectAction == null) {
				reconnectAction = input.getReconnectAction();
				disconnectAction = input.getDisconnectAction();
			}
			reconnectAction.setEnabled(true);
			disconnectAction.setEnabled(!input.getClient().isDisconnected());
		} else {
			reconnectAction.setEnabled(false);
			disconnectAction.setEnabled(false);
		}
	}

	@Override
	public void dispose() {
		reconnectAction.setEnabled(false);
		disconnectAction.setEnabled(false);
	}
}
