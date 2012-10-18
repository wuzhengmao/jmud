package org.mingy.jmud.model;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.mingy.jmud.client.IMudClient;

/**
 * 上下文及配置。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Context {

	/** MUD客户端 */
	public IMudClient CLIENT;

	/** JS脚本引擎 */
	public ScriptEngine JS;

	/** 快捷键定义 */
	public ShortKeys SHORT_KEYS;

	/** 别名定义 */
	public Aliases ALIASES;

	/** 触发器定义 */
	public Triggers TRIGGERS;

	/**
	 * 初始化。
	 * 
	 * @param client
	 *            MUD客户端
	 */
	public void init(IMudClient client) {
		CLIENT = client;
		JS = new ScriptEngineManager().getEngineByName("JavaScript");
		JS.put("context", this);
		JS.put("client", client);
		try {
			JS.eval("function $(cmd) {client.send(cmd);}");
			JS.eval("function $alias(name, script) {eval(name+'=function($0,$1,$2,$3,$4,$5,$6,$7,$8,$9) {'+script+'}')}");
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		}
		SHORT_KEYS = new ShortKeys();
		ALIASES = new Aliases();
		TRIGGERS = new Triggers(this);
	}
}
