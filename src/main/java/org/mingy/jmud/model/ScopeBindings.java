package org.mingy.jmud.model;

import javax.script.Bindings;
import javax.script.SimpleBindings;

/**
 * 支持模块化的{@link Bindings}实现。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class ScopeBindings extends SimpleBindings {

	/** 上级模块的变量 */
	ScopeBindings parent;

	public ScopeBindings() {
		super();
	}

	public ScopeBindings(ScopeBindings parent) {
		this.parent = parent;
	}

	@Override
	public boolean containsKey(Object key) {
		if (super.containsKey(key))
			return true;
		else if (parent != null)
			return parent.containsKey(key);
		else
			return false;
	}

	@Override
	public Object get(Object key) {
		Object value = super.get(key);
		if (value == null && parent != null)
			value = parent.get(key);
		return value;
	}
}
