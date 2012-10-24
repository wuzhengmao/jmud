package org.mingy.jmud.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 表达式。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Expression {

	/** 日志 */
	private static final Log logger = LogFactory.getLog(Expression.class);

	/** 脚本内容 */
	private String content;

	/**
	 * 构造器。
	 * 
	 * @param content
	 *            脚本内容
	 */
	public Expression(String content) {
		setContent(content);
	}

	/**
	 * 运算表达式。
	 * 
	 * @param scope
	 *            上下文
	 * @return 运算结果
	 * @throws Exception
	 */
	public Object compute(Scope scope) throws Exception {
		String expression = Commands.replaceExpression(scope, content);
		Object value = scope.getJSEngine().eval(expression,
				scope.getVariables());
		if (logger.isDebugEnabled()) {
			logger.debug("[" + scope.getName() + "] CALC: " + expression
					+ " = " + value);
		}
		return value;
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
		this.content = content;
	}

	@Override
	public String toString() {
		return "Expression [content=" + content + "]";
	}
}
