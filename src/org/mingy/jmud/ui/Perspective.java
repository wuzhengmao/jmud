package org.mingy.jmud.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

public class Perspective implements IPerspectiveFactory {

	public static final String ID = "org.mingy.jmud.ui.perspective"; //$NON-NLS-1$

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW,
				IPageLayout.BOTTOM, 0.7f, editorArea);
		IFolderLayout characterFolder = layout.createFolder("characters",
				IPageLayout.RIGHT, 0.7f, editorArea);
		characterFolder.addPlaceholder(CharacterView.ID_PATTERN + ":*");
	}
}
