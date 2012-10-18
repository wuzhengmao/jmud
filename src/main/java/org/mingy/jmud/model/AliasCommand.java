package org.mingy.jmud.model;

/**
 * 对别名进行增、删、改的指令。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class AliasCommand extends Command {

	@Override
	public boolean execute(Context context) {
		if (args.length == 0 || args.length > 2)
			return false;
		if (args.length == 1) {
			context.ALIASES.remove(args[0]);
		} else {
			context.ALIASES.add(args[0], args[1]);
		}
		return true;
	}
}
