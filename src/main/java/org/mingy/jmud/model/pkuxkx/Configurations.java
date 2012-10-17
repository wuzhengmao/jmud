package org.mingy.jmud.model.pkuxkx;

import org.eclipse.swt.SWT;
import org.mingy.jmud.client.IMudClient;

/**
 * 北大侠客行的配置定义。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Configurations extends org.mingy.jmud.model.Configurations {

	@Override
	public void init(IMudClient client) {
		super.init(client);
		SHORT_KEYS.add(SWT.KEYPAD_0, "hp");
		TRIGGERS.add(
				"^【 精神 】\\s*(\\d+)\\s*/\\s*(\\d+)\\s*\\[\\s*(\\d+)%\\]\\s*【 精力 】\\s*(\\d+)\\s*/\\s*(\\d+)\\s*\\(\\+\\s*(\\d+)\\)$",
				"ok");
	}
}
