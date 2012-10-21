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
	protected boolean execute(IScope scope, String header, String[] items,
			String[] args) throws Exception {
		Alias alias = scope.getAlias(items[0]);
		if (alias != null) {
			IExecution execution = alias.getExecution();
			if (execution != null)
				execution.execute(scope, items);
			return true;
		}
		if (args != null && args.length > 0)
			header = Commands.replaceArgs(header, args);
		scope.sendCommand(Commands.replaceCommand(scope, header));
		return true;
	}
}
