package org.mingy.jmud.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.mingy.jmud.Activator;
import org.mingy.jmud.client.ConnectionEvent;
import org.mingy.jmud.client.ConnectionStates;
import org.mingy.jmud.client.IConnectionStateListener;
import org.mingy.jmud.client.IMudClient;
import org.mingy.jmud.client.MudClient;
import org.mingy.jmud.model.Configuration;
import org.mingy.jmud.model.Session;

public class SessionEditor extends EditorPart {

	public static final String ID = "org.mingy.jmud.ui.SessionEditor"; //$NON-NLS-1$

	private static final Log logger = LogFactory.getLog(EditorPart.class);

	private StyledText styledText;
	private Text text;
	private SessionEditorInput input;

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

		Session session = input.getSession();
		if (session.getFont() != null)
			styledText.setFont(session.getFont());

		final Image onlineImage = Activator.getImageDescriptor(
				"/icons/online_16.gif").createImage();
		final Image offlineImage = Activator.getImageDescriptor(
				"/icons/offline_16.gif").createImage();

		final IMudClient mc = new MudClient(session, styledText, text);
		input.setClient(mc);
		mc.addConnectionStateListener(new IConnectionStateListener() {
			@Override
			public void onStateChanged(ConnectionEvent event) {
				if (isActive()) {
					final boolean offline = event.getNewState() == ConnectionStates.DISCONNECTED;
					input.getDisconnectAction().setEnabled(!offline);
					getSite().getShell().getDisplay().syncExec(new Runnable() {
						@Override
						public void run() {
							setTitleImage(offline ? offlineImage : onlineImage);
						}
					});
				}
			}
		});
		mc.connect();

		IPartService service = (IPartService) getSite().getService(
				IPartService.class);
		service.addPartListener(new PartAdapter() {
			@Override
			public void partOpened(IWorkbenchPart part) {
				if (part == SessionEditor.this) {
					Configuration configuration = input.getSession()
							.getConfiguration();
					if (configuration != null
							&& configuration.getCharacterViewId() != null) {
						try {
							input.setCharacterView((CharacterView) getSite()
									.getPage().showView(
											configuration.getCharacterViewId(),
											input.getId(),
											IWorkbenchPage.VIEW_VISIBLE));
						} catch (PartInitException e) {
							if (logger.isErrorEnabled()) {
								logger.error("error on open character view", e);
							}
						}
					}
				}
			}

			@Override
			public void partClosed(IWorkbenchPart part) {
				if (part == SessionEditor.this) {
					mc.close();
					if (input.getCharacterView() != null) {
						getSite().getPage().hideView(input.getCharacterView());
						input.setCharacterView(null);
					}
				} else if (part == input.getCharacterView()) {
					input.setCharacterView(null);
				}
			}

			@Override
			public void partActivated(IWorkbenchPart part) {
				if (part == SessionEditor.this) {
					if (input.getCharacterView() != null) {
						getSite().getPage()
								.bringToTop(input.getCharacterView());
					}
				}
			}
		});
	}

	private boolean isActive() {
		IWorkbenchPage page = getSite().getPage();
		return page != null && page.getActiveEditor() == this;
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
		if (!(input instanceof SessionEditorInput))
			throw new PartInitException("unsupported type: " + input.getClass());
		this.input = (SessionEditorInput) input;
		setInput(input);
		setPartName(input.getName());
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
