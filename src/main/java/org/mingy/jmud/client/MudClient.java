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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * MUD客户端实现。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class MudClient implements TelnetClientListener {

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
	/** 命令行历史记录数 */
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
	/** 命令行输入框 */
	private Text commandInput;
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
	/** 命令行历史记录 */
	private LinkedList<String> commands;
	/** 命令行历史选择时的指针 */
	private int cmdptr;
	/** 富文本显示区是否可以滚动 */
	private boolean canScroll;
	/** 富文本显示区是否正在滚动 */
	private boolean scrolling;
	/** true时禁止显示接收到的数据 */
	private boolean echoForbidden;

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
	 *            命令行输入框
	 */
	public MudClient(String hostname, int port, int connectTimeout,
			String charset, StyledText styledText, Text commandInput) {
		this.hostname = hostname;
		this.port = port;
		this.connectTimeout = connectTimeout;
		this.charset = Charset.forName(charset);
		this.styledText = styledText;
		this.commandInput = commandInput;
		init();
	}

	/**
	 * 连接主机。
	 */
	public void connect() {
		if (client == null)
			client = new TelnetClient(hostname, port, this);
		client.connect(connectTimeout);
	}

	/**
	 * 断开连接并关闭客户端。
	 */
	public void close() {
		if (client == null)
			throw new IllegalStateException("no telnet client instance");
		client.close();
		client = null;
	}

	public void onConnected() {
		echoForbidden = false;
		display.syncExec(new Runnable() {
			public void run() {
				initSGR();
				echo("Connected to " + hostname + ":" + port + "\n", SGR.INFO);
			}
		});
	}

	public void onDisconnected() {
		// TODO: 重连？
	}

	public void onReceived(byte[] data) {
		try {
			processLine(data);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("error on receive handler", e);
			}
		}
	}

	public void beep() {
		Toolkit.getDefaultToolkit().beep();
	}

	public void echoOn() {
		echoForbidden = false;
	}

	public void echoOff() {
		echoForbidden = true;
	}

	private void init() {
		display = styledText.getDisplay();
		content = styledText.getContent();
		styledText.setEditable(false);
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
		commandInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				switch (event.keyCode) {
				case SWT.CR:
					String command = commandInput.getText();
					if (command != null
							&& command.length() > 0
							&& (commands.isEmpty() || !command.equals(commands
									.getLast()))) {
						commandInput.selectAll();
						commands.addLast(command);
						if (commands.size() > COMMAND_HISTORY_SIZE)
							commands.removeFirst();
						cmdptr = commands.size();
					}
					command += "\n";
					echo(command, SGR.ECHO);
					if (client != null && client.isAvailable()) {
						client.write(command.getBytes(charset));
					}
					break;
				case SWT.ARROW_UP:
					if (cmdptr > 0) {
						cmdptr--;
						commandInput.setText(commands.get(cmdptr));
					} else {
						cmdptr = -1;
						commandInput.setText("");
					}
					break;
				case SWT.ARROW_DOWN:
					if (cmdptr < commands.size() - 1) {
						cmdptr++;
						commandInput.setText(commands.get(cmdptr));
					} else {
						cmdptr = commands.size();
						commandInput.setText("");
					}
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
		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.KeyDown:
				switch (event.keyCode) {
				case SWT.PAGE_UP:
					scrollUp(-1);
					break;
				case SWT.PAGE_DOWN:
					scrollDown(-1);
					break;
				}
				break;
			case SWT.MouseWheel:
				if (event.detail == SWT.SCROLL_LINE) {
					if (event.count > 0)
						scrollUp(event.count);
					else
						scrollDown(-event.count);
				}
				break;
			}
		}
	}

	private void scrollUp(int units) {
		if (!canScroll)
			return;
		if (logger.isTraceEnabled()) {
			if (units < 0)
				logger.trace("scroll page up");
			else
				logger.trace("scroll up: " + units);
		}
		scrolling = true;
		int y = styledText.getTopIndex();
		int h = styledText.getLineIndex(styledText.getClientArea().height) - y;
		y -= units < 0 ? h : units;
		if (y < 0)
			y = 0;
		if (logger.isTraceEnabled()) {
			logger.trace("scroll to line: " + y);
		}
		styledText.setTopIndex(y);
	}

	private void scrollDown(int units) {
		if (!scrolling)
			return;
		if (logger.isTraceEnabled()) {
			if (units < 0)
				logger.trace("scroll page down");
			else
				logger.trace("scroll down: " + units);
		}
		int n = styledText.getLineCount();
		int y = styledText.getTopIndex();
		int h = styledText.getLineIndex(styledText.getClientArea().height) - y;
		if (y + h < n) {
			y += units < 0 ? h : units;
			if (y + h >= n) {
				y = n - h;
				scrolling = false;
			}
			if (logger.isTraceEnabled()) {
				logger.trace("scroll to line: " + y);
			}
			styledText.setTopIndex(y);
		} else {
			scrolling = false;
		}
	}

	private void scrollToEnd() {
		int n = styledText.getLineCount();
		int y = styledText.getTopIndex();
		int h = styledText.getLineIndex(styledText.getClientArea().height) - y;
		y = n - h;
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
		int p = 0;
		int i = 0;
		while ((i = matchEscStart(bytes, p)) >= 0) {
			if (i > p) {
				appendLine(bytes, p, i - p);
				p = i;
			}
			int e = matchEscEnd(bytes, p);
			if (e > p) {
				String s = new String(bytes, p, e - p + 1);
				currentSGR = new SGR(s, currentSGR);
				p = e + 1;
				i = p;
			} else {
				break;
			}
		}
		if (bytes.length > p) {
			appendLine(bytes, p, bytes.length - p);
		}
	}

	private void appendLine(byte[] bytes, int start, int length) {
		if (!echoForbidden) {
			final String line = new String(bytes, start, length, charset);
			display.syncExec(new Runnable() {
				public void run() {
					echo(line, null);
				}
			});
		}
	}

	private void echo(final String message, String sgrString) {
		SGR sgr = sgrString != null ? new SGR(sgrString, defaultSGR)
				: currentSGR;
		int[] textColor = sgr.getTextColor();
		int[] bgColor = sgr.getBackgroundColor();
		final StyleRange style = new StyleRange(styledText.getCharCount(),
				message.length(), new Color(display, textColor[0],
						textColor[1], textColor[2]), new Color(display,
						bgColor[0], bgColor[1], bgColor[2]));
		if (sgr.isBlink())
			style.fontStyle |= SWT.BOLD;
		if (sgr.isItalic())
			style.fontStyle |= SWT.ITALIC;
		if (sgr.isUnderline())
			style.underline = true;
		if (logger.isTraceEnabled()) {
			logger.trace(style);
			logger.trace(message);
		}
		styledText.append(message);
		styledText.setStyleRange(style);
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
		for (int i = start; i < bytes.length; i++) {
			if (bytes[i] == 109)
				return i;
		}
		return -1;
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
		final MudClient mc = new MudClient("pkuxkx.net", 5555, 10000,
				DEFAULT_CHARSET, styledText, commandInput);
		mc.connect();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		mc.close();
		display.dispose();
	}
}
