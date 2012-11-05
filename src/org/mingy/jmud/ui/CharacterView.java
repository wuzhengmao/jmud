package org.mingy.jmud.ui;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * 角色视图的抽象类。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public abstract class CharacterView extends ViewPart {

	static final String ID_PATTERN = "org.mingy.jmud.ui.*.CharacterView"; //$NON-NLS-1$

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		createControls(parent);
		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create contents of the view part.
	 */
	protected abstract void createControls(Composite parent);

	/**
	 * Create the actions.
	 */
	protected void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	protected void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	protected void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}
}
