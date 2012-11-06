package org.mingy.jmud.model.pkuxkx;

import org.eclipse.swt.SWT;
import org.mingy.jmud.model.Constants;
import org.mingy.jmud.model.Context;
import org.mingy.jmud.model.Scope;
import org.mingy.jmud.ui.pkuxkx.CharacterView;

/**
 * 北大侠客行的通用配置定义。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Configuration extends org.mingy.jmud.model.Configuration {

	public static final String HP_PATTERN1 = "^【 精神 】\\s*(\\d+)\\s*/\\s*(\\d+)\\s*\\[\\s*(\\d+)%\\s*\\]\\s*【 精力 】\\s*(\\d+)\\s*/\\s*(\\d+)\\s*\\(\\+\\s*(\\d+)\\s*\\)$";
	public static final String HP_PATTERN2 = "^【 气血 】\\s*(\\d+)\\s*/\\s*(\\d+)\\s*\\[\\s*(\\d+)%\\s*\\]\\s*【 内力 】\\s*(\\d+)\\s*/\\s*(\\d+)\\s*\\(\\+\\s*(\\d+)\\s*\\)$";
	public static final String HP_PATTERN3 = "^【 食物 】\\s*(\\d+)\\s*/\\s*(\\d+)\\s*\\[\\s*(.+)\\s*\\]\\s*【 潜能 】\\s*(\\d+)\\s*$";
	public static final String HP_PATTERN4 = "^【 饮水 】\\s*(\\d+)\\s*/\\s*(\\d+)\\s*\\[\\s*(.+)\\s*\\]\\s*【 经验 】\\s*(\\d+)\\s*$";
	public static final String HP_PATTERN5 = "^【 状态 】\\s*(.+)\\s*$";
	public static final String HPBRIEF_PATTERN1 = "^(?:> |)#(\\d+),(\\d+),(\\d+),(\\d+),(\\d+),(\\d+)$";
	public static final String HPBRIEF_PATTERN2 = "^#(\\d+),(\\d+),(\\d+),(\\d+),(\\d+),(\\d+)$";

	@Override
	public String getName() {
		return "pkuxkx.net";
	}

	@Override
	public void inject(Context context) {
		context.setShortKey(SWT.KEYPAD_0, "hp");
		Scope loginModule = context.getScope(Constants.MODULE_LOGIN);
		loginModule.addTrigger(Constants.TRIGGER_GROUP_LOGIN,
				"^您的英文名字（要注册新人物请输入new）：", "@" + Constants.VAR_CHARACTER);
		loginModule.addTrigger(Constants.TRIGGER_GROUP_LOGIN,
				"此ID档案已存在，请输入密码：", "@" + Constants.VAR_PASSWORD);
		loginModule.addTrigger(Constants.TRIGGER_GROUP_LOGIN,
				"^目前权限：\\(player\\)$", "#set " + Constants.VAR_LOGIN_OK
						+ " 1;#t- " + Constants.TRIGGER_GROUP_LOGIN);
		loginModule.addTrigger(Constants.TRIGGER_GROUP_LOGIN, "^重新连线完毕。$",
				"#set " + Constants.VAR_LOGIN_OK + " 2;#t- "
						+ Constants.TRIGGER_GROUP_LOGIN);
		loginModule.addTrigger(Constants.TRIGGER_GROUP_LOGIN,
				"^您要将另一个连线中的相同人物赶出去，取而代之吗？\\(y/n\\)", "#set "
						+ Constants.VAR_LOGIN_OK + " 3;y;#t- "
						+ Constants.TRIGGER_GROUP_LOGIN);
		loginModule.addTrigger(null,
				"^==\\s*未完继续\\s*\\d+%\\s*==\\s*\\(q 离开，b 前一页，其他继续下一页\\)", "q");
		loginModule.addTrigger(null,
				"^有人从别处\\(\\s*(\\d+\\.\\d+\\.\\d+\\.\\d+)\\s*\\)连线取代你所控制的人物。$",
				"#set " + Constants.VAR_IP_RECORD + " '%1'");
		loginModule
				.addWatcher(Constants.VAR_LOGIN_OK, "#if {%2 > 0} {hpbrief}");
	}

	@Override
	public String getCharacterViewId() {
		return CharacterView.ID;
	}
}
