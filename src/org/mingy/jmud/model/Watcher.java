package org.mingy.jmud.model;

/**
 * 监听器。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Watcher {

	private static int SEQ = 0;

	/** ID */
	private String id;

	/** 执行逻辑 */
	private Execution execution;

	/**
	 * 构造器。
	 * 
	 * @param execution
	 *            执行逻辑
	 */
	public Watcher(Execution execution) {
		this.id = "__watcher_" + (++SEQ);
		this.execution = execution;
	}

	/**
	 * 构造器。
	 * 
	 * @param id
	 *            ID
	 * @param execution
	 *            执行逻辑
	 */
	public Watcher(String id, Execution execution) {
		this.id = id;
		this.execution = execution;
	}

	/**
	 * 返回ID。
	 * 
	 * @return ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 返回执行逻辑。
	 * 
	 * @return 执行逻辑
	 */
	public Execution getExecution() {
		return execution;
	}

	@Override
	public String toString() {
		return "Watcher [id=" + id + ", execution=" + execution + "]";
	}
}
