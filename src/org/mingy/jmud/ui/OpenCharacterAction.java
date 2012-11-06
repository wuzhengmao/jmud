package org.mingy.jmud.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.mingy.jmud.Activator;
import org.mingy.jmud.model.Configuration;

public class OpenCharacterAction extends Action {

	private static final Log logger = LogFactory
			.getLog(OpenCharacterAction.class);

	private IWorkbenchWindow window;

	public OpenCharacterAction(IWorkbenchWindow window, String text) {
		super(text);
		this.window = window;
		setId(ICommandIds.CMD_OPEN_CHARACTER);
		setActionDefinitionId(ICommandIds.CMD_OPEN_CHARACTER);
		setImageDescriptor(Activator
				.getImageDescriptor("/icons/character_16.gif"));
		setDisabledImageDescriptor(Activator
				.getImageDescriptor("/icons/character_disabled_16.gif"));
		setEnabled(false);
	}

	@Override
	public void run() {
		IEditorPart part = window.getActivePage().getActiveEditor();
		if (part != null) {
			SessionEditorInput input = (SessionEditorInput) part
					.getEditorInput();
			try {
				Configuration configuration = input.getSession()
						.getConfiguration();
				if (configuration != null
						&& configuration.getCharacterViewId() != null) {
					CharacterView view = input.getCharacterView();
					if (view != null) {
						window.getActivePage().bringToTop(view);
					} else {
						view = (CharacterView) window.getActivePage().showView(
								configuration.getCharacterViewId(), null,
								IWorkbenchPage.VIEW_VISIBLE);
						input.setCharacterView(view);
						view.init(input);
					}
				} else {
					MessageDialog.openWarning(window.getShell(), "Warning",
							"No window configurated.");
				}
			} catch (PartInitException e) {
				if (logger.isErrorEnabled()) {
					logger.error("error on open character view", e);
				}
				MessageDialog.openError(window.getShell(), "Error",
						"Error opening character window:" + e.getMessage());
			}
		}
	}
}
