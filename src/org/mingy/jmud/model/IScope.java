package org.mingy.jmud.model;

import java.util.Collection;
import java.util.TimerTask;

/**
 * 模块化的上下文。
 * <p>
 * 每个模块可以定义内部的别名、触发器等。<br>
 * 模块内部的别名只能被本模块的触发器脚本中使用。
 * </p>
 * 
 * @author Mingy
 * @since 1.0.0
 */
public interface IScope {

	/**
	 * 返回模块名。
	 * 
	 * @return 模块名
	 */
	String getName();

	/**
	 * 返回上级模块的上下文。
	 * 
	 * @return 上级模块的上下文
	 */
	IScope getParent();

	/**
	 * 取得指定子模块的上下文。
	 * 
	 * @param name
	 *            子模块名
	 * @return 子模块的上下文
	 */
	IScope getChild(String name);

	/**
	 * 新增一个子模块的上下文。
	 * 
	 * @param name
	 *            子模块名
	 * @return 子模块的上下文
	 */
	IScope addChild(String name);

	/**
	 * 移除一个子模块的上下文。
	 * 
	 * @param name
	 *            子模块名
	 * @return 子模块的上下文
	 */
	IScope removeChild(String name);

	/**
	 * 取得上下文。
	 * 
	 * @return 上下文
	 */
	IScope getContext();

	/**
	 * 取得指定模块的上下文
	 * 
	 * @param name
	 *            模块名
	 * @return 模块的上下文
	 */
	IScope getScope(String name);

	/**
	 * 返回模块是否启用。
	 * 
	 * @return true为启用
	 */
	boolean isEnabled();

	/**
	 * 设置模块是否启用。
	 * 
	 * @param enabled
	 *            true为启用
	 */
	void setEnabled(boolean enabled);

	/**
	 * 重新连接主机。
	 */
	void reconnect();

	/**
	 * 从主机断开连接。
	 */
	void disconnect();

	/**
	 * 以指定样式显示一段文本，显示的文本会被触发。
	 * 
	 * @param text
	 *            文本
	 * @param style
	 *            SGR样式，null时使用当前样式
	 */
	void showText(String text, String style);

	/**
	 * 以指定样式回显一段文本，回显的文本不会被触发。
	 * 
	 * @param text
	 *            文本
	 * @param style
	 *            SGR样式，null时使用当前样式
	 */
	void echoText(String text, String style);

	/**
	 * 发送一条指令到服务器。
	 * 
	 * @param command
	 *            指令
	 */
	void sendCommand(String command);

	/**
	 * 在工作线程中执行。
	 * 
	 * @param runnable
	 *            可执行任务
	 */
	void runOnWorkThread(Runnable runnable);

	/**
	 * 定时执行任务。
	 * 
	 * @param task
	 *            定时任务
	 * @param period
	 *            定时周期（毫秒）
	 */
	void scheduleRun(TimerTask task, long period);

	/**
	 * 在工作线程中执行一段逻辑。
	 * 
	 * @param execution
	 *            执行逻辑
	 * @param args
	 *            参数
	 */
	void execute(IExecution execution, String[] args);

	/**
	 * 在工作线程中执行脚本。
	 * 
	 * @param script
	 *            脚本
	 * @param args
	 *            参数
	 */
	void executeScript(String script, String[] args);

	/**
	 * 计算表达式。
	 * 
	 * @param expression
	 *            表达式
	 * @return 计算后的值
	 * @throws Exception
	 */
	Object calcExpression(String expression) throws Exception;

	/**
	 * 取得变量值。
	 * 
	 * @param name
	 *            变量名
	 * @return 变量值，变量不存在时返回null
	 */
	Object getVariable(String name);

	/**
	 * 设置变量值。
	 * 
	 * @param name
	 *            变量名
	 * @param value
	 *            变量值
	 * @param local
	 *            true时为临时变量
	 * @return 原先的变量值
	 */
	Object setVariable(String name, Object value, boolean local);

	/**
	 * 移除一个变量。
	 * 
	 * @param name
	 *            变量名
	 * @param local
	 *            true时为临时变量
	 * @return 变量值，变量不存在时返回null
	 */
	Object removeVariable(String name, boolean local);

	/**
	 * 增加一个变量的监听器。
	 * 
	 * @param name
	 *            变量名
	 * @param watcher
	 *            监听器
	 */
	void addWatcher(String name, Watcher watcher);

	/**
	 * 监听变量的变化，执行指定的逻辑。
	 * 
	 * @param name
	 *            变量名
	 * @param execution
	 *            执行逻辑
	 * @return 生成的监听器
	 */
	Watcher addWatcher(String name, IExecution execution);

	/**
	 * 监听变量的变化，执行指定的逻辑。
	 * 
	 * @param name
	 *            变量名
	 * @param id
	 *            ID
	 * @param execution
	 *            执行逻辑
	 * @return 生成的监听器
	 */
	Watcher addWatcher(String name, String id, IExecution execution);

	/**
	 * 监听变量的变化，执行指定的脚本。
	 * 
	 * @param name
	 *            变量名
	 * @param script
	 *            脚本
	 * @return 生成的监听器
	 */
	Watcher addWatcher(String name, String script);

	/**
	 * 监听变量的变化，执行指定的脚本。
	 * 
	 * @param name
	 *            变量名
	 * @param id
	 *            ID
	 * @param script
	 *            脚本
	 * @return 生成的监听器
	 */
	Watcher addWatcher(String name, String id, String script);

	/**
	 * 移除一个变量的监听器。
	 * 
	 * @param name
	 *            变量名
	 * @param id
	 *            ID
	 * @return 移除的监听器
	 */
	Watcher removeWatcher(String name, String id);

