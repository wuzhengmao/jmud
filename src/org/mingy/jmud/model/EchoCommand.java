package org.mingy.jmud.model;

import org.mingy.jmud.client.SGR;

/**
 * 回显信息的指令，该信息不会激活触发器。
 * <p>
 * Usage: #echo message
 * </p>
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class EchoCommand extends Command {

	@Override
	protected boolean execute(IScope scope, String header, String[] items,
			String[] args) throws Exception {
		if (items.length != 1)
			return false;
		scope.echoText(ShowCommand.escapeText(scope, items[0]), SGR.INFO);
		return true;
	}
}
