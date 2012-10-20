package org.mingy.jmud.util;

import org.junit.Assert;
import org.junit.Test;

public class VariablesTest {

	@Test
	public void testToDouble() {
		Assert.assertNull(Variables.toDouble(null));
		Assert.assertNull(Variables.toDouble("20s"));
		Assert.assertEquals(new Double(100), Variables.toDouble(100.0d));
		Assert.assertEquals(new Double(100), Variables.toDouble(100.0f));
		Assert.assertEquals(new Double(100), Variables.toDouble(100l));
		Assert.assertEquals(new Double(100), Variables.toDouble(100));
		Assert.assertEquals(new Double(100), Variables.toDouble("100"));
	}

	@Test
	public void testToFloat() {
		Assert.assertNull(Variables.toFloat(null));
		Assert.assertNull(Variables.toFloat("20s"));
		Assert.assertEquals(new Float(100), Variables.toFloat(100.0d));
		Assert.assertEquals(new Float(100), Variables.toFloat(100.0f));
		Assert.assertEquals(new Float(100), Variables.toFloat(100l));
		Assert.assertEquals(new Float(100), Variables.toFloat(100));
		Assert.assertEquals(new Float(100), Variables.toFloat("100"));
	}

	@Test
	public void testToLong() {
		Assert.assertNull(Variables.toLong(null));
		Assert.assertNull(Variables.toLong("20s"));
		Assert.assertEquals(new Long(100), Variables.toLong(100.0d));
		Assert.assertEquals(new Long(100), Variables.toLong(100.1f));
		Assert.assertEquals(new Long(100), Variables.toLong(100l));
		Assert.assertEquals(new Long(100), Variables.toLong(100));
		Assert.assertEquals(new Long(100), Variables.toLong("100"));
	}

	@Test
	public void testToInteger() {
		Assert.assertNull(Variables.toInt(null));
		Assert.assertNull(Variables.toInt("20s"));
		Assert.assertEquals(new Integer(100), Variables.toInt(100.0d));
		Assert.assertEquals(new Integer(100), Variables.toInt(100.1f));
		Assert.assertEquals(new Integer(100), Variables.toInt(100l));
		Assert.assertEquals(new Integer(100), Variables.toInt(100));
		Assert.assertEquals(new Integer(100), Variables.toInt("100"));
	}

	@Test
	public void testToJSvar() {
		Assert.assertEquals("null", Variables.toJSvar(null));
		Assert.assertEquals("100.0", Variables.toJSvar(100d));
		Assert.assertEquals("100.0", Variables.toJSvar(100f));
		Assert.assertEquals("100", Variables.toJSvar(100l));
		Assert.assertEquals("100", Variables.toJSvar(100));
		Assert.assertEquals("'abc'", Variables.toJSvar("abc"));
		Assert.assertEquals("'a\\'bc'", Variables.toJSvar("a'bc"));
		Assert.assertEquals("'a\\\\bc'", Variables.toJSvar("a\\bc"));
	}
}
