package org.mingy.jmud.model;

import junit.framework.Assert;

import org.junit.Test;

public class CommandsTest {

	@Test
	public void testReplaceVariables() throws Exception {
		Context context = new Context();
		context.init(null);
		context.setVariable("abC", "Test");
		context.setVariable("b1", "Hello");
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

	@Test
	public void testGetScopeByPath() throws Exception {
		Context context = new Context();
		IScope module1 = context.addChild("1");
		IScope module2 = module1.addChild("2");
		IScope module3 = module2.addChild("3");
		IScope module4 = module3.addChild("4");
		Object[] r = Command.getScopeByPath(module4, "");
		Assert.assertEquals(module4, r[0]);
		Assert.assertEquals("", r[1]);
		r = Command.getScopeByPath(module4, "test");
		Assert.assertEquals(module4, r[0]);
		Assert.assertEquals("test", r[1]);
		r = Command.getScopeByPath(module4, "/");
		Assert.assertEquals(context, r[0]);
		Assert.assertEquals("", r[1]);
		r = Command.getScopeByPath(module4, "//");
		Assert.assertEquals(context, r[0]);
		Assert.assertEquals("", r[1]);
		r = Command.getScopeByPath(module4, "/test");
		Assert.assertEquals(context, r[0]);
		Assert.assertEquals("test", r[1]);
		r = Command.getScopeByPath(module4, "//test");
		Assert.assertEquals(context, r[0]);
		Assert.assertEquals("test", r[1]);
		r = Command.getScopeByPath(module4, "/2/");
		Assert.assertEquals(module2, r[0]);
		Assert.assertEquals("", r[1]);
		r = Command.getScopeByPath(module4, "/2/test");
		Assert.assertEquals(module2, r[0]);
		Assert.assertEquals("test", r[1]);
		Assert.assertNull(Command.getScopeByPath(module4, "/2/3/test"));
		Assert.assertNull(Command.getScopeByPath(module4, "/x/test"));
		r = Command.getScopeByPath(module4, "../test");
		Assert.assertEquals(module3, r[0]);
		Assert.assertEquals("test", r[1]);
		r = Command.getScopeByPath(module4, ".//../../test");
		Assert.assertEquals(module2, r[0]);
		Assert.assertEquals("test", r[1]);
		r = Command.getScopeByPath(module4, ".//../../");
		Assert.assertEquals(module2, r[0]);
		Assert.assertEquals("", r[1]);
		Assert.assertNull(Command.getScopeByPath(module4, "../.."));
		Assert.assertNull(Command.getScopeByPath(module4, "/../test"));
		Assert.assertNull(Command.getScopeByPath(module4, "../.../test"));
	}
}
