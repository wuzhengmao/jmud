package org.mingy.jmud.model.pkuxkx;

import org.eclipse.swt.SWT;
import org.mingy.jmud.client.IMudClient;
import org.mingy.jmud.model.Context;

/**
 * 北大侠客行的配置定义。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Configurations extends Context {

	@Override
	public void init(IMudClient client) {
		super.init(client);
		SHORT_KEYS.add(SWT.KEYPAD_0, "hp");
		JS.put("qi", 20);
		TRIGGERS.add("^您的英文名字（要注册新人物请输入new）：", "client.send('kscs');");
		TRIGGERS.add("此ID档案已存在，请输入密码：", "client.send('zxc123');");
		TRIGGERS.add(
				"^【 精神 】\\s*(\\d+)\\s*/\\s*(\\d+)\\s*\\[\\s*(\\d+)%\\]\\s*【 精力 】\\s*(\\d+)\\s*/\\s*(\\d+)\\s*\\(\\+\\s*(\\d+)\\)$",
				"qi=100;client.send('say '+args[1]);");

	}
}
