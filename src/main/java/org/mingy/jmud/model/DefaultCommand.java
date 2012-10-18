package org.mingy.jmud.model;

/**
 * 默认的指令。
 * <p>
 * <li>ALIAS指令
 * <li>赋值语句
 * <li>发送到MUD客户端
 * </p>
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class DefaultCommand extends Command {

	@Override
	public void execute(Context context, String[] extras) {
		// TODO Auto-generated method stub
		context.CLIENT.send(args[0]);
	}
}
