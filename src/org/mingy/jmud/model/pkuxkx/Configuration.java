package org.mingy.jmud.model.pkuxkx;

import org.eclipse.swt.SWT;
import org.mingy.jmud.model.Context;

/**
 * 北大侠客行的通用配置定义。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Configuration extends org.mingy.jmud.model.Configuration {

	@Override
	public String getName() {
		return "pkuxkx.net";
	}

	@Override
	public void inject(Context context) {
		context.setShortKey(SWT.KEYPAD_0, "hp");
		context.addTrigger("login", "^您的英文名字（要注册新人物请输入new）：", "@character");
		context.addTrigger("login", "此ID档案已存在，请输入密码：", "@password;#t- login");
	}
}
