package org.mingy.jmud.model;

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

	/** 分割位置 */
	protected int[][] splits;

	private String header;
	private String[] _items, items;
	private String[] _args;

	/**
	 * 执行指令。
	 * 
	 * @param scope
	 *            上下文
	 * @param args
	 *            参数
	 * @return true表示执行成功
	 * @throws Exception
	 */
	public boolean execute(IScope scope, String[] args) throws Exception {
		if (header == null) {
			_items = new String[splits.length - 1];
			for (int i = 0; i < splits.length; i++) {
				int[] r = splits[i];
				if (i == 0) {
					header = origin.substring(r[0], r[1]);
				} else {
					_items[i - 1] = origin.substring(r[0], r[1]);
				}
			}
			prepare(args);
		} else if (!isSameArgs(args)) {
			prepare(args);
		}
		return execute(scope, header, items, args);
	}

	private void prepare(String[] args) {
		items = new String[_items.length];
		for (int i = 0; i < items.length; i++)
			items[i] = Commands.replaceArgs(_items[i], args);
		_args = args;
		prepare(header, items, args);
	}

	/**
	 * 执行前准备。<br>
	 * 该方法只有在第一次执行前或执行参数变化后被调用。
	 * 
	 * @param header
	 *            指令头
	 * @param items
	 *            原始的未经过参数替换的指令项
	 * @param args
	 *            参数
	 */
	protected void prepare(String header, String[] items, String[] args) {
		// nothing to do
	}

	/**
	 * 判断参数是否发生变化。
	 * 
	 * @param args
	 *            参数
	 * @return true表示参数未变
	 */
	private boolean isSameArgs(String[] args) {
		if (_args == args)
			return true;
		if ((_args == null && (args == null || args.length == 0))
				|| (args == null && (_args == null || _args.length == 0)))
			return true;
		if (_args == null || args == null)
			return false;
		if (_args.length != args.length)
			return false;
		for (int i = 0; i < args.length; i++) {
			Object o1 = _args[i];
			Object o2 = args[i];
			if (!(o1 == null ? o2 == null : o1.equals(o2)))
				return false;
		}
		return true;
	}

	/**
	 * 执行指令。
	 * 
	 * @param scope
	 *            上下文
	 * @param header
	 *            指令头
	 * @param items
	 *            指令项
	 * @param args
	 *            参数
	 * @return true表示执行成功
	 * @throws Exception
	 */
	protected abstract boolean execute(IScope scope, String header,
			String[] items, String[] args) throws Exception;

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
		StringBuilder sb = new StringBuilder(getClass().getSimpleName())
				.append(" [origin=").append(origin).append(", splits=[");
		for (int i = 0; i < splits.length; i++) {
			if (i > 0)
				sb.append(", ");
			int[] r = splits[i];
			sb.append(r[0]).append("-").append(r[1]);
		}
		return sb.append("]]").toString();
	}
}
