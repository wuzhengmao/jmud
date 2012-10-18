package org.mingy.jmud.model;

import java.util.ArrayList;
import java.util.List;

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
		TRIGGERS = new Triggers(this);
	}

	/**
	 * 执行指令。
	 * 
	 * @param command
	 *            指令
	 */
	public void execute(String command) {
		for (String cmd : splitCmds(command)) {
			if (!cmd.isEmpty() && cmd.charAt(0) == '#') {
				
			} else {
				CLIENT.send(cmd);
			}
		}
	}

	private static List<String> splitCmds(String command) {
		List<String> cmds = new ArrayList<String>();
		int p = 0;
		int k = 0;
		for (int i = 0; i < command.length(); i++) {
			char b = command.charAt(i);
			if (k == 0 && b == ';') {
				if (i > p) {
					String cmd = command.substring(p, i).trim();
					if (!cmd.isEmpty())
						cmds.add(cmd);
				}
				p = i + 1;
			} else if (b == '{') {
				k++;
			} else if (b == '}') {
				if (k > 0)
					k--;
			}
		}
		if (p < command.length()) {
			String cmd = command.substring(p).trim();
			if (!cmd.isEmpty())
				cmds.add(cmd);
		}
		if (cmds.isEmpty())
			cmds.add("");
		return cmds;
	}
	
	
}
