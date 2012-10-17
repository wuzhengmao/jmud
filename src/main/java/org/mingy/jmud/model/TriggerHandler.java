package org.mingy.jmud.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mingy.jmud.client.IMudClient;

/**
 * 触发器处理程序。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class TriggerHandler {

	/** 日志 */
	private static final Log logger = LogFactory.getLog(TriggerHandler.class);

	/** 最大保留的行数 */
	private static final int MAX_LINES = 10;

	/** MUD客户端 */
	private IMudClient client;
	/** 最后行 */
	private Line last;
	/** 所有触发器 */
	private Map<Integer, ITrigger> triggers;

	/**
	 * 构造器。
	 * 
	 * @param client
	 *            MUD客户端
	 */
	public TriggerHandler(IMudClient client) {
		this.client = client;
		triggers = new ConcurrentHashMap<Integer, ITrigger>();
	}

	/**
	 * 取得ID对应的触发器。
	 * 
	 * @param id
	 *            ID
	 * @return 触发器
	 */
	public ITrigger get(int id) {
		return triggers.get(id);
	}

	/**
	 * 添加一个简单的触发器。
	 * 
	 * @param pattern
	 *            匹配模板
	 * @param command
	 *            触发后执行的指令
	 * @return ID
	 */
	public int add(String pattern, String command) {
		ITrigger trigger = new SimpleTrigger(pattern, command);
		return add(trigger);
	}

	/**
	 * 添加一个触发器。
	 * 
	 * @param trigger
	 *            触发器
	 * @return ID
	 */
	public int add(ITrigger trigger) {
		int i = triggers.size();
		triggers.put(i, trigger);
		return i;
	}

	/**
	 * 移除指定ID的触发器。
	 * 
	 * @param id
	 *            ID
	 * @return 触发器
	 */
	public ITrigger remove(int id) {
		return triggers.remove(id);
	}

	/**
	 * 处理文本。
	 * 
	 * @param text
	 *            文本
	 * @param force
	 *            true时强制处理，否则只有在一行结束后再处理
	 */
	public void handle(String text, boolean force) {
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
			fire(last);
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

	private void fire(Line line) {
		if (logger.isTraceEnabled()) {
			logger.trace("fire: " + line.text);
		}
		for (Entry<Integer, ITrigger> entry : triggers.entrySet()) {
			int id = entry.getKey();
			ITrigger trigger = entry.getValue();
			int n = trigger.requiresLineCount();
			if (n > 1) {
				Line prev = line;
				while (n > 1 && (prev = prev.prev) != null) {
					n--;
				}
				if (prev == null)
					continue;
			}
			if (trigger.isEnabled()) {
				Integer pos = line.ptrs.get(id);
				String[] args = trigger.match(line, pos != null ? pos : 0);
				if (args != null) {
					line.ptrs.put(id, line.text.length());
					trigger.execute(client, args);
				}
			} else {
				line.ptrs.put(id, line.text.length());
			}
		}
	}

	/**
	 * 一行文本，并包含对上下行的引用。
	 */
	public class Line {

		private String text;
		private Line prev;
		private Line next;
		private Map<Integer, Integer> ptrs;

		Line(String text) {
			this.text = text;
			ptrs = new HashMap<Integer, Integer>();
		}

		/**
		 * 返回文本。
		 * 
		 * @return 文本
		 */
		public String getText() {
			return text;
		}

		/**
		 * 取得前一行。
		 * 
		 * @return 前一行
		 */
		public Line getPrevious() {
			return prev;
		}

		/**
		 * 取得后一行。
		 * 
		 * @return 后一行
		 */
		public Line getNext() {
			return next;
		}
	}
}
