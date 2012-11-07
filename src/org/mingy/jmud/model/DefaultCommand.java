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
			Execution execution = alias.getExecution();
			if (execution != null)
				execution.execute(scope, items);
			return true;
		}
		header = Commands.replaceArgs(header, args);
		scope.sendCommand(Commands.replaceCommand(scope, header), true);
		return true;
	}
}
