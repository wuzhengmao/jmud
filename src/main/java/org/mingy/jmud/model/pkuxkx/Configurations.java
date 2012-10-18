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
		ALIASES.add("test", "say %*");
		JS.put("i", "hello");
		TRIGGERS.add("^您的英文名字（要注册新人物请输入new）：", "kscs");
		TRIGGERS.add("此ID档案已存在，请输入密码：", "zxc123");
		TRIGGERS.add(
				"^【 精神 】\\s*(\\d+)\\s*/\\s*(\\d+)\\s*\\[\\s*(\\d+)%\\]\\s*【 精力 】\\s*(\\d+)\\s*/\\s*(\\d+)\\s*\\(\\+\\s*(\\d+)\\)$",
				"#set i @i+'-'+%1;test @i");

	}
}
