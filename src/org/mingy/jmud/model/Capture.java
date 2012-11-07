package org.mingy.jmud.model;

/**
 * 捕捉并隐藏一段文本。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Capture extends Execution {

	@Override
	public boolean shallForkThread() {
		return false;
	}

	@Override
	public void execute(IScope scope, String[] args)
			throws InterruptedException {
		scope.hideText(args[0]);
	}
}
