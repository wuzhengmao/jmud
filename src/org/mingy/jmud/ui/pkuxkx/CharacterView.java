package org.mingy.jmud.ui.pkuxkx;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.mingy.jmud.model.Context;
import org.mingy.jmud.model.IExecution;
import org.mingy.jmud.model.IScope;
import org.mingy.jmud.model.Scope;
import org.mingy.jmud.model.pkuxkx.Configuration;
import org.mingy.jmud.ui.PartAdapter;
import org.mingy.jmud.ui.SessionEditorInput;

public class CharacterView extends org.mingy.jmud.ui.CharacterView {
	public CharacterView() {
	}

	public static final String ID = "org.mingy.jmud.ui.pkuxkx.CharacterView"; //$NON-NLS-1$

	private static final String MODULE = "_CHARACTER_VIEW_";

	private Composite composite;
	private Label currentHp;
	private Label currentHpPercent;
	private Label effectiveHp;
	private Label effectiveHpPercent;
	private Label currentSp;
	private Label currentSpPercent;
	private Label effectiveSp;
	private Label effectiveSpPercent;
	private Label currentForce;
	private Label currentForcePercent;
	private Label maxForce;
	private Label enforce;
	private Label currentEnergy;
	private Label currentEnergyPercent;
	private Label maxEnergy;
	private Label energy;
	private Label pots;
	private Label maxLevel;
	private Label exp;
	private Label nextLevelExp;
	private Label currentWater;
	private Label maxWater;
	private Label currentFood;
	private Label maxFood;

