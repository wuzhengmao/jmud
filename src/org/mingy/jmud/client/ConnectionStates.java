package org.mingy.jmud.client;

/**
 * 连接状态。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public enum ConnectionStates {

	/** 连接中状态 */
	CONNECTING,
	/** 已连接状态 */
	CONNECTED,
	/** 已断线状态 */
	DISCONNECTED;
}