	/**
	 * 取得一个定义的快捷键。
	 * 
	 * @param key
	 *            组合键
	 * @return 定义的快捷键，未定义时返回null
	 */
	ShortKey getShortKey(int key);

	/**
	 * 设置一个快捷键。
	 * 
	 * @param key
	 *            组合键
	 * @param command
	 *            命令行
	 * @return 新增或修改后的快捷键
	 */
	ShortKey setShortKey(int key, String command);

	/**
	 * 移除一个快捷键。
	 * 
	 * @param key
	 *            组合键
	 * @return 移除的快捷键，未定义时返回null
	 */
	ShortKey removeShortKey(int key);

	/**
	 * 取得一个定义的别名。
	 * 
	 * @param name
	 *            别名
	 * @return 定义的别名，未定义时返回null
	 */
	Alias getAlias(String name);

	/**
	 * 设置一个别名。
	 * 
	 * @param name
	 *            别名
	 * @param execution
	 *            执行逻辑
	 * @return 新增或修改后的别名
	 */
	Alias setAlias(String name, IExecution execution);

	/**
	 * 设置一个别名。
	 * 
	 * @param name
	 *            别名
	 * @param script
	 *            脚本
	 * @return 新增或修改后的别名
	 */
	Alias setAlias(String name, String script);

	/**
	 * 移除一个别名。
	 * 
	 * @param name
	 *            别名
	 * @return 移除的别名，未定义时返回null
	 */
	Alias removeAlias(String name);

	/**
	 * 展开一个别名对应的脚本。
	 * 
	 * @param name
	 *            别名
	 * @return 别名的脚本，未定义或非脚本类型时返回null
	 */
	String expandAlias(String name);

	/**
	 * 取得一组触发器。
	 * 
	 * @param group
	 *            组名
	 * @return 一组触发器
	 */
	Collection<Trigger> getTriggers(String group);

	/**
	 * 添加一个触发器。
	 * 
	 * @param group
	 *            组名
	 * @param trigger
	 *            触发器
	 * @return true表示添加成功，如该触发器已存在则返回false
	 */
	boolean addTrigger(String group, Trigger trigger);

	/**
	 * 添加一个简单的触发器。
	 * 
	 * @param group
	 *            组名
	 * @param regex
	 *            正则表达式
	 * @param execution
	 *            执行逻辑
	 * @return 新增的触发器
	 */
	Trigger addTrigger(String group, String regex, IExecution execution);

	/**
	 * 添加一个简单的触发器。
	 * 
	 * @param group
	 *            组名
	 * @param regex
	 *            正则表达式
	 * @param script
	 *            脚本
	 * @return 新增的触发器
	 */
	Trigger addTrigger(String group, String regex, String script);

	/**
	 * 添加一个简单的触发器。
	 * 
	 * @param group
	 *            组名
	 * @param regexes
	 *            多个正则表达式
	 * @param execution
	 *            执行逻辑
	 * @return 新增的触发器
	 */
	Trigger addTrigger(String group, String[] regexes, IExecution execution);

	/**
	 * 添加一个简单的触发器。
	 * 
	 * @param group
	 *            组名
	 * @param regexes
	 *            多个正则表达式
	 * @param script
	 *            脚本
	 * @return 新增的触发器
	 */
	Trigger addTrigger(String group, String[] regexes, String script);

	/**
	 * 移除一个触发器。
	 * 
	 * @param group
	 *            组名
	 * @param trigger
	 *            触发器
	 * @return true表示移除成功，如该触发器不存在则返回false
	 */
	boolean removeTrigger(String group, Trigger trigger);

	/**
	 * 返回触发器组是否启用。
	 * 
	 * @param group
	 *            组名
	 * @return true为已启用，如组不存在也会返回false
	 */
	boolean isTriggerEnabled(String group);

	/**
	 * 设置触发器组是否启用。
	 * 
	 * @param group
	 *            组名，"*"表示所有组，"**"表示当前及下级模块的所有组
	 * @param enabled
	 *            true为已启用
	 * @return 如组不存在时返回false
	 */
	boolean setTriggerEnabled(String group, boolean enabled);

	/**
	 * 取得一个定时器。
	 * 
	 * @param name
	 *            名称
	 * @return 定时器，未找到时返回null
	 */
	Timer getTimer(String name);

	/**
	 * 设置一个定时器。
	 * 
	 * @param name
	 *            名称
	 * @param execution
	 *            执行逻辑
	 * @return 新增或修改后的定时器
	 */
	Timer setTimer(String name, IExecution execution);

	/**
	 * 设置一个定时器。
	 * 
	 * @param name
	 *            名称
	 * @param script
	 *            脚本
	 * @return 新增或修改后的定时器
	 */
	Timer setTimer(String name, String script);

	/**
	 * 移除一个定时器。
	 * 
	 * @param name
	 *            名称
	 * @return 移除的定时器，未找到时返回null
	 */
	Timer removeTimer(String name);

	/**
	 * 重置定时间隔。<br>
	 * 如定时间隔大于0则开启，否则关闭。
	 * 
	 * @param name
	 *            名称
	 * @param tick
	 *            定时间隔（毫秒）
	 * @return 定时器，未找到时返回null
	 */
	Timer resetTimer(String name, int tick);

	/**
	 * 切换定时器开关状态。<br>
	 * 如定时间隔不大于0时无效。
	 * 
	 * @param name
	 *            名称
	 * @return 定时器，未找到时返回null
	 */
	Timer switchTimer(String name);
}
