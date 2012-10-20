package org.mingy.jmud.model.pkuxkx;

import org.eclipse.swt.SWT;
import org.mingy.jmud.client.IMudClient;
import org.mingy.jmud.model.Context;
import org.mingy.jmud.model.Scope;

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
		setShortKey(SWT.KEYPAD_0, "hp");
		setVariable("i", "hello");
		addTrigger(null, "^您的英文名字（要注册新人物请输入new）：", "kscs");
		addTrigger(null, "此ID档案已存在，请输入密码：", "zxc123");
		setTimer("ok", "hi @i");
		Scope module = addChild("m");
		module.addTrigger(
				null,
				"^【 精神 】\\s*(\\d+)\\s*/\\s*(\\d+)\\s*\\[\\s*(\\d+)%\\]\\s*【 精力 】\\s*(\\d+)\\s*/\\s*(\\d+)\\s*\\(\\+\\s*(\\d+)\\)$",
				"#set i {i + ' ' + %1};#alias test {ok %%*; hi %%*};test @i");

	}
}
