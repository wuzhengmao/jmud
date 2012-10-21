package org.mingy.jmud.model;

/**
 * 对别名进行增、删、改的指令。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class AliasCommand extends Command {

	@Override
	protected boolean execute(IScope scope, String header, String[] items,
			String[] args) throws Exception {
		if (items.length == 0 || items.length > 2)
			return false;
		Object[] r = getScopeByPath(scope, items[0]);
		if (r == null)
			return false;
		IScope target = (IScope) r[0];
		String name = (String) r[1];
		if (items.length == 1) {
			target.removeAlias(name);
		} else {
			target.setAlias(name, items[1]);
		}
		return true;
	}
}
