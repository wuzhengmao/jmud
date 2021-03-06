package org.mingy.jmud.client;

import java.awt.Toolkit;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.mingy.jmud.model.Context;
import org.mingy.jmud.model.Session;
import org.mingy.jmud.model.ShortKey;

/**
 * MUD客户端实现。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class MudClient implements ITelnetClientListener, IMudClient {

	/** 日志 */
	private static final Log logger = LogFactory.getLog(MudClient.class);

	/** 富文本内容的最大行数 */
	private static final int CONTENT_MAX_LINES = 1000;
	/** 指令历史记录数 */
	private static final int COMMAND_HISTORY_SIZE = 20;
	/** TAB的长度 */
	private static final int TABS = 8;
	/** 颜色的高速缓存 */
	private static final Map<Integer, Color> COLORS;

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
	/** 当前连接状态 */
	private ConnectionStates state;
	/** 注册连接监听器 */
	private Set<IConnectionStateListener> listeners;
	/** 回显内容在文档中的位置记录 */
	private LinkedList<int[]> echoPositions;

	/** 是否为MAC操作系统 */
	private static final boolean IS_MAC;
	static {
		String platform = SWT.getPlatform();
		IS_MAC = "carbon".equals(platform) || "cocoa".equals(platform);
		COLORS = new HashMap<Integer, Color>();
	}

	/**
	 * 构造器。
	 * 
	 * @param session
	 *            会话连接信息
	 * @param mainStyledText
	 *            富文本显示区
	 * @param commandInput
	 *            指令输入框
	 */
	public MudClient(Session session, StyledText styledText, Text commandInput) {
		this.hostname = session.getHost();
		this.port = session.getPort();
		this.connectTimeout = session.getTimeout() * 1000;
		this.charset = session.getCharset();
		this.styledText = styledText;
		this.commandInput = commandInput;
		this.context = new Context(this);
		commands = new LinkedList<String>();
		state = ConnectionStates.DISCONNECTED;
		listeners = new HashSet<IConnectionStateListener>(4);
		echoPositions = new LinkedList<int[]>();
		context.init(session);
		init();
		initSGR();
	}

	@Override
	public void connect() {
		if (client == null)
			client = new TelnetClient(hostname, port, this);
		changeConnectionEvent(ConnectionStates.CONNECTING);
		echo("Connecting to " + hostname + ":" + port + " ... ", SGR.INFO);
		client.connect(connectTimeout);
	}

	@Override
	public void disconnect() {
		if (client != null) {
			client.close();
			client = null;
		}
	}

	@Override
	public void close() {
		disconnect();
		listeners.clear();
		context.destroy();
		context = null;
	}

	@Override
	public boolean isConnected() {
		return client != null && client.isAvailable();
	}

	@Override
	public boolean isDisconnected() {
		return client == null || client.isDisconnected();
	}

	@Override
	public void addConnectionStateListener(IConnectionStateListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeConnectionStateListener(IConnectionStateListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void onConnected() {
		echoForbidden = false;
		echo("Connected\n", SGR.INFO);
		changeConnectionEvent(ConnectionStates.CONNECTED);
	}

	@Override
	public void onDisconnected() {
		if (state == ConnectionStates.CONNECTING)
			echo("Failed\n", SGR.INFO);
		else
			echo("Lost connection of " + hostname + ":" + port + "\n", SGR.INFO);
		changeConnectionEvent(ConnectionStates.DISCONNECTED);
	}

	private void changeConnectionEvent(ConnectionStates newState) {
		ConnectionEvent event = new ConnectionEvent(this, state, newState);
		state = newState;
		for (IConnectionStateListener listener : listeners)
			listener.onStateChanged(event);
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
		commandInput.addKeyListener(new CommandInputListener());
		commandInput.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent event) {
				if (event.detail == SWT.TRAVERSE_TAB_NEXT
						|| event.detail == SWT.TRAVERSE_TAB_PREVIOUS)
					event.doit = false;
			}
		});
	}

	/**
	 * 命令行输入键的监听。
	 */
	class CommandInputListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent event) {
			switch (event.keyCode) {
			case SWT.CR:
				doCommand();
				event.doit = false;
				break;
			case SWT.TAB:
				String command = commandInput.getText();
				if (command != null && command.length() > 0) {
					String script = context.expandAlias(command);
					if (script != null) {
						commandInput.setText(script);
						commandInput.setSelection(script.length());
					}
				}
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
				commandInput.setSelection(commandInput.getText().length());
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
				commandInput.setSelection(commandInput.getText().length());
				event.doit = false;
				break;
			}
		}
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
		styledText.setBackground(getColor(display,
				defaultSGR.getBackgroundColor()));
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
					if (logger.isTraceEnabled()) {
						logger.trace("SGR: <ESC>" + s.substring(1));
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
			boolean continues) {
		if (!echoForbidden) {
			String line = new String(bytes, start, length, charset).replace(
					"\r\n", "\n");
			show(line, null, continues);
		}
	}

	@Override
	public void show(final String text, final String style,
			final boolean continues) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (text != null && text.length() > 0)
					doEcho(text, style);
				context.handleText(text, !continues);
			}
		});
	}

	@Override
	public void echo(final String text, final String style) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (text != null && text.length() > 0) {
					doEcho(text, style);
					int[] pos = new int[2];
					pos[1] = styledText.getCharCount();
					pos[0] = pos[1] - text.length();
					echoPositions.addLast(pos);
				}
			}
		});
	}

	private void doEcho(String text, String style) {
		SGR sgr = style != null ? new SGR(style, defaultSGR) : currentSGR;
		final StyleRange sr = new StyleRange(styledText.getCharCount(),
				text.length(), getColor(display, sgr.getTextColor()), getColor(
						display, sgr.getBackgroundColor()));
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
		if (n > 0)
			cutText(0, styledText.getOffsetAtLine(n));
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

	private void cutText(int start, int end) {
		int n = end - start;
		styledText.replaceTextRange(start, n, "");
		for (Iterator<int[]> it = echoPositions.descendingIterator(); it
				.hasNext();) {
			int[] pos = it.next();
			if (pos[1] <= start)
				break;
			pos[1] -= n;
			if (pos[0] >= end)
				pos[0] -= n;
			else if (pos[0] > start)
				pos[0] = start;
			if (pos[1] < 0) {
				it.remove();
			} else if (pos[0] < 0) {
				pos[0] = 0;
			}
		}
	}

	@Override
	public void hide(final String text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				int len = styledText.getCharCount();
				int k = len - text.length();
				for (Iterator<int[]> it = echoPositions.descendingIterator(); it
						.hasNext();) {
					int[] pos = it.next();
					if (pos[1] > k) {
						k -= pos[1] - pos[0];
					} else {
						break;
					}
				}
				String str = styledText.getTextRange(k, len - k);
				List<int[]> list = null;
				for (Iterator<int[]> it = echoPositions.descendingIterator(); it
						.hasNext();) {
					int[] pos = it.next();
					if (pos[0] < len && pos[1] > k) {
						str = str.substring(0, pos[0] - k)
								+ str.substring(pos[1] - k);
						if (list == null)
							list = new ArrayList<int[]>(4);
						list.add(pos);
					} else if (pos[1] <= k) {
						break;
					}
				}
				int j = str.lastIndexOf(text);
				if (j >= 0) {
					j += k;
					if (list != null) {
						List<int[]> ranges = new ArrayList<int[]>(4);
						int m = j;
						int n = text.length();
						for (int i = list.size() - 1; i >= 0; i--) {
							int[] pos = list.get(i);
							if (m < pos[0]) {
								if (j + n > pos[0]) {
									ranges.add(new int[] { m, pos[0] });
									m = pos[1];
									n += pos[1] - pos[0];
								} else {
									break;
								}
							} else {
								j += pos[1] - pos[0];
								m += pos[1] - pos[0];
							}
						}
						if (j + n > m)
							ranges.add(new int[] { m, j + n });
						if (logger.isDebugEnabled()) {
							for (int[] pos : ranges) {
								logger.debug("cut "
										+ pos[0]
										+ "-"
										+ pos[1]
										+ ": "
										+ styledText.getTextRange(pos[0],
												pos[1] - pos[0]));
							}
						}
						for (int i = ranges.size() - 1; i >= 0; i--) {
							int[] pos = ranges.get(i);
							cutText(pos[0], pos[1]);
						}
					} else {
						styledText.replaceTextRange(j, text.length(), "");
					}
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn("text not match in:\n" + str);
					}
				}
			}
		});
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

	private static Color getColor(Device device, int[] rgb) {
		int key = (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
		Color color = COLORS.get(key);
		if (color == null) {
			color = new Color(device, rgb[0], rgb[1], rgb[2]);
			COLORS.put(key, color);
		}
		return color;
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
					send(shortKey.getCommand(), true);
					event.doit = false;
				}
				break;
			}
		}
	}

	private void doCommand() {
		final String command = commandInput.getText();
		if (command != null && command.length() > 0
				&& (commands.isEmpty() || !command.equals(commands.getLast()))) {
			commandInput.selectAll();
			commands.addLast(command);
			if (commands.size() > COMMAND_HISTORY_SIZE)
				commands.removeFirst();
			cmdptr = commands.size();
		}
		context.executeScript(command, null);
	}

	@Override
	public void send(String command, boolean echo) {
		command += "\n";
		if (echo)
			echo(command, SGR.ECHO);
		if (isConnected()) {
			client.write(command.getBytes(charset));
		}
	}

	@Override
	public void runOnUiThread(final Runnable runnable) {
		if (context != null && !display.isDisposed()) {
			display.syncExec(new Runnable() {
				@Override
				public void run() {
					if (context != null && !display.isDisposed())
						runnable.run();
				}
			});
		}
	}

	/**
	 * 返回上下文。
	 * 
	 * @return 上下文
	 */
	public Context getContext() {
		return context;
	}
}
