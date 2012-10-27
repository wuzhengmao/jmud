package org.mingy.jmud.model;

import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.SimpleBindings;

/**
 * 支持模块化的{@link Bindings}实现。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class ScopeBindings extends SimpleBindings {

	/** 临时变量 */
	private static final ThreadLocal<Map<String, Object>> localVariables;

	/** 上级模块的变量 */
	private ScopeBindings parent;

	static {
		localVariables = new ThreadLocal<Map<String, Object>>();
	}

	public ScopeBindings() {
		super();
	}

	public ScopeBindings(ScopeBindings parent) {
		this.parent = parent;
	}

	@Override
	public boolean containsKey(Object key) {
		if (hasLocalVariable(key))
			return true;
		return _containsKey(key);
	}

	boolean _containsKey(Object key) {
		if (super.containsKey(key))
			return true;
		else if (parent != null)
			return parent._containsKey(key);
		else
			return false;
	}

	@Override
	public Object get(Object key) {
		Object value = getLocalVariable(key);
		if (value == null)
			value = _get(key);
		return value;
	}

	Object _get(Object key) {
		Object value = getLocalVariable(key);
		if (value == null)
			value = super.get(key);
		if (value == null && parent != null)
			value = parent._get(key);
		return value;
	}

	@Override
	public Object put(String key, Object value) {
		if (value instanceof Double) {
			Double val = (Double) value;
			if (val.doubleValue() == val.longValue())
				value = val.longValue();
		}
		return super.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return super.remove(key);
	}

	static void resetLocalVariables() {
		Map<String, Object> map = localVariables.get();
		if (map != null)
			map.clear();
	}

	private static Map<String, Object> getLocalVariables() {
		Map<String, Object> map = localVariables.get();
		if (map == null) {
			map = new HashMap<String, Object>(8);
			localVariables.set(map);
		}
		return map;
	}

	private static boolean hasLocalVariable(Object key) {
		return getLocalVariables().containsKey(key);
	}

	private static Object getLocalVariable(Object key) {
		return getLocalVariables().get(key);
	}

	static Object setLocalVariable(String key, Object value) {
		if (value instanceof Double) {
			Double val = (Double) value;
			if (val.doubleValue() == val.longValue())
				value = val.longValue();
		}
		return getLocalVariables().put(key, value);
	}

	static Object removeLocalVariable(Object key) {
		return getLocalVariables().remove(key);
	}
}
