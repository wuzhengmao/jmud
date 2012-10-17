package org.mingy.jmud.model;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.mingy.jmud.client.IMudClient;

/**
 * 上下文及配置。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Context {

	/** MUD客户端 */
	private IMudClient client;

	/** JS脚本引擎 */
	public ScriptEngine JS;

	/** 快捷键定义 */
	public ShortKeys SHORT_KEYS;

	/** 触发器定义 */
	public Triggers TRIGGERS;

	/**
	 * 初始化
	 * 
	 * @param client
	 *            MUD客户端
	 */
	public void init(IMudClient client) {
		this.client = client;
		JS = new ScriptEngineManager().getEngineByName("JavaScript");
		JS.put("context", this);
		JS.put("client", client);
		SHORT_KEYS = new ShortKeys();
		TRIGGERS = new Triggers(this);
	}
}
