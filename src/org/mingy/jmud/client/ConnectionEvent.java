package org.mingy.jmud.client;

import java.util.EventObject;

/**
 * 连接状态变化的事件。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class ConnectionEvent extends EventObject {

	private static final long serialVersionUID = -3074756523890070033L;

	private ConnectionStates oldState;
	private ConnectionStates newState;

	/**
	 * 构造器。
	 * 
	 * @param client
	 *            MUD客户端
	 */
	public ConnectionEvent(IMudClient client, ConnectionStates oldState,
			ConnectionStates newState) {
		super(client);
		this.oldState = oldState;
		this.newState = newState;
	}

	/**
	 * 返回MUD客户端。
	 * 
	 * @return MUD客户端
	 */
	public IMudClient getClient() {
		return (IMudClient) getSource();
	}

	/**
	 * 返回旧的状态。
	 * 
	 * @return 旧的状态
	 */
	public ConnectionStates getOldState() {
		return oldState;
	}

	/**
	 * 返回新的状态。
	 * 
	 * @return 新的状态
	 */
	public ConnectionStates getNewState() {
		return newState;
	}
}
