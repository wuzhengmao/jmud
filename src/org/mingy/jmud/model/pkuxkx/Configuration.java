package org.mingy.jmud.model.pkuxkx;

import org.eclipse.swt.SWT;
import org.mingy.jmud.model.Context;
import org.mingy.jmud.model.Constants;
import org.mingy.jmud.model.Scope;

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
		Scope loginModule = context.getScope(Constants.MODULE_LOGIN);
		loginModule.addTrigger(null, "^您的英文名字（要注册新人物请输入new）：", "@character");
		loginModule.addTrigger(null, "此ID档案已存在，请输入密码：", "@password");
	}
}
