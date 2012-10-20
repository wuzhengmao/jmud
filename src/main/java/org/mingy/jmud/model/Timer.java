package org.mingy.jmud.model;

import org.mingy.jmud.model.Timers.Task;

/**
 * 定时器。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Timer {

	/** 名称 */
	private String name;

	/** 脚本 */
	private String script;

	/** 时间间隔（毫秒） */
	private int tick;

	/** 是否启动 */
	private boolean start;
	
	/** 定时任务 */
	Task task;

	/**
	 * 构造器。
	 * 
	 * @param name
	 *            名称
	 * @param script
	 *            脚本
	 */
	Timer(String name, String script) {
		this.name = name;
		this.script = script;
	}

	/**
	 * 返回名称。
	 * 
	 * @return 名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 返回脚本。
	 * 
	 * @return 脚本
	 */
	public String getScript() {
		return script;
	}

	/**
	 * 设置脚本。
	 * 
	 * @param script
	 *            脚本
	 */
	void setScript(String script) {
		this.script = script;
	}

	/**
	 * 返回时间间隔。
	 * 
	 * @return 时间间隔（毫秒）
	 */
	public int getTick() {
		return tick;
	}

	/**
	 * 设置时间间隔。
	 * 
	 * @param tick
	 *            时间间隔（毫秒）
	 */
	void setTick(int tick) {
		this.tick = tick;
	}

	/**
	 * 返回定时器是否启动。
	 * 
	 * @return true为启动
	 */
	public boolean isStart() {
		return start;
	}

	/**
	 * 设置定时器是否启动。
	 * 
	 * @param start
	 *            true为启动
	 */
	void setStart(boolean start) {
		this.start = start;
	}
}