	@Override
	protected void createControls(Composite parent) {
		composite = parent;
		parent.setLayout(new GridLayout(10, false));
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("气血：");
		}
		{
			currentHp = new Label(parent, SWT.NONE);
			currentHp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
					false, 1, 1));
			currentHp.setText("100");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("[");
		}
		{
			currentHpPercent = new Label(parent, SWT.NONE);
			currentHpPercent.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
					false, false, 1, 1));
			currentHpPercent.setText("100%");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("]");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("/");
		}
		{
			effectiveHp = new Label(parent, SWT.NONE);
			effectiveHp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
					false, false, 1, 1));
			effectiveHp.setText("100");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("[");
		}
		{
			effectiveHpPercent = new Label(parent, SWT.NONE);
			effectiveHpPercent.setLayoutData(new GridData(SWT.RIGHT,
					SWT.CENTER, false, false, 1, 1));
			effectiveHpPercent.setText("100%");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("]");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("精神：");
		}
		{
			currentSp = new Label(parent, SWT.NONE);
			currentSp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
					false, 1, 1));
			currentSp.setText("100");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("[");
		}
		{
			currentSpPercent = new Label(parent, SWT.NONE);
			currentSpPercent.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
					false, false, 1, 1));
			currentSpPercent.setText("100%");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("]");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("/");
		}
		{
			effectiveSp = new Label(parent, SWT.NONE);
			effectiveSp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
					false, false, 1, 1));
			effectiveSp.setText("100");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("[");
		}
		{
			effectiveSpPercent = new Label(parent, SWT.NONE);
			effectiveSpPercent.setLayoutData(new GridData(SWT.RIGHT,
					SWT.CENTER, false, false, 1, 1));
			effectiveSpPercent.setText("100%");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("]");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("内力：");
		}
		{
			currentForce = new Label(parent, SWT.NONE);
			currentForce.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
					false, false, 1, 1));
			currentForce.setText("0");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("[");
		}
		{
			currentForcePercent = new Label(parent, SWT.NONE);
			currentForcePercent.setLayoutData(new GridData(SWT.RIGHT,
					SWT.CENTER, false, false, 1, 1));
			currentForcePercent.setText("100%");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("]");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("/");
		}
		{
			maxForce = new Label(parent, SWT.NONE);
			maxForce.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
					false, 1, 1));
			maxForce.setText("0");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("(");
		}
		{
			enforce = new Label(parent, SWT.NONE);
			enforce.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
					false, 1, 1));
			enforce.setText("+0");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText(")");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("精力：");
		}
		{
			currentEnergy = new Label(parent, SWT.NONE);
			currentEnergy.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
					false, false, 1, 1));
			currentEnergy.setText("0");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("[");
		}
		{
			currentEnergyPercent = new Label(parent, SWT.NONE);
			currentEnergyPercent.setLayoutData(new GridData(SWT.RIGHT,
					SWT.CENTER, false, false, 1, 1));
			currentEnergyPercent.setText("100%");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("]");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("/");
		}
		{
			maxEnergy = new Label(parent, SWT.NONE);
			maxEnergy.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
					false, 1, 1));
			maxEnergy.setText("0");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("(");
		}
		{
			energy = new Label(parent, SWT.NONE);
			energy.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
					false, 1, 1));
			energy.setText("+0");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText(")");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("潜能：");
		}
		{
			pots = new Label(parent, SWT.NONE);
			pots.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
					false, 3, 1));
			pots.setText("100");
		}
		new Label(parent, SWT.NONE);
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("/");
		}
		{
			maxLevel = new Label(parent, SWT.NONE);
			maxLevel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
					false, 3, 1));
			maxLevel.setText("0");
		}
		new Label(parent, SWT.NONE);
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("经验：");
		}
		{
			exp = new Label(parent, SWT.NONE);
			exp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
					3, 1));
			exp.setText("0");
		}
		new Label(parent, SWT.NONE);
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("/");
		}
		{
			nextLevelExp = new Label(parent, SWT.NONE);
			nextLevelExp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
					false, false, 3, 1));
			nextLevelExp.setText("1");
		}
		new Label(parent, SWT.NONE);
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("饮食：");
		}
		{
			currentWater = new Label(parent, SWT.NONE);
			currentWater.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
					false, false, 1, 1));
			currentWater.setText("200");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("/");
		}
		{
			maxWater = new Label(parent, SWT.NONE);
			maxWater.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
					false, 1, 1));
			maxWater.setText("300");
		}
		new Label(parent, SWT.NONE);
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("|");
		}
		{
			currentFood = new Label(parent, SWT.NONE);
			currentFood.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
					false, false, 1, 1));
			currentFood.setText("200");
		}
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText("/");
		}
		{
			maxFood = new Label(parent, SWT.NONE);
			maxFood.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
					false, 1, 1));
			maxFood.setText("300");
		}
		new Label(parent, SWT.NONE);
	}

	@Override
	protected void init(SessionEditorInput input) {
		final Context context = input.getContext();
		final Scope scope = context.addChild(MODULE);
		IPartService service = (IPartService) getSite().getService(
				IPartService.class);
		service.addPartListener(new PartAdapter() {
			@Override
			public void partClosed(IWorkbenchPart part) {
				context.removeChild(MODULE);
			}
		});
		scope.addTrigger(null, Configuration.HP_PATTERN1, new IExecution() {
			@Override
			public void execute(final IScope scope, final String[] args)
					throws InterruptedException {
				getSite().getShell().getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						currentSp.setText(args[1]);
						effectiveSp.setText(args[2]);
						effectiveSpPercent.setText(args[3] + "%");
						int effsp = Integer.parseInt(args[2]);
						int spp = effsp > 0 ? Integer.parseInt(args[1]) * 100
								/ effsp : 0;
						currentSpPercent.setText(spp + "%");
						currentEnergy.setText(args[4]);
						maxEnergy.setText(args[5]);
						energy.setText(args[6] + "+");
						int maxeng = Integer.parseInt(args[5]);
						int engp = maxeng > 0 ? Integer.parseInt(args[4]) * 100
								/ maxeng : 0;
						currentEnergyPercent.setText(engp + "%");
						composite.layout();
					}
				});
			}
		});
		scope.addTrigger(null, Configuration.HP_PATTERN2, new IExecution() {
			@Override
			public void execute(final IScope scope, final String[] args)
					throws InterruptedException {
				getSite().getShell().getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						currentHp.setText(args[1]);
						effectiveHp.setText(args[2]);
						effectiveHpPercent.setText(args[3] + "%");
						int effhp = Integer.parseInt(args[2]);
						int hpp = effhp > 0 ? Integer.parseInt(args[1]) * 100
								/ effhp : 0;
						currentHpPercent.setText(hpp + "%");
						currentForce.setText(args[4]);
						maxForce.setText(args[5]);
						enforce.setText(args[6] + "+");
						int maxforce = Integer.parseInt(args[5]);
						int forcep = maxforce > 0 ? Integer.parseInt(args[4])
								* 100 / maxforce : 0;
						currentForcePercent.setText(forcep + "%");
						composite.layout();
					}
				});
			}
		});
		scope.addTrigger(null, Configuration.HP_PATTERN3, new IExecution() {
			@Override
			public void execute(final IScope scope, final String[] args)
					throws InterruptedException {
				getSite().getShell().getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						currentFood.setText(args[1]);
						maxFood.setText(args[2]);
						pots.setText(args[4]);
						composite.layout();
					}
				});
			}
		});
		scope.addTrigger(null, Configuration.HP_PATTERN4, new IExecution() {
			@Override
			public void execute(final IScope scope, final String[] args)
					throws InterruptedException {
				getSite().getShell().getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						currentWater.setText(args[1]);
						maxWater.setText(args[2]);
						exp.setText(args[4]);
						long e = Integer.parseInt(args[4]);
						long lvl = (long) Math.pow(e * 10, 1.0 / 3) + 1;
						maxLevel.setText(String.valueOf(lvl));
						long n = (long) Math.ceil((lvl + 1) * (lvl + 1)
								* (lvl + 1) / 10.0);
						nextLevelExp.setText(String.valueOf(n));
						composite.layout();
					}
				});
			}
		});
		scope.addTrigger(null, new String[] { Configuration.HPBRIEF_PATTERN1,
				Configuration.HPBRIEF_PATTERN2 }, new IExecution() {
			@Override
			public void execute(final IScope scope, final String[] args)
					throws InterruptedException {
				getSite().getShell().getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						currentHp.setText(args[9]);
						effectiveHp.setText(args[8]);
						int effhp = Integer.parseInt(args[8]);
						effectiveHpPercent.setText((effhp * 100 / Integer
								.parseInt(args[7])) + "%");
						int hpp = effhp > 0 ? Integer.parseInt(args[9]) * 100
								/ effhp : 0;
						currentHpPercent.setText(hpp + "%");
						currentSp.setText(args[12]);
						effectiveSp.setText(args[11]);
						int effsp = Integer.parseInt(args[11]);
						effectiveSpPercent.setText((effsp * 100 / Integer
								.parseInt(args[10])) + "%");
						int spp = effsp > 0 ? Integer.parseInt(args[12]) * 100
								/ effsp : 0;
						currentSpPercent.setText(spp + "%");
						currentForce.setText(args[4]);
						maxForce.setText(args[3]);
						int maxforce = Integer.parseInt(args[3]);
						int forcep = maxforce > 0 ? Integer.parseInt(args[4])
								* 100 / maxforce : 0;
						currentForcePercent.setText(forcep + "%");
						currentEnergy.setText(args[6]);
						maxEnergy.setText(args[5]);
						int maxeng = Integer.parseInt(args[5]);
						int engp = maxeng > 0 ? Integer.parseInt(args[6]) * 100
								/ maxeng : 0;
						currentEnergyPercent.setText(engp + "%");
						pots.setText(args[2]);
						exp.setText(args[1]);
						long e = Integer.parseInt(args[1]);
						long lvl = (long) Math.pow(e * 10, 1.0 / 3) + 1;
						maxLevel.setText(String.valueOf(lvl));
						long n = (long) Math.ceil((lvl + 1) * (lvl + 1)
								* (lvl + 1) / 10.0);
						nextLevelExp.setText(String.valueOf(n));
						composite.layout();
					}
				});
			}
		});
	}
}
