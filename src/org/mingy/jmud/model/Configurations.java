package org.mingy.jmud.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 配置的管理类。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public abstract class Configurations {

	/** 所有注册的配置 */
	private static final Map<String, Configuration> ALL;

	static {
		ALL = new HashMap<String, Configuration>();
		// TODO: 配置管理
		register(new org.mingy.jmud.model.pkuxkx.Configuration());
		register(new org.mingy.jmud.model.pkuxkx.TestConfiguration());
	}

	/**
	 * 注册一个配置。
	 * 
	 * @param configuration
	 *            配置
	 */
	public static void register(Configuration configuration) {
		String name = configuration.getName();
		if (name == null)
			throw new IllegalArgumentException("configuration name is null");
		if (ALL.containsKey(name))
			throw new IllegalArgumentException("duplicated name: " + name);
		ALL.put(name, configuration);
	}

	/**
	 * 注册一个配置。
	 * 
	 * @param clazz
	 *            配置类
	 */
	public static void register(Class<? extends Configuration> clazz) {
		try {
			Configuration configuration = clazz.newInstance();
			register(configuration);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 返回指定名称的配置。
	 * 
	 * @param name
	 *            配置名
	 * @return 配置
	 */
	public static Configuration get(String name) {
		return ALL.get(name);
	}

	/**
	 * 返回所有已注册的配置名。
	 * 
	 * @return 配置名的集合
	 */
	public static Set<String> names() {
		return ALL.keySet();
	}
}
