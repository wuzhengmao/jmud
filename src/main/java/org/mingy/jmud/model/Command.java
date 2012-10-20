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

	/** 指令头 */
	protected String header;

	/** 参数 */
	protected String[] args;

	/**
	 * 执行指令。
	 * 
	 * @param scope
	 *            上下文
	 * @return true表示执行成功
	 * @throws Exception
	 */
	public abstract boolean execute(IScope scope) throws Exception;

	protected static Object[] getScopeByPath(IScope scope, String path) {
		if (path == null)
			return null;
		else if (path.isEmpty())
			return new Object[] { scope, path };
		IScope r = scope;
		int k = -1;
		int n = 0;
		for (int i = 0; i < path.length(); i++) {
			char b = path.charAt(i);
			if (k == -1) {
				if (b == '/') {
					r = scope.getContext();
					i = path.indexOf('/', 1);
					if (i > 0) {
						if (path.indexOf('/', i + 1) > 0)
							return null;
						if (i > 1) {
							String s = path.substring(1, i);
							r = r.getScope(s);
							if (r == null)
								return null;
						}
						return new Object[] { r, path.substring(i + 1) };
					} else {
						return new Object[] { r, path.substring(1) };
					}
				} else if (b == '.') {
					k = 1;
				} else {
					k = 0;
					break;
				}
			} else if (k == 0) {
				if (b == '/') {
					continue;
				} else if (b == '.') {
					k = 1;
				} else {
					break;
				}
			} else if (k == 1) {
				if (b == '/') {
					k = 0;
					n = i + 1;
					continue;
				} else if (b == '.') {
					k = 2;
				} else {
					return null;
				}
			} else if (k == 2) {
				if (b == '/') {
					r = r.getParent();
					if (r == null)
						return null;
					k = 0;
					n = i + 1;
				} else {
					return null;
				}
			}
		}
		if (k != 0 || path.indexOf('/', n) > 0)
			return null;
		return new Object[] { r, path.substring(n) };
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [header=" + header
				+ ", args=" + Arrays.toString(args) + "]";
	}
}
