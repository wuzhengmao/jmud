package org.mingy.jmud.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 触发器的定义和处理。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Triggers {

	/** 序列号 */
	private static int SEQ = 0;

	/** 所有触发器 */
	private Map<String, TriggerGroup> groups;

	/**
	 * 构造器。
	 * 
	 * @param scope
	 *            上下文
	 */
	public Triggers() {
		groups = new ConcurrentHashMap<String, TriggerGroup>();
		groups.put("", new TriggerGroup());
	}

	/**
	 * 取得一组触发器。
	 * 
	 * @param group
	 *            组名
	 * @return 一组触发器
	 */
	public Collection<Trigger> get(String group) {
		if (group == null)
			group = "";
		TriggerGroup tg = groups.get(group);
		return tg != null ? tg.triggers.values() : null;
	}

	/**
	 * 添加一个触发器。
	 * 
	 * @param group
	 *            组名
	 * @param trigger
	 *            触发器
	 * @return true表示添加成功，如该触发器已存在则返回false
	 */
	public boolean add(String group, Trigger trigger) {
		if (group == null)
			group = "";
		TriggerGroup tg = groups.get(group);
		if (tg == null) {
			tg = new TriggerGroup();
			tg.name = group;
			groups.put(group, tg);
		}
		if (!tg.triggers.containsValue(trigger)) {
			tg.triggers.put(++SEQ, trigger);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 添加一个简单的触发器。
	 * 
	 * @param group
	 *            组名
	 * @param regex
	 *            正则表达式
	 * @param script
	 *            脚本
	 * @return 新增的触发器
	 */
	public Trigger add(String group, String regex, String script) {
		if (group == null)
			group = "";
		Trigger trigger = new SimpleTrigger(regex, script);
		add(group, trigger);
		return trigger;
	}

	/**
	 * 移除一个触发器。
	 * 
	 * @param group
	 *            组名
	 * @param trigger
	 *            触发器
	 * @return true表示移除成功，如该触发器不存在则返回false
	 */
	public boolean remove(String group, Trigger trigger) {
		if (group == null)
			group = "";
		TriggerGroup tg = groups.get(group);
		if (tg != null) {
			for (Entry<Integer, Trigger> entry : tg.triggers.entrySet()) {
				if (entry.getValue().equals(trigger)) {
					tg.triggers.remove(entry.getKey());
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 返回触发器组是否启用。
	 * 
	 * @param group
	 *            组名
	 * @return true为已启用，如组不存在也会返回false
	 */
	public boolean isEnabled(String group) {
		if (group == null)
			group = "";
		TriggerGroup tg = groups.get(group);
		return tg != null ? tg.enabled : false;
	}

	/**
	 * 设置触发器组是否启用。
	 * 
	 * @param group
	 *            组名，"*"表示所有组
	 * @param enabled
	 *            true为已启用
	 * @return 如组不存在时返回false
	 */
	public boolean setEnabled(String group, boolean enabled) {
		if (group == null)
			group = "";
		if ("*".equals(group)) {
			for (TriggerGroup tg : groups.values())
				tg.enabled = enabled;
			return true;
		} else {
			TriggerGroup tg = groups.get(group);
			if (tg != null) {
				tg.enabled = enabled;
				return true;
			} else {
				return false;
			}
		}
	}

	public void handleLine(IScope scope, Line line) {
		for (TriggerGroup group : groups.values()) {
			for (Entry<Integer, Trigger> entry : group.triggers.entrySet()) {
				int id = entry.getKey();
				Trigger trigger = entry.getValue();
				int n = trigger.requiresLineCount();
				if (n > 1) {
					Line prev = line;
					while (n > 1 && (prev = prev.prev) != null) {
						n--;
					}
					if (prev == null)
						continue;
				}
				if (group.enabled) {
					Integer pos = line.ptrs.get(id);
					String[] args = trigger.match(line, pos != null ? pos : 0);
					if (args != null) {
						line.ptrs.put(id, line.text.length());
						trigger.execute(scope, args);
					}
				} else {
					line.ptrs.put(id, line.text.length());
				}
			}
		}
	}

	/**
	 * 一行文本，并包含对上下行的引用。
	 */
	public static class Line {

		String text;
		Line prev;
		Line next;
		Map<Integer, Integer> ptrs;

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

	/**
	 * 一组触发器。
	 */
	static class TriggerGroup {

		String name;
		boolean enabled = true;
		Map<Integer, Trigger> triggers = new ConcurrentHashMap<Integer, Trigger>();
	}
}
