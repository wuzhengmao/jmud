package org.mingy.jmud.model;

import junit.framework.Assert;

import org.junit.Test;

public class CommandsTest {

	@Test
	public void testReplaceVariables() throws Exception {
		Context context = new Context();
		context.init(null);
		context.JS.put("abC", "Test");
		context.JS.put("b1", "Hello");
		String command = "@@@abC我们 @b1  @b2 @@b3 @b1";
		String result = Commands.replaceVariables(context, command);
		Assert.assertEquals("@Test我们 Hello   @b3 Hello", result);
	}

	@Test
	public void testReplaceArgs() throws Exception {
		String script = "#alias test {f=%1};%%2 %2; %3%t ok";
		String result = Commands.replaceArgs(script, new String[] { "unused",
				"abc", "100" });
		Assert.assertEquals("#alias test {f=abc};%2 100; %t ok", result);
	}
}
