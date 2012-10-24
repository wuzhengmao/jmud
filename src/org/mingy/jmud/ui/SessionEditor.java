package org.mingy.jmud.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.mingy.jmud.client.IMudClient;
import org.mingy.jmud.client.MudClient;
import org.mingy.jmud.client.Session;

public class SessionEditor extends EditorPart {

	public static final String ID = "org.mingy.jmud.ui.SessionEditor"; //$NON-NLS-1$

	private StyledText styledText;
	private Text text;
	private Session session;

	public SessionEditor() {
	}

	/**
	 * Create contents of the editor part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FormLayout());

		text = new Text(container, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.left = new FormAttachment(0);
		fd_text.right = new FormAttachment(100);
		fd_text.bottom = new FormAttachment(100);
		text.setLayoutData(fd_text);

		styledText = new StyledText(container, SWT.BORDER | SWT.V_SCROLL);
		FormData fd_styledText = new FormData();
		fd_styledText.left = new FormAttachment(0);
		fd_styledText.right = new FormAttachment(100);
		fd_styledText.top = new FormAttachment(0);
		fd_styledText.bottom = new FormAttachment(text, 0);
		styledText.setLayoutData(fd_styledText);

		if (session.getFont() != null)
			styledText.setFont(session.getFont());

		final IMudClient mc = new MudClient(session.getHost(),
				session.getPort(), session.getTimeout(), session.getCharset(),
				styledText, text,
				new org.mingy.jmud.model.pkuxkx.Configurations());
		mc.connect();

		IPartService service = (IPartService) getSite().getService(
				IPartService.class);
		service.addPartListener(new PartAdapter() {
			@Override
			public void partClosed(IWorkbenchPart part) {
				if (part == SessionEditor.this)
					mc.close();
			}
		});
	}

	@Override
	public void setFocus() {
		text.setFocus();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// Do the Save operation
	}

	@Override
	public void doSaveAs() {
		// Do the Save As operation
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		session = (Session) input.getAdapter(Session.class);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
}
