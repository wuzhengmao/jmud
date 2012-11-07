package org.mingy.jmud.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

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
	private Timers timers;

	Scope(String name, Scope parent) {
		this.name = name;
		this.parent = parent;
		this.children = new HashMap<String, Scope>(8);
		this.variables = parent != null ? new ScopeBindings(this,
				parent.getVariables()) : new ScopeBindings(this);
		this.aliases = parent != null ? new Aliases(parent.getAliases())
				: new Aliases();
		this.triggers = new Triggers(this);
		this.timers = new Timers(this);
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
		Scope module = getChild(name);
		if (module == null) {
			if (getScope(name) != null)
				throw new IllegalArgumentException("duplicated name: " + name);
			if (logger.isInfoEnabled()) {
				logger.info("[" + getName() + "] create module: " + name);
			}
			module = new Module(name, this);
			children.put(name, module);
		}
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

	@Override
	public abstract Scope getScope(String name);

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
			echoText("Module [" + name + "] is "
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

	protected Timers getTimers() {
		return timers;
	}

	@Override
	public void reconnect() {
		disconnect();
		getClient().connect();
	}

	@Override
	public void disconnect() {
		if (!getClient().isDisconnected())
			getClient().disconnect();
	}

	@Override
	public void showText(String text, String style) {
		getClient().show(text, style, false);
	}

	@Override
	public void echoText(String text, String style) {
		getClient().echo(text, style);
	}

	@Override
	public void hideText(String text) {
		getClient().hide(text);
	}

	@Override
	public void sendCommand(String command, boolean echo) {
		getClient().send(command, echo);
	}

	@Override
	public void runOnWorkThread(Runnable runnable) {
		getContext().runOnWorkThread(runnable);
	}

	@Override
	public void scheduleRun(TimerTask task, long period) {
		getContext().scheduleRun(task, period);
	}

	@Override
	public void execute(final Execution execution, final String[] args) {
		if (execution.shallForkThread()) {
			runOnWorkThread(new Runnable() {
				@Override
				public void run() {
					doExecute(execution, args);
				}
			});
		} else {
			doExecute(execution, args);
		}
	}

	private void doExecute(Execution execution, String[] args) {
		try {
			execution.execute(this, args);
		} catch (InterruptExecutionException e) {
			// ignore
		} catch (InterruptedException e) {
			// ignore
		} catch (RuntimeException e) {
			if (logger.isErrorEnabled()) {
				logger.error("error on execute", e);
			}
		}
	}

	@Override
	public void executeScript(String script, String[] args) {
		ScopeBindings.resetLocalVariables();
		execute(new Script(script), args);
	}

	@Override
	public Object calcExpression(String expression) throws Exception {
		return new Expression(expression).compute(this);
	}

	@Override
	public Object getVariable(String name) {
		return getVariables().get(name);
	}

	@Override
	public Object setVariable(String name, Object value, boolean local) {
		if (logger.isDebugEnabled()) {
			logger.debug("[" + getName() + "] VAR[+]: " + name + " = " + value
					+ (local ? " (LOCAL)" : ""));
		}
		if (local)
			return ScopeBindings.setLocalVariable(name, value);
		else
			return getVariables().put(name, value);
	}

	@Override
	public Object removeVariable(String name, boolean local) {
		if (logger.isDebugEnabled()) {
			logger.debug("[" + getName() + "] VAR[-]: " + name
					+ (local ? " (LOCAL)" : ""));
		}
		if (local)
			return ScopeBindings.removeLocalVariable(name);
		else
			return getVariables().remove(name);
	}

	@Override
	public void addWatcher(String name, Watcher watcher) {
		if (logger.isDebugEnabled()) {
			logger.debug("[" + getName() + "] WATCH[+]: " + name + ", "
					+ watcher);
		}
		getVariables().addWatcher(name, watcher);
	}

	@Override
	public Watcher addWatcher(String name, Execution execution) {
		Watcher watcher = getVariables().addWatcher(name, execution);
		if (logger.isDebugEnabled()) {
			logger.debug("[" + getName() + "] WATCH[+]: " + name + ", "
					+ watcher);
		}
		return watcher;
	}

	@Override
	public Watcher addWatcher(String name, String id, Execution execution) {
		Watcher watcher = getVariables().addWatcher(name, id, execution);
		if (logger.isDebugEnabled()) {
			logger.debug("[" + getName() + "] WATCH[+]: " + name + ", "
					+ watcher);
		}
		return watcher;
	}

	@Override
	public Watcher addWatcher(String name, String script) {
		Watcher watcher = getVariables().addWatcher(name, script);
		if (logger.isDebugEnabled()) {
			logger.debug("[" + getName() + "] WATCH[+]: " + name + ", "
					+ watcher);
		}
		return watcher;
	}

	@Override
	public Watcher addWatcher(String name, String id, String script) {
		Watcher watcher = getVariables().addWatcher(name, id, script);
		if (logger.isDebugEnabled()) {
			logger.debug("[" + getName() + "] WATCH[+]: " + name + ", "
					+ watcher);
		}
		return watcher;
	}

	@Override
	public Watcher removeWatcher(String name, String id) {
		Watcher watcher = getVariables().removeWatcher(name, id);
		if (logger.isDebugEnabled()) {
			logger.debug("[" + getName() + "] WATCH[-]: " + name + ", "
					+ watcher);
		}
		return watcher;
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
	public Alias setAlias(String name, Execution execution) {
		if (logger.isDebugEnabled()) {
			logger.debug("[" + getName() + "] ALIAS[+]: " + name + " = "
					+ execution);
		}
		return getAliases().set(name, execution);
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
	public String expandAlias(String name) {
		return getAliases().getScript(name);
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
	public Trigger addTrigger(String group, String regex, Execution execution) {
		Trigger trigger = getTriggers().add(group, regex, execution);
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
	public Trigger addTrigger(String group, String[] regexes,
			Execution execution) {
		Trigger trigger = getTriggers().add(group, regexes, execution);
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
	public Trigger addTrigger(String group, String[] regexes, String script) {
		Trigger trigger = getTriggers().add(group, regexes, script);
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
			String m = this instanceof Context ? "ROOT" : ("module ["
					+ getName() + "]");
			if ("*".equals(group)) {
				echoText("All triggers in " + m + " is "
						+ (enabled ? "enabled" : "disabled") + "\n", SGR.INFO);
			} else {
				echoText(
						(group == null || group.length() == 0 ? "Ungrouped triggers"
								: ("Trigger group [" + group) + "]")
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
		getTriggers().handleLine(line);
		for (Scope child : getChildren()) {
			child.handleLine(line);
		}
	}

	@Override
	public Timer getTimer(String name) {
		return getTimers().get(name);
	}

	@Override
	public Timer setTimer(String name, Execution execution) {
		if (logger.isDebugEnabled()) {
			logger.debug("[" + getName() + "] TIMER[+]: " + name + " = "
					+ execution);
		}
		return getTimers().set(name, execution);
	}

	@Override
	public Timer setTimer(String name, String script) {
		if (logger.isDebugEnabled()) {
			logger.debug("[" + getName() + "] TIMER[+]: " + name + " = "
					+ script);
		}
		return getTimers().set(name, script);
	}

	@Override
	public Timer removeTimer(String name) {
		if (logger.isDebugEnabled()) {
			logger.debug("[" + getName() + "] TIMER[-]: " + name);
		}
		return getTimers().remove(name);
	}

	@Override
	public Timer resetTimer(String name, int tick) {
		Timer timer = getTimers().reset(name, tick);
		if (timer != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("[" + getName() + "] TIMER: set " + name
						+ " tick to " + timer.getTick());
			}
			String m = this instanceof Context ? "ROOT" : ("module ["
					+ getName() + "]");
			echoText(
					"Reset timer ["
							+ name
							+ "] in "
							+ m
							+ " to "
							+ (timer.getTick() < 1000 ? (timer.getTick() + "ms")
									: (timer.getTick() / 1000.0 + "s")) + "\n",
					SGR.INFO);
		}
		return timer;
	}

	@Override
	public Timer switchTimer(String name) {
		Timer timer = getTimers().turn(name);
		if (timer != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("[" + getName() + "] TIMER: turn " + name
						+ (timer.isStart() ? " on" : " off"));
			}
			String m = this instanceof Context ? "ROOT" : ("module ["
					+ getName() + "]");
			echoText("Turn timer [" + name + "] in " + m
					+ (timer.isStart() ? " ON" : " OFF") + "\n", SGR.INFO);
		}
		return timer;
	}
}
