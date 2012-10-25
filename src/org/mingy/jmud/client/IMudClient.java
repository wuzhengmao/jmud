package org.mingy.jmud.client;

/**
 * MUD客户端接口。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public interface IMudClient {

	/**
	 * 连接主机。
	 */
	void connect();

	/**
	 * 断开连接。
	 */
	void disconnect();

	/**
	 * 断开连接并关闭客户端。
	 */
	void close();

	/**
	 * 返回客户端是否已连接。
	 * 
	 * @return true为已连接
	 */
	boolean isConnected();

	/**
	 * 以指定样式显示一段文本。
	 * 
	 * @param text
	 *            文本
	 * @param style
	 *            SGR样式，null时使用当前样式
	 * @param continues
	 *            true是表示还有后续文本需要显示
	 */
	void show(String text, String style, boolean continues);

	/**
	 * 以指定样式回显一段文本，回显的文本不会被触发。
	 * 
	 * @param text
	 *            文本
	 * @param style
	 *            SGR样式，null时使用当前样式
	 */
	void echo(String text, String style);

	/**
	 * 发送一条指令到服务器。
	 * 
	 * @param command
	 *            指令
	 */
	void send(String command);

	/**
	 * 在UI线程中执行。
	 * 
	 * @param runnable
	 *            可执行任务
	 */
	void runOnUiThread(Runnable runnable);
}