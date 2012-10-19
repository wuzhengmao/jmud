package org.mingy.jmud.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mingy.jmud.client.SGR;

/**
 * 指令的定义和处理。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Commands {

	/** 日志 */
	private static final Log logger = LogFactory.getLog(Commands.class);

	/** 指令类型表 */
	private static final Map<String, Class<? extends Command>> TYPES;

	static {
		TYPES = new HashMap<String, Class<? extends Command>>();
		TYPES.put("#al", AliasCommand.class);
		TYPES.put("#alias", AliasCommand.class);
		TYPES.put("#set", SetCommand.class);
	}

	/**
	 * 执行脚本
	 * 
	 * @param scope
	 *            上下文
	 * @param script
	 *            脚本
	 * @param args
	 *            参数
	 */
	public static void execute(IScope scope, String script, String[] args) {
		if (args != null)
			script = replaceArgs(script, args);
		for (Command cmd : parse(script)) {
			if (!cmd.execute(scope)) {
				if (logger.isWarnEnabled()) {
					logger.warn("ignore: " + cmd.origin);
				}
				scope.echoText("ERROR: " + cmd.origin, SGR.ERROR);
			}
		}
	}

	/**
	 * 替换指令中的变量。
	 * 
	 * @param scope
	 *            上下文
	 * @param command
	 *            指令
	 * @return 替换后的指令
	 */
	public static String replaceVariables(IScope scope, String command) {
		if (logger.isTraceEnabled()) {
			logger.trace("before: " + command);
		}
		StringBuilder sb = new StringBuilder();
		int l = 0;
		int p = 0;
		int k = 0;
		for (int i = 0; i < command.length(); i++) {
			char b = command.charAt(i);
			if (k == 0) {
				if (b == '@') {
					k = 1;
					p = i;
				}
			} else if (k == 1) {
				if ((b >= 'A' && b <= 'Z') || (b >= 'a' && b <= 'z')) {
					k = 2;
				} else if (b == '@') {
					sb.append(command, l, i);
					k = 0;
					l = i + 1;
				} else {
					k = 0;
				}
			} else if (k == 2) {
				if (!((b >= 'A' && b <= 'Z') || (b >= 'a' && b <= 'z')
						|| (b >= '0' && b <= '9') || b == '_')) {
					if (p > l)
						sb.append(command, l, p);
					String var = command.substring(p + 1, i);
					Object value = scope.getVariable(var);
					if (value != null)
						sb.append(value);
					k = 0;
					l = i;
				}
			}
		}
		if (k == 2) {
			if (p > l)
				sb.append(command, l, p);
			String var = command.substring(p + 1);
			Object value = scope.getVariable(var);
			if (value != null)
				sb.append(value);
		} else {
			if (command.length() > l)
				sb.append(command, l, command.length());
		}
		if (logger.isTraceEnabled()) {
			logger.trace("after: " + sb.toString());
		}
		return sb.toString();
	}

	/**
	 * 替换脚本中的参数。
	 * 
	 * @param script
	 *            脚本
	 * @param args
	 *            参数
	 * @return 替换后的脚本
	 */
	public static String replaceArgs(String script, String[] args) {
		if (logger.isTraceEnabled()) {
			logger.trace("before: " + script);
		}
		StringBuilder sb = new StringBuilder();
		int l = 0;
		int p = 0;
		int k = 0;
		for (int i = 0; i < script.length(); i++) {
			char b = script.charAt(i);
			if (k == 0) {
				if (b == '%') {
					k = 1;
					p = i;
				}
			} else if (k == 1) {
				if (b >= '0' && b <= '9') {
					k = 2;
				} else if (b == '*') {
					if (p > l)
						sb.append(script, l, p);
					for (int j = 1; j < args.length; j++) {
						if (j > 1)
							sb.append(' ');
						sb.append(args[j]);
					}
					k = 0;
					l = i + 1;
				} else if (b == '%') {
					sb.append(script, l, i);
					k = 0;
					l = i + 1;
				} else {
					k = 0;
				}
			} else if (k == 2) {
				if (b < '0' || b > '9') {
					if (p > l)
						sb.append(script, l, p);
					int j = Integer.parseInt(script.substring(p + 1, i));
					if (j < args.length)
						sb.append(args[j]);
					k = 0;
					l = i;
				}
			}
		}
		if (k == 2) {
			if (p > l)
				sb.append(script, l, p);
			int j = Integer.parseInt(script.substring(p + 1));
			if (j < args.length)
				sb.append(args[j]);
		} else {
			if (script.length() > l)
				sb.append(script, l, script.length());
		}
		if (logger.isTraceEnabled()) {
			logger.trace("after: " + sb.toString());
		}
		return sb.toString();
	}

	/**
	 * 解析脚本为指令列表。
	 * 
	 * @param script
	 *            脚本
	 * @return 指令列表，至少会有一项
	 */
	private static List<Command> parse(String script) {
		List<String> cmds = split(script, ';');
		List<Command> list = new ArrayList<Command>(cmds.size());
		for (String command : cmds) {
			list.add(parseCmd(command));
		}
		return list;
	}

	private static Command parseCmd(String command) {
		Command cmd = null;
		List<String> list = split(command, ' ');
		String s = list.get(0);
		if (!s.isEmpty() && s.charAt(0) == '#') {
			Class<? extends Command> clz = TYPES.get(s);
			if (clz == null) {
				if (logger.isWarnEnabled()) {
					logger.warn("unknow command type: " + s);
				}
			} else {
				try {
					cmd = clz.newInstance();
					cmd.args = new String[list.size() - 1];
					for (int i = 0; i < cmd.args.length; i++)
						cmd.args[i] = list.get(i + 1);
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("error on new instance: " + clz.getName(),
								e);
					}
				}
			}
		}
		if (cmd == null) {
			cmd = new DefaultCommand();
			cmd.args = new String[list.size()];
			for (int i = 0; i < cmd.args.length; i++)
				cmd.args[i] = list.get(i);
		}
		cmd.origin = command;
		if (logger.isTraceEnabled()) {
			logger.trace(cmd);
		}
		return cmd;
	}

	private static List<String> split(String command, char ch) {
		if (command.length() > 1 && command.charAt(0) == '{'
				&& command.charAt(command.length() - 1) == '}')
			command = command.substring(1, command.length() - 1);
		List<String> cmds = new ArrayList<String>();
		int p = 0;
		int k = 0;
		for (int i = 0; i < command.length(); i++) {
			char b = command.charAt(i);
			if (k == 0 && b == ch) {
				if (i > p) {
					String cmd = command.substring(p, i).trim();
					if (!cmd.isEmpty())
						cmds.add(cmd);
				}
				p = i + 1;
			} else if (b == '{') {
				k++;
			} else if (b == '}') {
				if (k > 0)
					k--;
			}
		}
		if (p < command.length()) {
			String cmd = command.substring(p).trim();
			if (cmd.length() > 1 && cmd.charAt(0) == '{'
					&& cmd.charAt(cmd.length() - 1) == '}')
				cmd = cmd.substring(1, cmd.length() - 1);
			if (!cmd.isEmpty())
				cmds.add(cmd);
		}
		if (cmds.isEmpty())
			cmds.add("");
		return cmds;
	}
}
