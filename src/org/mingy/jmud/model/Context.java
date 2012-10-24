package org.mingy.jmud.model;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.mingy.jmud.client.IMudClient;
import org.mingy.jmud.model.Triggers.Line;

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
	/** 最大的输入线程数 */
	private static final int MAX_INPUT_THREADS = 3;

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
	/** 输入线程池 */
	private ExecutorService inputThreadPool;

	public Context() {
		super("root", null);
	}

	@Override
	public Scope getContext() {
		return this;
	}

	@Override
	public IScope getScope(String name) {
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
	 * @param client
	 *            MUD客户端
	 */
	public void init(IMudClient client) {
		this.client = client;
		JSEngine = new ScriptEngineManager().getEngineByName("JavaScript");
		shortKeys = new ShortKeys();
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
		workThreadPool = Executors.newFixedThreadPool(MAX_WORK_THREADS);
		inputThreadPool = Executors.newFixedThreadPool(MAX_INPUT_THREADS);
	}

	/**
	 * 销毁。
	 */
	public void destroy() {
		Timers.TIMER.cancel();
		workThreadPool.shutdownNow();
		inputThreadPool.shutdownNow();
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
		workThreadPool.execute(runnable);
	}

	@Override
	public void runOnInputThread(Runnable runnable) {
		inputThreadPool.execute(runnable);
	}
}
