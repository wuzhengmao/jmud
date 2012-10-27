package org.mingy.jmud.client;

/**
 * <a href="http://en.wikipedia.org/wiki/ANSI_escape_code#Colors">Select Graphic
 * Rendition</a>
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class SGR {

	/** ESC */
	public static final String ESC = new String(new byte[] { 27 });

	/** 默认的文字颜色 */
	public static final String DEFAULT = ESC + "[0m";
	/** 回显的文字颜色 */
	public static final String ECHO = ESC + "[1;33m";
	/** 提示信息的颜色 */
	public static final String INFO = ESC + "[1;36m";
	/** 错误信息的颜色 */
	public static final String ERROR = ESC + "[1;31;47m";

	/** 黑色 */
	public static final String BLACK = "BLK";
	/** 红色 */
	public static final String RED = "RED";
	/** 绿色 */
	public static final String GREEN = "GRN";
	/** 土黄色 */
	public static final String YELLOW = "YEL";
	/** 深蓝色 */
	public static final String BLUE = "BLU";
	/** 浅紫色 */
	public static final String MAGENTA = "MAG";
	/** 蓝绿色 */
	public static final String CYAN = "CYN";
	/** 浅灰色 */
	public static final String LIGHT_GRAY = "WHT";
	/** 深灰色 */
	public static final String DARK_GRAY = "HID";
	/** 亮红色 */
	public static final String BRIGHT_RED = "HIR";
	/** 亮绿色 */
	public static final String BRIGHT_GREEN = "HIG";
	/** 黄色 */
	public static final String BRIGHT_YELLOW = "HIY";
	/** 蓝色 */
	public static final String BRIGHT_BLUE = "HIB";
	/** 粉红色 */
	public static final String PINK = "HIM";
	/** 天青色 */
	public static final String BRIGHT_CYAN = "HIC";
	/** 白色 */
	public static final String WHITE = "HIW";
	/** 默认色 */
	public static final String NORMAL = "NOR";

	private static int DEFAULT_BACKGROUND_COLOR_INDEX = 0;
	private static int DEFAULT_TEXT_COLOR_INDEX = 2;

	private static int[][][] colorTable = new int[][][] {
			new int[][] { new int[] { 0, 0, 0 }, new int[] { 128, 0, 0 },
					new int[] { 0, 128, 0 }, new int[] { 128, 128, 0 },
					new int[] { 0, 0, 128 }, new int[] { 128, 0, 128 },
					new int[] { 0, 128, 128 }, new int[] { 192, 192, 192 } },
			new int[][] { new int[] { 128, 128, 128 }, new int[] { 255, 0, 0 },
					new int[] { 0, 255, 0 }, new int[] { 255, 255, 0 },
					new int[] { 0, 0, 255 }, new int[] { 255, 0, 255 },
					new int[] { 0, 255, 255 }, new int[] { 255, 255, 255 } } };

	private String style;
	private boolean bright;
	private boolean italic;
	private boolean underline;
	private boolean blink;
	private boolean reverse;
	private int backgroundColorIndex;
	private int textColorIndex;

	/**
	 * 构造器。
	 * 
	 * @param style
	 *            样式
	 */
	public SGR(String style) {
		this(style, null);
	}

	/**
	 * 构造器。
	 * 
	 * @param style
	 *            样式
	 * @param parent
	 *            继承的SGR
	 */
	public SGR(String style, SGR parent) {
		this.style = style;
		if (parent != null) {
			bright = parent.bright;
			italic = parent.italic;
			underline = parent.underline;
			blink = parent.blink;
			reverse = parent.reverse;
			backgroundColorIndex = parent.backgroundColorIndex;
			textColorIndex = parent.textColorIndex;
		}
		init();
	}

	private void init() {
		String[] ss = style.substring(2, style.length() - 1).split(";");
		for (String s : ss) {
			int code = Integer.parseInt(s);
			if (code == 0) {
				bright = false;
				italic = false;
				underline = false;
				blink = false;
				reverse = false;
				backgroundColorIndex = DEFAULT_BACKGROUND_COLOR_INDEX;
				textColorIndex = DEFAULT_TEXT_COLOR_INDEX;
			} else if (code == 1) {
				bright = true;
			} else if (code == 3) {
				italic = true;
			} else if (code == 4) {
				underline = true;
			} else if (code == 5 || code == 6) {
				blink = true;
			} else if (code == 7) {
				reverse = true;
			} else if (code >= 30 && code < 38) {
				textColorIndex = code - 30;
			} else if (code >= 40 && code < 48) {
				backgroundColorIndex = code - 40;
			}
		}
	}

	/**
	 * 返回样式，即SGR参数，如&lt;ESC&gt;[0m。
	 * 
	 * @return 样式
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * 是否高亮显示。
	 * 
	 * @return true为是
	 */
	public boolean isBright() {
		return bright;
	}

	/**
	 * 是否为斜体。
	 * 
	 * @return true为是
	 */
	public boolean isItalic() {
		return italic;
	}

	/**
	 * 是否加下划线。
	 * 
	 * @return true为是
	 */
	public boolean isUnderline() {
		return underline;
	}

	/**
	 * 是否闪烁显示。
	 * 
	 * @return true为是
	 */
	public boolean isBlink() {
		return blink;
	}

	/**
	 * 是否反转文字颜色和背景色。
	 * 
	 * @return true为是
	 */
	public boolean isReverse() {
		return reverse;
	}

	/**
	 * 返回背景色（RGB）。
	 * 
	 * @return 背景色
	 */
	public int[] getBackgroundColor() {
		int i = isReverse() ? textColorIndex : backgroundColorIndex;
		return colorTable[0][i];
	}

	/**
	 * 返回文字颜色（RGB）。
	 * 
	 * @return 文字颜色
	 */
	public int[] getTextColor() {
		int i = isReverse() ? backgroundColorIndex : textColorIndex;
		int n = isBright() ? 1 : 0;
		return colorTable[n][i];
	}
}
