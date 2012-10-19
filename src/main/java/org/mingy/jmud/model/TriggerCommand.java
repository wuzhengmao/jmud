package org.mingy.jmud.model;

/**
 * 触发器控制指令。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class TriggerCommand extends Command {

	@Override
	public boolean execute(IScope scope) {
		if (args.length > 1)
			return false;
		Object[] r = getScopeByPath(scope, args.length > 0 ? args[0] : "");
		if (r == null)
			return false;
		IScope target = (IScope) r[0];
		String group = (String) r[1];
		if ("#t+".equals(header)) {
			return target.setTriggerEnabled(group, true);
		} else if ("#t-".equals(header)) {
			return target.setTriggerEnabled(group, false);
		}
		return false;
	}
}
