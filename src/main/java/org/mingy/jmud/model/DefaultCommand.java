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
	public boolean execute(IScope scope) throws Exception {
		Alias alias = scope.getAlias(args[0]);
		if (alias != null) {
			scope.executeScript(alias.getScript(), args);
			return true;
		}
		scope.sendCommand(scope.replaceCommand(origin));
		return true;
	}
}
