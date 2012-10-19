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
	public boolean execute(IScope scope) {
		Alias alias = scope.getAlias(args[0]);
		if (alias != null) {
			Commands.execute(scope, alias.getScript(), args);
			return true;
		}
		scope.sendCommand(Commands.replaceVariables(scope, origin));
		return true;
	}
}
