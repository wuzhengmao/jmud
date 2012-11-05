package org.mingy.jmud.model;

import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.mingy.jmud.client.ConnectionEvent;
import org.mingy.jmud.client.ConnectionStates;
import org.mingy.jmud.client.IConnectionStateListener;
import org.mingy.jmud.client.IMudClient;
import org.mingy.jmud.model.Triggers.Line;
import org.mingy.jmud.util.WorkThreadFactory;

/**
 * 上下文。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Context extends Scope {

	/** 为触发器处理保留的最大行数 */
	private static final int MAX_LINES = 10;
	/** 最大的工作线程数 */
	private static final int MAX_WORK_THREADS = 20;

	/** MUD客户端 */
	private IMudClient client;
	/** JS引擎 */
	private ScriptEngine JSEngine;
	/** 快捷键定义 */
	private ShortKeys shortKeys;
	/** 最后处理行 */
	private Line last;
	/** 工作线程池 */
	private ExecutorService workThreadPool;
	/** 定时服务 */
	private java.util.Timer timerService;

	/**
	 * 构造器。
	 * 
	 * @param client
	 *            MUD客户端
	 */
	public Context(IMudClient client) {
		super("root", null);
		this.client = client;
		JSEngine = new ScriptEngineManager().getEngineByName("JavaScript");
		shortKeys = new ShortKeys();
		workThreadPool = new ThreadPoolExecutor(MAX_WORK_THREADS,
				MAX_WORK_THREADS, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(),
				WorkThreadFactory.getInstance());
		timerService = new java.util.Timer(true);
	}

	@Override
	public Scope getContext() {
		return this;
	}

	@Override
	public Scope getScope(String name) {
		return getSubScope(name);
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (!enabled)
			throw new UnsupportedOperationException("can not disable context");
	}

	@Override
	protected IMudClient getClient() {
		return client;
	}

	@Override
	protected ScriptEngine getJSEngine() {
		return JSEngine;
	}

	@Override
	protected ShortKeys getShortKeys() {
		return shortKeys;
	}

	/**
	 * 初始化。
	 * 
	 * @param session
	 *            会话连接信息
	 */
	public void init(Session session) {
		getVariables().put("context", this);
		getVariables().put("client", client);
		try {
			JSEngine.eval("function $(cmd) {client.send(cmd);}", getVariables());
			JSEngine.eval(
					"function $alias(name, script) {eval(name+'=function($0,$1,$2,$3,$4,$5,$6,$7,$8,$9) {'+script+'}')}",
					getVariables());
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		}
		final Scope module = addChild(Constants.MODULE_LOGIN);
		module.setVariable(Constants.VAR_CHARACTER, session.getCharacter(),
				false);
		module.setVariable(Constants.VAR_PASSWORD, session.getPassword(), false);
		module.setAlias(Constants.ALIAS_ON_CONNECTED, "#set "
				+ Constants.VAR_LOGIN_OK + " 0;#set " + Constants.VAR_IP_RECORD
				+ " 'N/A';#t+ " + Constants.TRIGGER_GROUP_LOGIN);
		module.setAlias(
				Constants.ALIAS_ON_DISCONNECTED,
				"#if {"
						+ Constants.VAR_IP_RECORD
						+ " == 'N/A'} {#echo {\\nRetry connect after 10 seconds ...\\n};#var i 10;#while {true} {#wait 1000;#if {"
						+ Constants.VAR_CONNECTION_STATE
						+ " < 2} {#return};#var i {i - 1};#if {i == 0} {#break};#echo {@i ...\\n};};#reconnect;}");
		client.addConnectionStateListener(new IConnectionStateListener() {
			@Override
			public void onStateChanged(ConnectionEvent event) {
				ConnectionStates state = event.getNewState();
				module.setVariable(Constants.VAR_CONNECTION_STATE,
						state.ordinal(), false);
				if (state == ConnectionStates.CONNECTED) {
					Alias alias = module.getAlias(Constants.ALIAS_ON_CONNECTED);
					if (alias != null)
						module.execute(alias.getExecution(), null);
				} else if (state == ConnectionStates.DISCONNECTED) {
					Alias alias = module
							.getAlias(Constants.ALIAS_ON_DISCONNECTED);
					if (alias != null)
						module.execute(alias.getExecution(), null);
				}
			}
		});
		if (session.getConfiguration() != null)
			session.getConfiguration().inject(this);
	}

	/**
	 * 销毁。
	 */
	public void destroy() {
		timerService.cancel();
		workThreadPool.shutdownNow();
	}

	/**
	 * 使用触发器对文本进行检查。
	 * 
	 * @param text
	 *            文本
	 * @param force
	 *            true时强制处理，否则只有在一行结束后再处理
	 */
	public void handleText(String text, boolean force) {
		if (last == null || last.getText().endsWith("\n")) {
			Line line = new Line(text);
			line.prev = last;
			if (last != null)
				last.next = line;
			last = line;
		} else {
			last.text += text;
		}
		if (force || last.text.endsWith("\n")) {
			handleLine(last);
		}
		Line line = last;
		for (int i = 0; i < MAX_LINES; i++) {
			if (line == null)
				break;
			line = line.prev;
		}
		if (line != null) {
			line.next.prev = null;
			line.next = null;
		}
	}

	@Override
	public void runOnWorkThread(Runnable runnable) {
		if (WorkThreadFactory.inWorkThread())
			runnable.run();
		else
			workThreadPool.execute(runnable);
	}

	@Override
	public void scheduleRun(TimerTask task, long period) {
		timerService.scheduleAtFixedRate(task, period, period);
	}
}
