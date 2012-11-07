package org.mingy.jmud.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.script.Bindings;
import javax.script.SimpleBindings;

import org.mingy.jmud.util.Variables;

/**
 * 支持模块化的{@link Bindings}实现。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class ScopeBindings extends SimpleBindings {

	/** 临时变量 */
	private static final ThreadLocal<Map<String, Object>> localVariables;

	/** 上下文 */
	private final IScope scope;

	/** 上级模块的变量 */
	private ScopeBindings parent;

	/** 监听器 */
	private ConcurrentMap<String, ConcurrentMap<String, Watcher>> watchers;

	static {
		localVariables = new ThreadLocal<Map<String, Object>>();
	}

	public ScopeBindings(IScope scope) {
		super();
		this.scope = scope;
		watchers = new ConcurrentHashMap<String, ConcurrentMap<String, Watcher>>();
	}

	public ScopeBindings(IScope scope, ScopeBindings parent) {
		this(scope);
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
		Object old = super.put(key, value);
		if (!(old == null ? value == null : old.equals(value))) {
			fireWatcher(key, old, value);
		}
		return old;
	}

	@Override
	public Object remove(Object key) {
		Object old = super.remove(key);
		fireWatcher(key, old, null);
		return old;
	}

	private void fireWatcher(Object key, Object oldValue, Object newValue) {
		Map<String, Watcher> map = watchers.get(key);
		if (map != null && !map.isEmpty()) {
			for (Watcher watcher : map.values()) {
				scope.execute(
						watcher.getExecution(),
						new String[] { watcher.getId(),
								Variables.toString(oldValue),
								Variables.toString(newValue) });
			}
		}
	}

	/**
	 * 添加一个变量的监听器。
	 * 
	 * @param key
	 *            变量名
	 * @param watcher
	 *            监听器
	 */
	public void addWatcher(String key, Watcher watcher) {
		ConcurrentMap<String, Watcher> map = watchers.get(key);
		if (map == null) {
			map = new ConcurrentHashMap<String, Watcher>(4);
			ConcurrentMap<String, Watcher> m = watchers.putIfAbsent(key, map);
			if (m != null)
				map = m;
		}
		map.put(watcher.getId(), watcher);
	}

	/**
	 * 添加一个变量的监听器。
	 * 
	 * @param key
	 *            变量名
	 * @param execution
	 *            执行逻辑
	 * @return 监听器
	 */
	public Watcher addWatcher(String key, Execution execution) {
		Watcher watcher = new Watcher(execution);
		addWatcher(key, watcher);
		return watcher;
	}

	/**
	 * 添加一个变量的监听器。
	 * 
	 * @param key
	 *            变量名
	 * @param id
	 *            ID
	 * @param execution
	 *            执行逻辑
	 * @return 监听器
	 */
	public Watcher addWatcher(String key, String id, Execution execution) {
		Watcher watcher = new Watcher(id, execution);
		addWatcher(key, watcher);
		return watcher;
	}

	/**
	 * 添加一个变量的监听器。
	 * 
	 * @param key
	 *            变量名
	 * @param script
	 *            脚本
	 * @return 监听器
	 */
	public Watcher addWatcher(String key, String script) {
		return addWatcher(key, new Script(script));
	}

	/**
	 * 添加一个变量的监听器。
	 * 
	 * @param key
	 *            变量名
	 * @param id
	 *            ID
	 * @param script
	 *            脚本
	 * @return 监听器
	 */
	public Watcher addWatcher(String key, String id, String script) {
		return addWatcher(key, id, new Script(script));
	}

	/**
	 * 移除一个变量的监听器。
	 * 
	 * @param key
	 *            变量名
	 * @param id
	 *            ID
	 * @return 移除的监听器
	 */
	public Watcher removeWatcher(String key, String id) {
		Map<String, Watcher> map = watchers.get(key);
		return map != null ? map.remove(id) : null;
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
