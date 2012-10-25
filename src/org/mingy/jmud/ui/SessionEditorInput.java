package org.mingy.jmud.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.mingy.jmud.client.IMudClient;
import org.mingy.jmud.model.Session;

public class SessionEditorInput implements IEditorInput {

	private Session session;
	private IMudClient client;
	private Action reconnectAction;
	private Action disconnectAction;

	public SessionEditorInput(Session session) {
		this.session = session;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		return session.getHost() + ":" + session.getPort();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return "";
	}

	public Session getSession() {
		return session;
	}

	public IMudClient getClient() {
		return client;
	}

	void setClient(IMudClient client) {
		this.client = client;
	}

	public Action getReconnectAction() {
		return reconnectAction;
	}

	void setReconnectAction(Action reconnectAction) {
		this.reconnectAction = reconnectAction;
	}

	public Action getDisconnectAction() {
		return disconnectAction;
	}

	void setDisconnectAction(Action disconnectAction) {
		this.disconnectAction = disconnectAction;
	}
}
