package org.mingy.jmud.model;

/**
 * 模块控制指令。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class ModuleCommand extends Command {

	@Override
	public boolean execute(IScope scope) throws Exception {
		if (args.length != 1)
			return false;
		IScope target = scope.getScope(args[0]);
		if (target == null)
			return false;
		if ("#m+".equals(header)) {
			target.setEnabled(true);
			return true;
		} else if ("#m-".equals(header)) {
			target.setEnabled(false);
			return true;
		}
		return false;
	}
}
