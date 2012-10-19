package org.mingy.jmud.model;

/**
 * 对别名进行增、删、改的指令。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class AliasCommand extends Command {

	@Override
	public boolean execute(IScope scope) {
		if (args.length == 0 || args.length > 2)
			return false;
		Object[] r = getScopeByPath(scope, args[0]);
		if (r == null)
			return false;
		IScope target = (IScope) r[0];
		String name = (String) r[1];
		if (args.length == 1) {
			target.removeAlias(name);
		} else {
			target.setAlias(name, args[1]);
		}
		return true;
	}
}
