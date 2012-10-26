package org.mingy.jmud.client;

/**
 * {@link TelnetClient}的监听器。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public interface ITelnetClientListener {

	/**
	 * 与主机建立连接后回调。
	 */
	void onConnected();

	/**
	 * 与主机断开连接时回调。
	 */
	void onDisconnected();

	/**
	 * 从主机接收到数据。
	 * 
	 * @param data
	 *            数据
	 */
	void onReceived(byte[] data);

	/**
	 * 请求客户端鸣叫。
	 */
	void beep();

	/**
	 * 要求客户端显示接收到的数据。
	 */
	void echoOn();

	/**
	 * 禁止客户端显示接收到的数据。
	 */
	void echoOff();
}
