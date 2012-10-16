package org.mingy.jmud.client;


/**
 * <a href="http://en.wikipedia.org/wiki/ANSI_escape_code#Colors">Select Graphic
 * Rendition</a>
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class SGR {

	/** 默认的文字颜色 */
	public static final String DEFAULT = new String(new byte[] { 27, '[', '0',
			'm' });
	/** 回显的文字颜色 */
	public static final String ECHO = new String(new byte[] { 27, '[', '1',
			';', '3', '3', 'm' });
	/** 提示信息的颜色 */
	public static final String INFO = new String(new byte[] { 27, '[', '1',
			';', '3', '6', 'm' });

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
