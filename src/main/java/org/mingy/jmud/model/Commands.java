package org.mingy.jmud.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	}

	/**
	 * 执行脚本
	 * 
	 * @param context
	 *            上下文
	 * @param script
	 *            脚本
	 * @param args
	 *            参数
	 */
	public static void execute(Context context, String script, String[] args) {
		for (Command cmd : parse(script)) {
			cmd.execute(context, args);
		}
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
		Command cmd;
		if (command.length() > 2 && command.charAt(0) == '{'
				&& command.charAt(command.length() - 1) == '}')
			command = command.substring(1, command.length() - 1);
		if (command.isEmpty() || command.charAt(0) != '#') {
			cmd = new DefaultCommand();
			cmd.args = new String[1];
			cmd.args[0] = command;
		} else {
			List<String> list = split(command, ' ');
			Class<? extends Command> clz = TYPES.get(list.get(0));
			if (clz == null) {
				if (logger.isWarnEnabled()) {
					logger.warn("unknow command type: " + list.get(0));
				}
				cmd = new DefaultCommand();
				cmd.args = new String[1];
				cmd.args[0] = command;
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
					cmd = new DefaultCommand();
					cmd.args = new String[1];
					cmd.args[0] = command;
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace(cmd);
		}
		return cmd;
	}

	private static List<String> split(String command, char ch) {
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
			if (!cmd.isEmpty())
				cmds.add(cmd);
		}
		if (cmds.isEmpty())
			cmds.add("");
		return cmds;
	}
}
