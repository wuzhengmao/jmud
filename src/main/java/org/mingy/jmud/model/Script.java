package org.mingy.jmud.model;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mingy.jmud.client.SGR;

/**
 * 可执行的脚本。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Script implements IExecution {

	/** 日志 */
	private static final Log logger = LogFactory.getLog(Script.class);

	/** 脚本内容 */
	private String content;

	/** 解析后的指令 */
	private List<Command> commands;

	/**
	 * 构造器。
	 * 
	 * @param content
	 *            脚本内容
	 */
	public Script(String content) {
		setContent(content);
	}

	@Override
	public void execute(IScope scope, String[] args) {
		if (commands == null || commands.isEmpty())
			return;
		for (Command cmd : commands) {
			try {
				if (!cmd.execute(scope, args)) {
					if (logger.isWarnEnabled()) {
						logger.warn("ignore: " + cmd.origin);
					}
					scope.echoText("ERROR: " + cmd.origin + "\n", SGR.ERROR);
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("error on execute: " + cmd.origin, e);
				}
				scope.echoText("ERROR: " + cmd.origin + "\n", SGR.ERROR);
			}
		}
	}

	/**
	 * 返回脚本内容。
	 * 
	 * @return 脚本内容
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 设置脚本内容。
	 * 
	 * @param content
	 *            脚本内容
	 */
	public void setContent(String content) {
		if (content != null) {
			if (!content.equals(this.content)) {
				this.content = content;
				this.commands = Commands.parse(content);
			}
		} else {
			this.content = null;
			this.commands = null;
		}
	}

	@Override
	public String toString() {
		return "Script [content=" + content + ", commands=" + commands + "]";
	}
}
