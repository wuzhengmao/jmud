package org.mingy.jmud.client;

import java.util.EventListener;

/**
 * 连接状态的监听器。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public interface IConnectionStateListener extends EventListener {

	/**
	 * 连接状态发生变化。
	 * 
	 * @param event
	 *            事件对象
	 */
	void onStateChanged(ConnectionEvent event);
}
