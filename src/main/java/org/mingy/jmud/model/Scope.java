package org.mingy.jmud.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mingy.jmud.client.IMudClient;
import org.mingy.jmud.client.SGR;
import org.mingy.jmud.model.Triggers.Line;

/**
 * 模块化的上下文的抽象类。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public abstract class Scope implements IScope {

	private static final Log logger = LogFactory.getLog(Scope.class);

	private String name;
	private Scope parent;
	private Map<String, Scope> children;
	private boolean enabled = true;
	private ScopeBindings variables;
	private Aliases aliases;
	private Triggers triggers;

	Scope(String name, Scope parent) {
		this.name = name;
		this.parent = parent;
		this.children = new HashMap<String, Scope>(8);
		this.variables = parent != null ? new ScopeBindings(
				parent.getVariables()) : new ScopeBindings();
		this.aliases = parent != null ? new Aliases(parent.getAliases())
				: new Aliases();
		this.triggers = new Triggers();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Scope getParent() {
		return parent;
	}

	public Collection<Scope> getChildren() {
		return children.values();
	}

	@Override
	public Scope getChild(String name) {
		return children.get(name);
	}

	@Override
	public Scope addChild(String name) {
		if (name == null)
			throw new NullPointerException("name is null");
		if (getScope(name) != null)
			throw new IllegalArgumentException("duplicate module name: " + name);
		if (logger.isInfoEnabled()) {
			logger.info("[" + getName() + "] create module: " + name);
		}
		Scope module = new Module(name, this);
		children.put(name, module);
		return module;
	}

	@Override
	public Scope removeChild(String name) {
		Scope scope = children.remove(name);
		if (scope != null) {
			if (logger.isInfoEnabled()) {
				logger.info("[" + getName() + "] remove module: " + name);
			}
		}
		return scope;
	}

	@Override
	public abstract Scope getContext();

	protected Scope getSubScope(String name) {
		Scope scope = getChild(name);
		if (scope != null)
			return scope;
		for (Scope child : getChildren()) {
			scope = child.getSubScope(name);
			if (scope != null)
				return scope;
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (this.enabled != enabled) {
			if (logger.isInfoEnabled()) {
				logger.info("[" + getName() + "] "
						+ (enabled ? "enable" : "disable") + " module: " + name);
			}
			this.enabled = enabled;
			echoText("Module " + name + " is "
					+ (enabled ? "enabled" : "disabled") + "\n", SGR.INFO);
		}
	}

	protected IMudClient getClient() {
		return getContext().getClient();
	}

	protected ScriptEngine getJSEngine() {
		return getContext().getJSEngine();
	}

	protected ScopeBindings getVariables() {
		return variables;
	}

	protected ShortKeys getShortKeys() {
		return getContext().getShortKeys();
	}

	protected Aliases getAliases() {
		return aliases;
	}

	protected Triggers getTriggers() {
		return triggers;
	}

	@Override
	public void echoText(String text, String style) {
		getClient().echo(text, style);
	}

	@Override
	public void sendCommand(String command) {
		getClient().send(command);
	}

	@Override
	public Object calcExpression(String expression) throws Exception {
		Object value = getJSEngine().eval(expression, getVariables());
		if (logger.isDebugEnabled()) {
			logger.debug("[" + getName() + "] CALC: " + expression + " = "
					+ value);
		}
		return value;
	}

	@Override
	public Object getVariable(String name) {
		return getVariables().get(name);
	}

	@Override
	public Object setVariable(String name, Object value) {
		if (logger.isDebugEnabled()) {
			logger.debug("[" + getName() + "] VAR[+]: " + name + " = " + value);
		}
		return getVariables().put(name, value);
	}

	@Override
	public Object removeVariable(String name) {
		if (logger.isDebugEnabled()) {
			logger.debug("[" + getName() + "] VAR[-]: " + name);
		}
		return getVariables().remove(name);
	}

	@Override
	public ShortKey getShortKey(int key) {
		return getShortKeys().get(key);
	}

	@Override
	public ShortKey setShortKey(int key, String command) {
		if (logger.isDebugEnabled()) {
			logger.debug("[" + getName() + "] SHORTKEY[+]: " + key + " = "
					+ command);
		}
		return getShortKeys().set(key, command);
	}

	@Override
	public ShortKey removeShortKey(int key) {
		if (logger.isDebugEnabled()) {
			logger.debug("[" + getName() + "] SHORTKEY[-]: " + key);
		}
		return getShortKeys().remove(key);
	}

	@Override
	public Alias getAlias(String name) {
		return getAliases().get(name);
	}

	@Override
	public Alias setAlias(String name, String script) {
		if (logger.isDebugEnabled()) {
			logger.debug("[" + getName() + "] ALIAS[+]: " + name + " = "
					+ script);
		}
		return getAliases().set(name, script);
	}

	@Override
	public Alias removeAlias(String name) {
		if (logger.isDebugEnabled()) {
			logger.debug("[" + getName() + "] ALIAS[-]: " + name);
		}
		return getAliases().remove(name);
	}

	@Override
	public Collection<Trigger> getTriggers(String group) {
		return getTriggers().get(group);
	}

	@Override
	public boolean addTrigger(String group, Trigger trigger) {
		boolean b = getTriggers().add(group, trigger);
		if (b) {
			if (logger.isDebugEnabled()) {
				logger.debug("["
						+ getName()
						+ "] TRIGGER[+]: "
						+ (group != null && group.length() > 0 ? "(" + group
								+ ") " : "") + trigger);
			}
		}
		return b;
	}

	@Override
	public Trigger addTrigger(String group, String regex, String script) {
		Trigger trigger = getTriggers().add(group, regex, script);
		if (trigger != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("["
						+ getName()
						+ "] TRIGGER[+]: "
						+ (group != null && group.length() > 0 ? "(" + group
								+ ") " : "") + trigger);
			}
		}
		return trigger;
	}

	@Override
	public boolean removeTrigger(String group, Trigger trigger) {
		if (logger.isDebugEnabled()) {
			logger.debug("["
					+ getName()
					+ "] TRIGGER[-]: "
					+ (group != null && group.length() > 0 ? "(" + group + ") "
							: "") + trigger);
		}
		return getTriggers().remove(group, trigger);
	}

	@Override
	public boolean isTriggerEnabled(String group) {
		return getTriggers().isEnabled(group);
	}

	@Override
	public boolean setTriggerEnabled(String group, boolean enabled) {
		boolean all = false;
		if ("**".equals(group)) {
			group = "*";
			all = true;
		}
		boolean b = getTriggers().setEnabled(group, enabled);
		if (b) {
			if (logger.isDebugEnabled()) {
				logger.debug("["
						+ getName()
						+ "] TRIGGER: "
						+ (enabled ? "enable" : "disable")
						+ (group != null && group.length() > 0 ? " "
								+ ("*".equals(group) ? "ALL" : group) : ""));
			}
			String m = this instanceof Context ? "root"
					: ("module " + getName());
			if ("*".equals(group)) {
				echoText("All triggers in " + m + " is "
						+ (enabled ? "enabled" : "disabled") + "\n", SGR.INFO);
			} else {
				echoText(
						(group == null || group.length() == 0 ? "Ungrouped triggers"
								: ("Trigger group " + group))
								+ " in "
								+ m
								+ " is "
								+ (enabled ? "enabled" : "disabled") + "\n",
						SGR.INFO);
			}
		}
		if (all) {
			for (Scope child : getChildren()) {
				child.setTriggerEnabled("**", enabled);
			}
		}
		return b;
	}

	protected void handleLine(Line line) {
		if (logger.isTraceEnabled()) {
			logger.trace("[" + getName() + "] >>> " + line.text);
		}
		getTriggers().handleLine(this, line);
		for (Scope child : getChildren()) {
			child.handleLine(line);
		}
	}
}