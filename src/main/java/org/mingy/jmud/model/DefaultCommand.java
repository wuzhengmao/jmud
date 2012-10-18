package org.mingy.jmud.model;

/**
 * 默认的指令。
 * <p>
 * <li>ALIAS指令
 * <li>发送到MUD客户端
 * </p>
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class DefaultCommand extends Command {

	@Override
	public boolean execute(Context context) {
		Alias alias = context.ALIASES.get(args[0]);
		if (alias != null) {
			Commands.execute(context, alias.getScript(), args);
			return true;
		}
		context.CLIENT.send(Commands.replaceVariables(context, origin));
		return true;
	}
}
