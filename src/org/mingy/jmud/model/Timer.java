package org.mingy.jmud.model;

import java.util.TimerTask;

/**
 * 定时器。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Timer {

	/** 名称 */
	private String name;

	/** 执行逻辑 */
	private Execution execution;

	/** 时间间隔（毫秒） */
	private int tick;

	/** 是否启动 */
	private boolean start;

	/** 定时任务 */
	TimerTask task;

	/**
	 * 构造器。
	 * 
	 * @param name
	 *            名称
	 * @param execution
	 *            执行逻辑
	 */
	Timer(String name, Execution execution) {
		this.name = name;
		this.execution = execution;
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
	 * 返回执行逻辑。
	 * 
	 * @return 执行逻辑
	 */
	public Execution getExecution() {
		return execution;
	}

	/**
	 * 设置执行逻辑。
	 * 
	 * @param execution
	 *            执行逻辑
	 */
	public void setExecution(Execution execution) {
		this.execution = execution;
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

	@Override
	public String toString() {
		return "Timer [name=" + name + ", execution=" + execution + ", tick="
				+ tick + ", start=" + start + "]";
	}
}
