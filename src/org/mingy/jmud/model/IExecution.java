package org.mingy.jmud.model;

/**
 * 可执行的接口。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public interface IExecution {

	/**
	 * 执行一段逻辑，如脚本、程序段等。
	 * 
	 * @param scope
	 *            上下文
	 * @param args
	 *            参数
	 */
	void execute(IScope scope, String[] args);
}
