package org.mingy.jmud.model;

/**
 * 可执行逻辑的抽象类。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public abstract class Execution {

	/**
	 * 指示{@link #execute(IScope, String[])}是否在另一个线程中执行。
	 * 
	 * @return true时在新的工作线程中执行（默认），false时在当前线程中执行
	 */
	public boolean shallForkThread() {
		return true;
	}

	/**
	 * 执行一段逻辑，如脚本、程序段等。
	 * 
	 * @param scope
	 *            上下文
	 * @param args
	 *            参数
	 * @throws InterruptedException
	 */
	public abstract void execute(IScope scope, String[] args)
			throws InterruptedException;
}
