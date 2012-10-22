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
		setAlias("lc", "l corpse");
		setAlias(
				"while",
				"#set x 1;#while true {hi @x;#set x {x+1};#if {x > 10} {#break;};#wa 1000;};over;");
		addTrigger(null, "^您的英文名字（要注册新人物请输入new）：", "kscs");
		addTrigger(null, "此ID档案已存在，请输入密码：", "zxc123");
		setTimer("ok", "hi @i;#wa 1500;l @i");
		Scope module = addChild("m");
		module.addTrigger(
				null,
				"^【 精神 】\\s*(\\d+)\\s*/\\s*(\\d+)\\s*\\[\\s*(\\d+)%\\]\\s*【 精力 】\\s*(\\d+)\\s*/\\s*(\\d+)\\s*\\(\\+\\s*(\\d+)\\)$",
				"#set i {i + ' ' + %1};#if {i.length > 15} {#return;};#alias test {ok %%*; hi %%*};test @i");

	}
}
