package org.mingy.jmud.model;

import org.mingy.jmud.client.SGR;

/**
 * 显示信息的指令，该信息可能会激活触发器。
 * <p>
 * Usage: #show [style] message
 * </p>
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class ShowCommand extends Command {

	@Override
	protected boolean execute(IScope scope, String header, String[] items,
			String[] args) throws Exception {
		if (items.length < 1 || items.length > 2)
			return false;
		if (items.length == 1) {
			scope.showText(escapeText(scope, items[0]), null);
		} else {
			String style = escapeStyle(scope, items[0]);
			scope.showText(escapeText(scope, items[1]), style);
		}
		return true;
	}

	static String escapeText(IScope scope, String text) {
		return Commands.replaceCommand(scope, text).replace("\\n", "\n");
	}

	static String escapeStyle(IScope scope, String style) {
		style = Commands.replaceCommand(scope, style);
		if (style.charAt(0) != 27)
			style = SGR.ESC + style;
		return style;
	}
}
