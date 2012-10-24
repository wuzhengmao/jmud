package org.mingy.jmud.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.mingy.jmud.client.Session;

public class SessionEditorInput implements IEditorInput {

	private Session session;

	public SessionEditorInput(Session session) {
		this.session = session;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter.isAssignableFrom(Session.class))
			return session;
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
}
