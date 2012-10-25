package org.mingy.jmud.model;

/**
 * 子模块。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Module extends Scope {

	private Scope context;

	Module(String name, Scope parent) {
		super(name, parent);
		this.context = parent.getContext();
	}

	@Override
	public Scope getContext() {
		return context;
	}

	@Override
	public Scope getScope(String name) {
		return getContext().getScope(name);
	}
}
