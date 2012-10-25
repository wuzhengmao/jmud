package org.mingy.jmud.model.pkuxkx;

import org.mingy.jmud.model.Context;
import org.mingy.jmud.model.Scope;

public class TestConfiguration extends Configuration {

	@Override
	public String getName() {
		return "test@pkuxkx.net";
	}

	@Override
	public void inject(Context context) {
		super.inject(context);
		context.setVariable("i", "hello");
		context.setAlias("lc", "l corpse");
		context.setAlias(
				"while",
				"#set x 1;#while true {hi @x;#set x {x+1};#if {x > 10} {#break;};#wa 1000;};over;");
		context.setTimer("ok", "hi @i;#wa 1500;l @i");
		Scope module = context.addChild("m");
		module.addTrigger(
				null,
				"^【 精神 】\\s*(\\d+)\\s*/\\s*(\\d+)\\s*\\[\\s*(\\d+)%\\]\\s*【 精力 】\\s*(\\d+)\\s*/\\s*(\\d+)\\s*\\(\\+\\s*(\\d+)\\)$",
				"#set i {i + ' ' + %1};#if {i.length > 15} {#return;};#alias test {ok %%*; hi %%*};test @i");
	}
}
