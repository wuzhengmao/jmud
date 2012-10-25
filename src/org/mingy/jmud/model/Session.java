package org.mingy.jmud.model;

import java.nio.charset.Charset;

import org.eclipse.swt.graphics.Font;

/**
 * 会话连接信息。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Session {

	private String host;
	private int port = 23;
	private int timeout = 10;
	private Charset charset = Charset.defaultCharset();
	private Font font;
	private String character;
	private String password;
	private Configuration configuration;

	/**
	 * 返回主机名或IP。
	 * 
	 * @return 主机名或IP
	 */
	public String getHost() {
		return host;
	}

	/**
	 * 设置主机名或IP。
	 * 
	 * @param host
	 *            主机名或IP
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * 返回端口。
	 * 
	 * @return 端口
	 */
	public int getPort() {
		return port;
	}

	/**
	 * 设置端口。
	 * 
	 * @param port
	 *            端口
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * 返回连接超时。
	 * 
	 * @return 连接超时（秒）
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * 设置连接超时。
	 * 
	 * @param timeout
	 *            连接超时（秒）
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * 返回字符集。
	 * 
	 * @return 字符集
	 */
	public Charset getCharset() {
		return charset;
	}

	/**
	 * 设置字符集。
	 * 
	 * @param charset
	 *            字符集
	 */
	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	/**
	 * 返回字体。
	 * 
	 * @return 字体
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * 设置字体。
	 * 
	 * @param font
	 *            字体
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * 返回角色名。
	 * 
	 * @return 角色名
	 */
	public String getCharacter() {
		return character;
	}

	/**
	 * 设置角色名。
	 * 
	 * @param character
	 *            角色名
	 */
	public void setCharacter(String character) {
		this.character = character;
	}

	/**
	 * 返回密码。
	 * 
	 * @return 密码
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * 设置密码。
	 * 
	 * @param password
	 *            密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 返回配置。
	 * 
	 * @return 配置
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * 设置配置。
	 * 
	 * @param configuration
	 *            配置
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}
