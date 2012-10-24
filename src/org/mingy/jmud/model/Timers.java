package org.mingy.jmud.model;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

/**
 * 定时器的定义和控制。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Timers {

	/** 定时服务 */
	static final java.util.Timer TIMER;

	/** 所有的定时器 */
	private final Map<String, Timer> ALL;

	/** 上下文 */
	private final IScope scope;

	static {
		TIMER = new java.util.Timer(true);
	}

	/**
	 * 构造器。
	 * 
	 * @param scope
	 *            上下文
	 */
	public Timers(IScope scope) {
		this.scope = scope;
		ALL = new HashMap<String, Timer>();
	}

	/**
	 * 取得一个定时器。
	 * 
	 * @param name
	 *            名称
	 * @return 定时器，未找到时返回null
	 */
	public Timer get(String name) {
		return ALL.get(name);
	}

	/**
	 * 设置一个定时器。
	 * 
	 * @param name
	 *            名称
	 * @param execution
	 *            执行逻辑
	 * @return 新增或修改后的定时器
	 */
	public Timer set(String name, IExecution execution) {
		Timer timer = ALL.get(name);
		if (timer != null) {
			timer.setExecution(execution);
		} else {
			timer = new Timer(name, execution);
			ALL.put(name, timer);
		}
		return timer;
	}

	/**
	 * 设置一个定时器。
	 * 
	 * @param name
	 *            名称
	 * @param script
	 *            脚本
	 * @return 新增或修改后的定时器
	 */
	public Timer set(String name, String script) {
		Timer timer = ALL.get(name);
		if (timer != null) {
			IExecution execution = timer.getExecution();
			if (execution instanceof Script) {
				((Script) execution).setContent(script);
			} else {
				timer.setExecution(new Script(script));
			}
		} else {
			timer = new Timer(name, new Script(script));
			ALL.put(name, timer);
		}
		return timer;
	}

	/**
	 * 移除一个定时器。
	 * 
	 * @param name
	 *            名称
	 * @return 移除的定时器，未找到时返回null
	 */
	public Timer remove(String name) {
		return ALL.remove(name);
	}

	/**
	 * 重置定时间隔。<br>
	 * 如定时间隔大于0则开启，否则关闭。
	 * 
	 * @param name
	 *            名称
	 * @param tick
	 *            定时间隔（毫秒）
	 * @return 定时器，未找到时返回null
	 */
	public Timer reset(String name, int tick) {
		Timer timer = get(name);
		if (timer != null) {
			if (timer.task != null)
				timer.task.cancel();
			timer.setTick(tick > 0 ? tick : 0);
			timer.setStart(tick > 0);
			if (timer.isStart()) {
				timer.task = new Task(timer);
				TIMER.scheduleAtFixedRate(timer.task, tick, tick);
			}
		}
		return timer;
	}

	/**
	 * 切换定时器开关状态。<br>
	 * 如定时间隔不大于0时无效。
	 * 
	 * @param name
	 *            名称
	 * @return 定时器，未找到时返回null
	 */
	public Timer turn(String name) {
		Timer timer = get(name);
		if (timer != null && timer.getTick() > 0) {
			if (timer.task != null)
				timer.task.cancel();
			timer.setStart(!timer.isStart());
			if (timer.isStart()) {
				timer.task = new Task(timer);
				TIMER.scheduleAtFixedRate(timer.task, timer.getTick(),
						timer.getTick());
			}
		}
		return timer;
	}

	/**
	 * 定时任务。
	 */
	class Task extends TimerTask {

		private Timer timer;

		Task(Timer timer) {
			this.timer = timer;
		}

		@Override
		public void run() {
			final IExecution execution = timer.getExecution();
			if (execution != null) {
				scope.runOnWorkThread(new Runnable() {
					@Override
					public void run() {
						scope.execute(execution, null);
					}
				});
			}
		}
	}
}
