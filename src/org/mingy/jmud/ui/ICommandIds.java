package org.mingy.jmud.ui;

/**
 * Interface defining the application's command IDs. Key bindings can be defined
 * for specific commands. To associate an action with a command, use
 * IAction.setActionDefinitionId(commandId).
 * 
 * @see org.eclipse.jface.action.IAction#setActionDefinitionId(String)
 */
public interface ICommandIds {

	public static final String CMD_NEW_SESSION = "org.mingy.jmud.ui.newSession";
	public static final String CMD_RECONNECT = "org.mingy.jmud.ui.reconnect";
	public static final String CMD_DISCONNECT = "org.mingy.jmud.ui.disconnect";
	public static final String CMD_OPEN_CHARACTER = "org.mingy.jmud.ui.openCharacter";
	public static final String CMD_OPEN_CONSOLE = "org.mingy.jmud.ui.openConsole";

}
