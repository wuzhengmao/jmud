package org.mingy.jmud.model;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 指令抽象类。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public abstract class Command {

	/** 日志 */
	protected static final Log logger = LogFactory.getLog(Command.class);

	/** 原始指令 */
	protected String origin;

	/** 参数 */
	protected String[] args;

	/**
	 * 执行指令。
	 * 
	 * @param context
	 *            上下文
	 * @return true表示执行成功
	 */
	public abstract boolean execute(Context context);

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [args="
				+ Arrays.toString(args) + "]";
	}
}
