package org.mingy.jmud.client;

import java.awt.Toolkit;
import java.nio.charset.Charset;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.mingy.jmud.model.Context;
import org.mingy.jmud.model.ShortKey;

/**
 * MUD客户端实现。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class MudClient implements TelnetClientListener, IMudClient {

	/** 日志 */
	private static final Log logger = LogFactory.getLog(MudClient.class);

	/** 默认字体 */
	private static final String DEFAULT_FONT_FAMILY = "YaHei Consolas Hybrid";
	/** 默认的字体大小 */
	private static final int DEFAULT_FONT_SIZE = 10;
	/** 默认的语种 */
	private static final String DEFAULT_LOCALE = "zh_CN";
	/** 默认的字符集 */
	private static final String DEFAULT_CHARSET = "GBK";
	/** 富文本内容的最大行数 */
	private static final int CONTENT_MAX_LINES = 1000;
	/** 指令历史记录数 */
	private static final int COMMAND_HISTORY_SIZE = 20;
	/** TAB的长度 */
	private static final int TABS = 8;

	/** 主机名 */
	private String hostname;
	/** 端口 */
	private int port;
	/** 连接超时（毫秒） */
	private int connectTimeout;
	/** 字符集 */
	private Charset charset;
	/** 富文本显示区 */
	private StyledText styledText;
	/** 指令输入框 */
	private Text commandInput;
	/** 上下文 */
	private Context context;
	/** UI */
	private Display display;
	/** 富文本内容 */
	private StyledTextContent content;
	/** 默认的SGR */
	private SGR defaultSGR;
	/** 当前的SGR */
	private SGR currentSGR;
	/** Telnet客户端 */
	private TelnetClient client;
	/** 指令历史记录 */
	private LinkedList<String> commands;
	/** 指令历史选择时的指针 */
	private int cmdptr;
	/** 富文本显示区是否可以滚动 */
	private boolean canScroll;
	/** 富文本显示区是否正在滚动 */
	private boolean scrolling;
	/** true时禁止显示接收到的数据 */
	private boolean echoForbidden;

	/** 是否为MAC操作系统 */
	private static final boolean IS_MAC;
	static {
		String platform = SWT.getPlatform();
		IS_MAC = "carbon".equals(platform) || "cocoa".equals(platform);
	}

	/**
	 * 构造器。
	 * 
	 * @param hostname
	 *            主机名或IP
	 * @param port
	 *            端口
	 * @param connectTimeout
	 *            连接超时（毫秒）
	 * @param charset
	 *            字符集
	 * @param mainStyledText
	 *            富文本显示区
	 * @param commandInput
	 *            指令输入框
	 * @param context
	 *            上下文
	 */
	public MudClient(String hostname, int port, int connectTimeout,
			String charset, StyledText styledText, Text commandInput,
			Context context) {
		this.hostname = hostname;
		this.port = port;
		this.connectTimeout = connectTimeout;
		this.charset = Charset.forName(charset);
		this.styledText = styledText;
		this.commandInput = commandInput;
		this.context = context;
		init();
	}

	@Override
	public void connect() {
		if (client == null)
			client = new TelnetClient(hostname, port, this);
		client.connect(connectTimeout);
	}

	@Override
	public void close() {
		if (client == null)
			throw new IllegalStateException("no telnet client instance");
		context.destroy();
		client.close();
		client = null;
	}

	@Override
	public void onConnected() {
		echoForbidden = false;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				initSGR();
				echo("Connected to " + hostname + ":" + port + "\n", SGR.INFO);
			}
		});
	}

	@Override
	public void onDisconnected() {
		// TODO: 重连？
	}

	@Override
	public void onReceived(byte[] data) {
		try {
			processLine(data);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("error on receive handler", e);
			}
		}
	}

	@Override
	public void beep() {
		Toolkit.getDefaultToolkit().beep();
	}

	@Override
	public void echoOn() {
		echoForbidden = false;
	}

	@Override
	public void echoOff() {
		echoForbidden = true;
	}

	private void init() {
		context.init(this);
		display = styledText.getDisplay();
		content = styledText.getContent();
		styledText.setEditable(false);
		styledText.setWordWrap(true);
		styledText.setCaret(null);
		styledText.setTabs(TABS);
		styledText.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent event) {
				ScrollBar sb = styledText.getVerticalBar();
				if (sb.getMinimum() + sb.getThumb() < sb.getMaximum()) {
					canScroll = true;
					if (!scrolling)
						scrollToEnd();
				} else {
					canScroll = false;
					scrolling = false;
				}
			}
		});
		styledText.getVerticalBar().addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent event) {
						ScrollBar sb = styledText.getVerticalBar();
						scrolling = sb.getSelection() + sb.getThumb() < sb
								.getMaximum();
					}
				});
		final ScrollListener scrollListener = new ScrollListener();
		styledText.addListener(SWT.KeyDown, scrollListener);
		styledText.addListener(SWT.MouseWheel, scrollListener);
		commandInput.addListener(SWT.KeyDown, scrollListener);
		commandInput.addListener(SWT.MouseWheel, scrollListener);
		commandInput.addListener(SWT.KeyDown, new ShortKeyListener());
		commandInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				switch (event.keyCode) {
				case SWT.CR:
					doCommand();
					event.doit = false;
					break;
				case SWT.ARROW_UP:
					if (cmdptr > 0) {
						cmdptr--;
						commandInput.setText(commands.get(cmdptr));
					} else {
						cmdptr = -1;
						commandInput.setText("");
					}
					event.doit = false;
					break;
				case SWT.ARROW_DOWN:
					if (cmdptr < commands.size() - 1) {
						cmdptr++;
						commandInput.setText(commands.get(cmdptr));
					} else {
						cmdptr = commands.size();
						commandInput.setText("");
					}
					event.doit = false;
					break;
				}
			}
		});
		commands = new LinkedList<String>();
	}

	/**
	 * 监听并处理PageUp、PageDown和鼠标的滚卷事件。
	 */
	class ScrollListener implements Listener {
		@Override
		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.KeyDown:
				int state = event.stateMask & SWT.MODIFIER_MASK;
				switch (event.keyCode) {
				case SWT.PAGE_UP:
					scrollUp(-1);
					event.doit = false;
					break;
				case SWT.PAGE_DOWN:
					scrollDown(-1);
					event.doit = false;
					break;
				case SWT.HOME:
					if ((IS_MAC && state == SWT.NONE)
							|| (!IS_MAC && state == SWT.CTRL)) {
						scrollToTop();
						event.doit = false;
					}
					break;
				case SWT.END:
					if ((IS_MAC && state == SWT.NONE)
							|| (!IS_MAC && state == SWT.CTRL)) {
						scrollToEnd();
						event.doit = false;
					}
					break;
				}
				break;
			case SWT.MouseWheel:
				if (event.detail == SWT.SCROLL_LINE) {
					if (event.count > 0)
						scrollUp(event.count);
					else
						scrollDown(-event.count);
					event.doit = false;
				}
				break;
			}
		}
	}

	private void scrollUp(int units) {
		if (!canScroll)
			return;
		scrolling = true;
		Rectangle rect = styledText.getClientArea();
		int y = styledText.getTopIndex();
		int top = styledText.getLinePixel(y);
		top -= units < 0 ? rect.height : (units * styledText.getLineHeight());
		if (top > styledText.getLinePixel(0)) {
			y = styledText.getLineIndex(top);
			if (top > styledText.getLinePixel(y))
				y++;
			if (logger.isTraceEnabled()) {
				logger.trace("scroll to line: " + y);
			}
			styledText.setTopIndex(y);
		} else {
			scrollToTop();
		}
	}

	private void scrollDown(int units) {
		if (!scrolling)
			return;
		Rectangle rect = styledText.getClientArea();
		int n = styledText.getLineCount();
		int y = styledText.getTopIndex();
		int top = styledText.getLinePixel(y);
		top += units < 0 ? rect.height : (units * styledText.getLineHeight());
		if (top + rect.height < styledText.getLinePixel(n)) {
			y = styledText.getLineIndex(top);
			if (logger.isTraceEnabled()) {
				logger.trace("scroll to line: " + y);
			}
			styledText.setTopIndex(y);
		} else {
			scrollToEnd();
		}
	}

	private void scrollToTop() {
		if (!canScroll)
			return;
		scrolling = true;
		if (logger.isTraceEnabled()) {
			logger.trace("scroll to line: " + 0);
		}
		styledText.setTopIndex(0);
	}

	private void scrollToEnd() {
		scrolling = false;
		Rectangle rect = styledText.getClientArea();
		int n = styledText.getLineCount();
		int bottom = styledText.getLinePixel(n);
		int top = bottom - rect.height;
		int y = styledText.getLineIndex(top);
		if (top > styledText.getLinePixel(y))
			y++;
		if (logger.isTraceEnabled()) {
			logger.trace("scroll to line: " + y);
		}
		styledText.setTopIndex(y);
	}

	private void initSGR() {
		currentSGR = defaultSGR = new SGR(SGR.DEFAULT);
		int[] rgb = defaultSGR.getBackgroundColor();
		styledText.setBackground(new Color(display, rgb[0], rgb[1], rgb[2]));
	}

	private void processLine(byte[] bytes) throws Exception {
		int n = bytes.length;
		int p = 0;
		int i = 0;
		while ((i = matchEscStart(bytes, p)) >= 0) {
			if (i > p) {
				appendLine(bytes, p, i - p, true);
				p = i;
			}
			int e = matchEscEnd(bytes, p);
			if (e > p) {
				String s = new String(bytes, p, e - p + 1);
				if (bytes[p + 1] == '[' && bytes[e] == 'm') {
					if (logger.isDebugEnabled()) {
						logger.debug("SGR: <ESC>" + s.substring(1));
					}
					currentSGR = new SGR(s, currentSGR);
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("ignore CSI: <ESC>" + s.substring(1));
					}
				}
				p = e + 1;
				i = p;
			} else {
				break;
			}
		}
		appendLine(bytes, p, n - p, false);
	}

	private void appendLine(byte[] bytes, int start, int length,
			final boolean continues) {
		if (!echoForbidden) {
			final String line = new String(bytes, start, length, charset);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					show(line, null, continues);
				}
			});
		}
	}

	@Override
	public void show(String text, String style, boolean continues) {
		if (text != null && text.length() > 0)
			echo(text, style);
		context.handleText(text, !continues);
	}

	@Override
	public void echo(String text, String style) {
		SGR sgr = style != null ? new SGR(style, defaultSGR) : currentSGR;
		int[] textColor = sgr.getTextColor();
		int[] bgColor = sgr.getBackgroundColor();
		final StyleRange sr = new StyleRange(styledText.getCharCount(),
				text.length(), new Color(display, textColor[0], textColor[1],
						textColor[2]), new Color(display, bgColor[0],
						bgColor[1], bgColor[2]));
		if (sgr.isBlink())
			sr.fontStyle |= SWT.BOLD;
		if (sgr.isItalic())
			sr.fontStyle |= SWT.ITALIC;
		if (sgr.isUnderline())
			sr.underline = true;
		if (logger.isTraceEnabled()) {
			logger.trace(text);
		}
		styledText.append(text);
		styledText.setStyleRange(sr);
		int n = styledText.getLineCount() - CONTENT_MAX_LINES;
		if (n > 0) {
			styledText.replaceTextRange(0, styledText.getOffsetAtLine(n), "");
		}
		if (logger.isTraceEnabled()) {
			logger.trace("lines: " + content.getLineCount());
		}
		if (!canScroll) {
			ScrollBar sb = styledText.getVerticalBar();
			canScroll = sb.getMinimum() + sb.getThumb() < sb.getMaximum();
		} else {
			if (!scrolling)
				scrollToEnd();
		}
	}

	private static int matchEscStart(byte[] bytes, int start) {
		for (int i = start; i < bytes.length - 1; i++) {
			if (bytes[i] == 27 && bytes[i + 1] == 91)
				return i;
		}
		return -1;
	}

	private static int matchEscEnd(byte[] bytes, int start) {
		if (bytes.length <= start + 1)
			return -1;
		if (bytes[start + 1] == '[') {
			for (int i = start + 2; i < bytes.length; i++) {
				if (bytes[i] >= 64 && bytes[i] <= 126)
					return i;
			}
			return -1;
		} else {
			return start + 1;
		}
	}

	/**
	 * 监听快捷键。
	 */
	class ShortKeyListener implements Listener {
		@Override
		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.KeyDown:
				int key = event.keyCode | event.stateMask;
				ShortKey shortKey = context.getShortKey(key);
				if (shortKey != null) {
					if (logger.isTraceEnabled()) {
						logger.trace("short key command: "
								+ shortKey.getCommand());
					}
					commandInput.setText(shortKey.getCommand());
					doCommand();
					event.doit = false;
				}
				break;
			}
		}
	}

	private void doCommand() {
		String command = commandInput.getText();
		if (command != null && command.length() > 0
				&& (commands.isEmpty() || !command.equals(commands.getLast()))) {
			commandInput.selectAll();
			commands.addLast(command);
			if (commands.size() > COMMAND_HISTORY_SIZE)
				commands.removeFirst();
			cmdptr = commands.size();
		}
		context.executeScript(command);
	}

	@Override
	public void send(String command) {
		command += "\n";
		echo(command, SGR.ECHO);
		if (client != null && client.isAvailable()) {
			client.write(command.getBytes(charset));
		}
	}

	@Override
	public void runOnUiThread(final Runnable runnable) {
		if (!display.isDisposed()) {
			display.syncExec(new Runnable() {
				@Override
				public void run() {
					runnable.run();
				}
			});
		}
	}

	/**
	 * TODO: 调试入口，创建UI布局。
	 * 
	 * @param args
	 *            未使用
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FormLayout());
		StyledText styledText = new StyledText(shell, SWT.BORDER | SWT.V_SCROLL);
		FontData fontData = new FontData(DEFAULT_FONT_FAMILY,
				DEFAULT_FONT_SIZE, SWT.NORMAL);
		fontData.setLocale(DEFAULT_LOCALE);
		Font font = new Font(display, fontData);
		styledText.setFont(font);
		Text commandInput = new Text(shell, SWT.BORDER | SWT.SINGLE);
		commandInput.forceFocus();
		FormData textData = new FormData();
		textData.top = new FormAttachment(0, 0);
		textData.bottom = new FormAttachment(commandInput, 0);
		textData.left = new FormAttachment(0, 0);
		textData.right = new FormAttachment(100, 0);
		styledText.setLayoutData(textData);
		FormData inputData = new FormData();
		inputData.bottom = new FormAttachment(100, 0);
		inputData.left = new FormAttachment(0, 0);
		inputData.right = new FormAttachment(100, 0);
		commandInput.setLayoutData(inputData);
		shell.setSize(800, 600);
		shell.open();
		IMudClient mc = new MudClient("pkuxkx.net", 5555, 10000,
				DEFAULT_CHARSET, styledText, commandInput,
				new org.mingy.jmud.model.pkuxkx.Configurations());
		mc.connect();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		mc.close();
		display.dispose();
	}
}
