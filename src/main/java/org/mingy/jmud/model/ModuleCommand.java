package org.mingy.jmud.model;

/**
 * 模块控制指令。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class ModuleCommand extends Command {

	@Override
	protected boolean execute(IScope scope, String header, String[] items,
			String[] args) throws Exception {
		if (items.length != 1)
			return false;
		IScope target = scope.getScope(items[0]);
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
