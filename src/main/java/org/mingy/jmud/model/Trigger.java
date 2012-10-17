package org.mingy.jmud.model;

/**
 * 触发器的抽象类。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public abstract class Trigger implements ITrigger {

	private boolean enabled = true;

	public int requiresLineCount() {
		return 1;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
