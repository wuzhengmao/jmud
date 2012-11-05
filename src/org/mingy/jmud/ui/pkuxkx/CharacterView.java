package org.mingy.jmud.ui.pkuxkx;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class CharacterView extends org.mingy.jmud.ui.CharacterView {

	public static final String ID = "org.mingy.jmud.ui.pkuxkx.CharacterView"; //$NON-NLS-1$

	@Override
	protected void createControls(Composite parent) {
		parent.setLayout(new GridLayout(8, false));
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("气血：");
		}
		{
			Label currentHp = new Label(parent, SWT.NONE);
			currentHp.setText("100");
		}
		{
			Label currentHpPercent = new Label(parent, SWT.NONE);
			currentHpPercent.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
					false, false, 2, 1));
			currentHpPercent.setText("(100%)");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("/");
		}
		{
			Label maxHp = new Label(parent, SWT.NONE);
			maxHp.setText("100");
		}
		{
			Label maxHpPercent = new Label(parent, SWT.NONE);
			maxHpPercent.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
					false, false, 2, 1));
			maxHpPercent.setText("(100%)");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("精神：");
		}
		{
			Label currentSp = new Label(parent, SWT.NONE);
			currentSp.setText("100");
		}
		{
			Label currentSpPercent = new Label(parent, SWT.NONE);
			currentSpPercent.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
					false, false, 2, 1));
			currentSpPercent.setText("(100%)");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("/");
		}
		{
			Label maxSp = new Label(parent, SWT.NONE);
			maxSp.setText("100");
		}
		{
			Label maxSpPercent = new Label(parent, SWT.NONE);
			maxSpPercent.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
					false, false, 2, 1));
			maxSpPercent.setText("(100%)");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("内力：");
		}
		{
			Label currentForce = new Label(parent, SWT.NONE);
			currentForce.setText("0");
		}
		{
			Label currentForcePercent = new Label(parent, SWT.NONE);
			currentForcePercent.setLayoutData(new GridData(SWT.LEFT,
					SWT.CENTER, false, false, 2, 1));
			currentForcePercent.setText("(100%)");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("/");
		}
		{
			Label maxForce = new Label(parent, SWT.NONE);
			maxForce.setText("0");
		}
		{
			Label enforce = new Label(parent, SWT.NONE);
			enforce.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
					false, 2, 1));
			enforce.setText("(+0)");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("精力：");
		}
		{
			Label currentEnergy = new Label(parent, SWT.NONE);
			currentEnergy.setText("0");
		}
		{
			Label currentEnergyPercent = new Label(parent, SWT.NONE);
			currentEnergyPercent.setLayoutData(new GridData(SWT.LEFT,
					SWT.CENTER, false, false, 2, 1));
			currentEnergyPercent.setText("(100%)");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("/");
		}
		{
			Label maxEnergy = new Label(parent, SWT.NONE);
			maxEnergy.setText("0");
		}
		{
			Label energy = new Label(parent, SWT.NONE);
			energy.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
					false, 2, 1));
			energy.setText("(+0)");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("潜能：");
		}
		{
			Label pots = new Label(parent, SWT.NONE);
			pots.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,
					3, 1));
			pots.setText("100");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("/");
		}
		{
			Label maxLevel = new Label(parent, SWT.NONE);
			maxLevel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
					false, 3, 1));
			maxLevel.setText("0");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("经验：");
		}
		{
			Label exp = new Label(parent, SWT.NONE);
			exp.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,
					3, 1));
			exp.setText("0");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("/");
		}
		{
			Label nextLevelExp = new Label(parent, SWT.NONE);
			nextLevelExp.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
					false, false, 3, 1));
			nextLevelExp.setText("1");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("饮食：");
		}
		{
			Label currentWater = new Label(parent, SWT.NONE);
			currentWater.setText("200");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("/");
		}
		{
			Label maxWater = new Label(parent, SWT.NONE);
			maxWater.setText("300");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("|");
		}
		{
			Label currentFood = new Label(parent, SWT.NONE);
			currentFood.setText("200");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("/");
		}
		{
			Label maxFood = new Label(parent, SWT.NONE);
			maxFood.setText("300");
		}
	}
}
