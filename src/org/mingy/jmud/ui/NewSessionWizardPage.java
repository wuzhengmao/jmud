package org.mingy.jmud.ui;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mingy.jmud.client.Session;
import org.mingy.jmud.util.Strings;

public class NewSessionWizardPage extends WizardPage implements ModifyListener {

	private Text txtHost;
	private Text txtPort;
	private Text txtCharacter;
	private Text txtPassword;
	private Text txtCharset;
	private Text txtFont;
	private Text txtTimeout;
	private Session session;

	/**
	 * Create the wizard.
	 */
	public NewSessionWizardPage(Session session) {
		super(NewSessionWizardPage.class.getName());
		setTitle("New Session");
		setDescription("Create a new session");
		this.session = session;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		GridLayout gl_container = new GridLayout(3, false);
		gl_container.marginRight = 5;
		gl_container.marginLeft = 5;
		container.setLayout(gl_container);

		Label lblHost = new Label(container, SWT.NONE);
		lblHost.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblHost.setText("Host:");

		txtHost = new Text(container, SWT.BORDER);
		txtHost.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		new Label(container, SWT.NONE);

		Label lblPort = new Label(container, SWT.NONE);
		lblPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblPort.setText("Port:");

		txtPort = new Text(container, SWT.BORDER);
		txtPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		new Label(container, SWT.NONE);

		Label lblTimeout = new Label(container, SWT.NONE);
		lblTimeout.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblTimeout.setText("Timeout:");

		txtTimeout = new Text(container, SWT.BORDER);
		txtTimeout.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		new Label(container, SWT.NONE);

		Label lblCharset = new Label(container, SWT.NONE);
		lblCharset.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblCharset.setText("Charset:");

		txtCharset = new Text(container, SWT.BORDER);
		txtCharset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		new Label(container, SWT.NONE);

		Label lblFont = new Label(container, SWT.NONE);
		lblFont.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblFont.setText("Font:");

		txtFont = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtFont.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		Button btnFontButton = new Button(container, SWT.NONE);
		btnFontButton.setText("Browse...");
		btnFontButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FontDialog dialog = new FontDialog(getShell());
				dialog.setText("Select mud output font");
				if (session.getFont() != null)
					dialog.setFontList(session.getFont().getFontData());
				FontData fd = dialog.open();
				if (fd != null) {
					txtFont.setText(Arrays.toString(dialog.getFontList()));
					session.setFont(new Font(getShell().getDisplay(), dialog
							.getFontList()));
				}
			}
		});

		Label lblCharacter = new Label(container, SWT.NONE);
		lblCharacter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblCharacter.setText("Character:");

		txtCharacter = new Text(container, SWT.BORDER);
		txtCharacter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		new Label(container, SWT.NONE);

		Label lblPassword = new Label(container, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblPassword.setText("Password:");

		txtPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		new Label(container, SWT.NONE);

		setSession();
		txtHost.addModifyListener(this);
		txtPort.addModifyListener(this);
		txtTimeout.addModifyListener(this);
		txtCharset.addModifyListener(this);
		txtCharacter.addModifyListener(this);
		txtPassword.addModifyListener(this);
		getSession();
	}

	@Override
	public void modifyText(ModifyEvent event) {
		setPageComplete(getSession());
	}

	/**
	 * 取得会话连接信息。
	 */
	private boolean getSession() {
		if (Strings.isBlank(txtHost.getText())) {
			setErrorMessage("Host name or IP is empty.");
			return false;
		} else {
			session.setHost(txtHost.getText().trim());
		}
		if (Strings.isBlank(txtPort.getText())) {
			setErrorMessage("Port is empty.");
			return false;
		} else if (!Strings.isNumber(txtPort.getText().trim(), false, false)) {
			setErrorMessage("Port is not a valid number.");
			return false;
		} else {
			int port = Integer.parseInt(txtPort.getText().trim());
			if (port < 1 || port > 65535) {
				setErrorMessage("Port is not between 1 to 65535.");
				return false;
			}
			session.setPort(port);
		}
		if (Strings.isBlank(txtTimeout.getText())) {
			setErrorMessage("Connect timeout is empty.");
			return false;
		} else if (!Strings.isNumber(txtTimeout.getText().trim(), false, false)) {
			setErrorMessage("Connect timeout is not a valid number.");
			return false;
		} else {
			int timeout = Integer.parseInt(txtTimeout.getText().trim());
			if (timeout <= 0) {
				setErrorMessage("Connect timeout must larger than 0.");
				return false;
			}
			session.setTimeout(timeout);
		}
		if (Strings.isBlank(txtCharset.getText())) {
			session.setCharset(null);
		} else {
			Charset charset = Strings.toCharset(txtCharset.getText().trim());
			if (charset == null) {
				setErrorMessage("Charset is not supported.");
				return false;
			} else {
				session.setCharset(charset);
			}
		}
		session.setCharacter(!Strings.isBlank(txtCharacter.getText()) ? txtCharacter
				.getText().trim() : null);
		session.setPassword(!Strings.isBlank(txtPassword.getText()) ? txtPassword
				.getText().trim() : null);
		setErrorMessage(null);
		return true;
	}

	/**
	 * 设置会话连接信息。
	 */
	private void setSession() {
		txtHost.setText(session.getHost() != null ? session.getHost() : "");
		txtPort.setText(String.valueOf(session.getPort()));
		txtTimeout.setText(String.valueOf(session.getTimeout()));
		txtCharset.setText(session.getCharset() != null ? session.getCharset()
				.toString() : "");
		txtFont.setText(session.getFont() != null ? Arrays.toString(session
				.getFont().getFontData()) : "");
		txtCharacter.setText(session.getCharacter() != null ? session
				.getCharacter() : "");
		txtPassword.setText(session.getPassword() != null ? session
				.getPassword() : "");
	}
}
