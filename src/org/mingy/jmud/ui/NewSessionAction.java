package org.mingy.jmud.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class NewSessionAction extends Action {

	private IWorkbenchWindow window;

	public NewSessionAction(IWorkbenchWindow window, String text) {
		super(text);
		this.window = window;
		setId(ICommandIds.CMD_NEW_SESSION);
		setActionDefinitionId(IWorkbenchCommandConstants.FILE_NEW);
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
	}

	@Override
	public void run() {
		NewSessionWizard wizard = new NewSessionWizard(window);
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.setPageSize(400, 210);
		dialog.open();
	}
}
