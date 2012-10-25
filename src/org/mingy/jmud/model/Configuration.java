package org.mingy.jmud.model;

/**
 * 配置。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public abstract class Configuration {

	/**
	 * 返回配置名。
	 * 
	 * @return 配置名
	 */
	public abstract String getName();

	/**
	 * 将配置注入到上下文中。
	 * 
	 * @param context
	 *            上下文
	 */
	public abstract void inject(Context context);
}
