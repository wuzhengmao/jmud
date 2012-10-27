package org.mingy.jmud.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mingy.jmud.util.Variables;

/**
 * 指令的定义和处理。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public abstract class Commands {

	/** 日志 */
	private static final Log logger = LogFactory.getLog(Commands.class);

	/** 指令类型表 */
	private static final Map<String, Class<? extends Command>> TYPES;

	static {
		TYPES = new HashMap<String, Class<? extends Command>>();
		TYPES.put("#m+", ModuleCommand.class);
		TYPES.put("#m-", ModuleCommand.class);
		TYPES.put("#set", SetCommand.class);
		TYPES.put("#var", VarCommand.class);
		TYPES.put("#al", AliasCommand.class);
		TYPES.put("#alias", AliasCommand.class);
		TYPES.put("#t+", TriggerCommand.class);
		TYPES.put("#t-", TriggerCommand.class);
		TYPES.put("#ti", TimerCommand.class);
		TYPES.put("#timer", TimerCommand.class);
		TYPES.put("#ts", TimerCommand.class);
		TYPES.put("#tset", TimerCommand.class);
		TYPES.put("#wa", WaitCommand.class);
		TYPES.put("#wait", WaitCommand.class);
		TYPES.put("#return", ReturnCommand.class);
		TYPES.put("#if", IfCommand.class);
		TYPES.put("#while", WhileCommand.class);
		TYPES.put("#until", UntilCommand.class);
		TYPES.put("#break", BreakCommand.class);
		TYPES.put("#skip", SkipCommand.class);
		TYPES.put("#reconnect", ReconnectCommand.class);
		TYPES.put("#disconnect", DisconnectCommand.class);
		TYPES.put("#sh", ShowCommand.class);
		TYPES.put("#show", ShowCommand.class);
		TYPES.put("#echo", EchoCommand.class);
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
	public static String replaceCommand(IScope scope, String command) {
		return replaceVariables(scope, command, false);
	}

	/**
	 * 替换表达式中的变量。
	 * 
	 * @param scope
	 *            上下文
	 * @param expression
	 *            表达式
	 * @return 替换后的表达式
	 */
	public static String replaceExpression(IScope scope, String expression) {
		return replaceVariables(scope, expression, true);
	}

	private static String replaceVariables(IScope scope, String command,
			boolean isExpression) {
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
					if (isExpression)
						sb.append(Variables.toJSvar(value));
					else
						sb.append(Variables.toString(value));
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
	public static List<Command> parse(String script) {
		List<String> cmds = split(script);
		List<Command> list = new ArrayList<Command>(cmds.size());
		for (String command : cmds) {
			list.add(parseCmd(command));
		}
		return list;
	}

	private static Command parseCmd(String command) {
		Command cmd = null;
		List<int[]> results = preSplit(command, ' ');
		int[] r = results.get(0);
		command = command.substring(r[0], r[1]);
		r = results.get(1);
		String s = command.substring(r[0], r[1]);
		if (!s.isEmpty() && s.charAt(0) == '#') {
			Class<? extends Command> clz = TYPES.get(s);
			if (clz == null) {
				if (logger.isWarnEnabled()) {
					logger.warn("unknow command type: " + s);
				}
			} else {
				try {
					cmd = clz.newInstance();
					cmd.splits = new int[results.size() - 1][];
					for (int i = 0; i < cmd.splits.length; i++)
						cmd.splits[i] = results.get(i + 1);
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
			cmd.splits = new int[results.size()][];
			cmd.splits[0] = new int[] { 0, command.length() };
			for (int i = 1; i < cmd.splits.length; i++)
				cmd.splits[i] = results.get(i);
		}
		cmd.origin = command;
		if (logger.isTraceEnabled()) {
			logger.trace(cmd);
		}
		return cmd;
	}

	private static List<String> split(String command) {
		List<int[]> results = preSplit(command, ';');
		List<String> cmds = new ArrayList<String>(results.size() - 1);
		for (int i = 0; i < results.size(); i++) {
			int[] r = results.get(i);
			if (i > 0)
				cmds.add(command.substring(r[0], r[1]));
			else
				command = command.substring(r[0], r[1]);
		}
		return cmds;
	}

	private static final Pattern PATTERN = Pattern
			.compile("^\\s*\\{\\s*(.*)\\s*\\}\\s*$");

	/**
	 * 预先将指令进行分割。
	 * 
	 * @param command
	 *            指令
	 * @param ch
	 *            分隔字符
	 * @return 返回列表的的第一个数组为输入指令的有效起始和结束位置，之后顺序为每个分隔段相对于输入指令有效位置的起始和结束位置，列表至少含两项
	 */
	public static List<int[]> preSplit(String command, char ch) {
		List<int[]> results = new ArrayList<int[]>();
		int[] all = match(command, 0, command.length());
		results.add(all);
		command = command.substring(all[0], all[1]);
		int p = 0;
		int k = 0;
		for (int i = 0; i < command.length(); i++) {
			char b = command.charAt(i);
			if (k == 0 && b == ch) {
				if (i > p) {
					int[] r = match(command, p, i);
					if (r[1] > r[0])
						results.add(r);
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
			int[] r = match(command, p, command.length());
			if (r[1] > r[0])
				results.add(r);
		}
		if (results.size() == 1)
			results.add(new int[] { 0, command.length() });
		return results;
	}

	private static int[] match(String command, int start, int end) {
		int[] all = new int[2];
		Matcher m = PATTERN.matcher(command.substring(start, end));
		if (m.find()) {
			all[0] = m.start(1) + start;
			all[1] = m.end(1) + start;
		} else {
			all[0] = start;
			all[1] = end;
		}
		return all;
	}
}
