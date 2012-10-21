package org.mingy.jmud.model;

/**
 * 触发器控制指令。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class TriggerCommand extends Command {

	@Override
	protected boolean execute(IScope scope, String header, String[] items,
			String[] args) throws Exception {
		if (items.length > 1)
			return false;
		Object[] r = getScopeByPath(scope, items.length > 0 ? items[0] : "");
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
