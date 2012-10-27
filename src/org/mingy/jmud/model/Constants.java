package org.mingy.jmud.model;

/**
 * 常量定义。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public abstract class Constants {

	/** 登录模块 */
	public static final String MODULE_LOGIN = "_LOGIN_";

	/** 角色变量 */
	public static final String VAR_CHARACTER = "character";

	/** 密码变量 */
	public static final String VAR_PASSWORD = "password";

	/** 连接状态变量 */
	public static final String VAR_CONNECTION_STATE = "state";

	/** 连接建立后调用的脚本 */
	public static final String ALIAS_ON_CONNECTED = "onConnected";

	/** 连接断开后调用的脚本 */
	public static final String ALIAS_ON_DISCONNECTED = "onDisconected";
}
