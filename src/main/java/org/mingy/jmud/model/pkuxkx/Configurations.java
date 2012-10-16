package org.mingy.jmud.model.pkuxkx;

import org.eclipse.swt.SWT;

/**
 * 北大侠客行的配置定义。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Configurations extends org.mingy.jmud.model.Configurations {

	/**
	 * 构造器。
	 */
	public Configurations() {
		super();
		SHORT_KEYS.add(SWT.KEYPAD_0, "hp");
		SHORT_KEYS.add(SWT.KEYPAD_5, "look");
	}
}
