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

	/** 参数 */
	protected String[] args;

	/**
	 * 执行指令。
	 * 
	 * @param context
	 *            上下文
	 * @param extras
	 *            外部传递的参数
	 */
	public abstract void execute(Context context, String[] extras);

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [args="
				+ Arrays.toString(args) + "]";
	}
}
