package org.mingy.jmud.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IAction aboutAction;
	private IAction quitAction;
	private Action openConsoleAction;
	private IAction closeAction;
	private NewSessionAction newSessionAction;
	private ReconnectAction reconnectAction;
	private DisconnectAction disconnectAction;
	private OpenCharacterAction openCharacterAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected void makeActions(final IWorkbenchWindow window) {
		{
			aboutAction = ActionFactory.ABOUT.create(window);
			register(aboutAction);
		}
		{
			quitAction = ActionFactory.QUIT.create(window);
			register(quitAction);
		}
		{
			openConsoleAction = new OpenConsoleAction(window, "Open Conso&le");
			register(openConsoleAction);
		}
		{
			closeAction = ActionFactory.CLOSE.create(window);
			register(closeAction);
		}
		{
			newSessionAction = new NewSessionAction(window, "&New Session");
			newSessionAction.setText("&New Session...");
			register(newSessionAction);
		}
		{
			reconnectAction = new ReconnectAction(window, "&Reconnect");
			register(reconnectAction);
		}
		{
			disconnectAction = new DisconnectAction(window, "&Disconnect");
			register(disconnectAction);
		}
		window.getPartService().addPartListener(new PartAdapter() {
			@Override
			public void partOpened(IWorkbenchPart part) {
				if (part instanceof SessionEditor) {
					SessionEditorInput input = (SessionEditorInput) ((SessionEditor) part)
							.getEditorInput();
					input.setReconnectAction(reconnectAction);
					input.setDisconnectAction(disconnectAction);
					openCharacterAction.setEnabled(true);
				}
			}

			@Override
			public void partClosed(IWorkbenchPart part) {
				if (part instanceof SessionEditor) {
					if (window.getActivePage() == null
							|| window.getActivePage().getActiveEditor() == null) {
						reconnectAction.setEnabled(false);
						disconnectAction.setEnabled(false);
						openCharacterAction.setEnabled(false);
					}
				}
			}
		});
		{
			openCharacterAction = new OpenCharacterAction(window,
					"Open &Character");
			register(openCharacterAction);
		}
	}

	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager("&File",
				IWorkbenchActionConstants.M_FILE);
		MenuManager windowMenu = new MenuManager("&Window",
				IWorkbenchActionConstants.M_WINDOW);
		MenuManager helpMenu = new MenuManager("&Help",
				IWorkbenchActionConstants.M_HELP);

		menuBar.add(fileMenu);
		fileMenu.add(newSessionAction);
		fileMenu.add(new Separator());
		fileMenu.add(reconnectAction);
		fileMenu.add(disconnectAction);
		fileMenu.add(closeAction);
		fileMenu.add(new Separator());
		fileMenu.add(quitAction);
		menuBar.add(windowMenu);
		windowMenu.add(openCharacterAction);
		windowMenu.add(new Separator());
		windowMenu.add(openConsoleAction);
		menuBar.add(helpMenu);
		helpMenu.add(aboutAction);
	}

	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		coolBar.add(new ToolBarContributionItem(toolbar, "main"));
		toolbar.add(newSessionAction);
		toolbar.add(new Separator());
		toolbar.add(reconnectAction);
		toolbar.add(disconnectAction);
		toolbar.add(new Separator());
		toolbar.add(openCharacterAction);
		toolbar.add(new Separator());
		toolbar.add(openConsoleAction);
	}
}
