package org.mingy.jmud.model;

import org.mingy.jmud.client.IMudClient;

/**
 * 配置定义。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Configurations {

	/** MUD客户端 */
	private IMudClient client;

	/** 快捷键定义 */
	public ShortKeys SHORT_KEYS;

	/** 触发器定义 */
	public TriggerHandler TRIGGERS;

	/**
	 * 初始化
	 * 
	 * @param client
	 *            MUD客户端
	 */
	public void init(IMudClient client) {
		this.client = client;
		SHORT_KEYS = new ShortKeys();
		TRIGGERS = new TriggerHandler(client);
	}
}
