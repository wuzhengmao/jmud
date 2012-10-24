package org.mingy.jmud.ui;

import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.mingy.jmud.client.Session;

public class NewSessionWizard extends Wizard {

	private static final Log logger = LogFactory.getLog(NewSessionWizard.class);

	private IWorkbenchWindow window;
	private Session session;

	public NewSessionWizard(IWorkbenchWindow window) {
		super();
		this.window = window;
		setWindowTitle("New Session");
	}

	@Override
	public void addPages() {
		// TODO: for test
		session = new Session();
		session.setHost("pkuxkx.net");
		session.setPort(5555);
		session.setCharset(Charset.forName("GBK"));
		FontData fontData = new FontData("YaHei Consolas Hybrid", 10,
				SWT.NORMAL);
		fontData.setLocale("zh_CN");
		session.setFont(new Font(getShell().getDisplay(), fontData));
		session.setCharacter("kscs");
		session.setPassword("zxc123");
		NewSessionWizardPage page = new NewSessionWizardPage(session);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		try {
			window.getActivePage().openEditor(new SessionEditorInput(session),
					SessionEditor.ID);
		} catch (PartInitException e) {
			if (logger.isErrorEnabled()) {
				logger.error("error on open editor", e);
			}
			MessageDialog.openError(window.getShell(), "Error",
					"Error opening editor:" + e.getMessage());
		}
		return true;
	}
}
