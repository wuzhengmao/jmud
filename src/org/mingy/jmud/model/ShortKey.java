package org.mingy.jmud.model;

/**
 * 快捷键。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class ShortKey {

	/** 组合键 */
	private int key;
	/** 指令 */
	private String command;

	/**
	 * 构造器。
	 * 
	 * @param key
	 *            组合键，按键与CTRL、ALT、SHIFT、COMMAND的组合
	 * @param command
	 *            命令行
	 */
	ShortKey(int key, String command) {
		this.key = key;
		this.command = command;
	}

	/**
	 * 返回组合键。
	 * 
	 * @return 组合键
	 */
	public int getKey() {
		return key;
	}

	/**
	 * 返回按键的代码。
	 * 
	 * @return 按键的代码
	 */
	public int getKeyCode() {
		return key & 0xF0FF;
	}

	/**
	 * 返回状态键的掩码。
	 * 
	 * @return 状态键的掩码
	 */
	public int getStateMask() {
		return key & 0x0F00;
	}

	/**
	 * 返回指令。
	 * 
	 * @return 指令
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * 设置指令。
	 * 
	 * @param command
	 *            指令
	 */
	void setCommand(String command) {
		this.command = command;
	}
}
