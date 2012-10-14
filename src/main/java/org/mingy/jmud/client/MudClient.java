package org.mingy.jmud.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	private static final String DEFAULT_FONT_FAMILY = "宋体";
	/** 默认的字体大小 */
	private static final int DEFAULT_FONT_SIZE = 16;
	/** 命令行历史记录数 */
	private static final int COMMAND_HISTORY_SIZE = 20;
	/** TAB转换成空格 */
	private static final byte[] TAB = "        ".getBytes();

	/** 主机名 */
	private String hostname;
	/** 端口 */
	private int port;
	/** 连接超时（毫秒） */
	private int connectTimeout;
	/** 字符集 */
	private Charset charset;
	/** 主显示区域（文本） */
	private JTextPane textPane;
	/** 主显示区域（滚动） */
	private JScrollPane scrollPane;
	/** 纵向滚动条 */
	private JScrollBar scrollBar;
	/** 滚动时的辅助显示区域（文本） */
	private JTextPane textPane0;
	/** 滚动时的辅助显示区域（滚动） */
	private JScrollPane scrollPane0;
	/** 命令行输入框 */
	private JTextField commandInput;
	/** 多样式的文档 */
	private StyledDocument document;
	/** 默认的SGR */
	private SGR defaultSGR;
	/** 当前的SGR */
	private SGR currentSGR;
	/** 默认的样式 */
	private Style defaultStyle;
	/** 当前的样式 */
	private Style currentStyle;
	/** Telnet客户端 */
	private TelnetClient client;
	/** 命令行历史记录 */
	private LinkedList<String> commands;
	/** 命令行历史选择时的指针 */
	private int cmdptr;
	/** 滚动时开启辅助显示区域，此时该值为true */
	private boolean scrollLocked;
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
	 * @param textPane
	 *            主显示区域（文本）
	 * @param scrollPane
	 *            主显示区域（滚动）
	 * @param scrollBar
	 *            纵向滚动条
	 * @param textPane0
	 *            滚动时的辅助显示区域（文本）
	 * @param scrollPane0
	 *            滚动时的辅助显示区域（滚动）
	 * @param commandInput
	 *            命令行输入框
	 */
	public MudClient(String hostname, int port, int connectTimeout,
			String charset, JTextPane textPane, JScrollPane scrollPane,
			JScrollBar scrollBar, JTextPane textPane0, JScrollPane scrollPane0,
			JTextField commandInput) {
		this.hostname = hostname;
		this.port = port;
		this.connectTimeout = connectTimeout;
		this.charset = Charset.forName(charset);
		this.textPane = textPane;
		this.scrollPane = scrollPane;
		this.scrollBar = scrollBar;
		this.textPane0 = textPane0;
		this.scrollPane0 = scrollPane0;
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
		initSGR();
		echo("Connected to " + hostname + ":" + port + "\n", SGR.INFO);
	}

	public void onDisconnected() {
		// TODO: 重连？
	}

	public void onReceived(byte[] data) {
		try {
			processLine(data);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(e);
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
		document = textPane.getStyledDocument();
		final ScrollListener scrollListener = new ScrollListener();
		scrollPane.getParent().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent event) {
				if (scrollLocked) {
					unlockScroll();
				}
				scrollToEnd();
			}
		});
		syncScroll(scrollPane);
		scrollPane.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {
					public void adjustmentValueChanged(AdjustmentEvent event) {
						if (!scrollLocked)
							syncScroll(scrollPane);
					}
				});
		textPane.addKeyListener(scrollListener);
		textPane.addMouseWheelListener(scrollListener);
		scrollPane0.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {
					public void adjustmentValueChanged(AdjustmentEvent event) {
						if (scrollLocked)
							syncScroll(scrollPane0);
					}
				});
		textPane0.addKeyListener(scrollListener);
		textPane0.addMouseWheelListener(scrollListener);
		scrollBar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent event) {
				// lockScroll();
			}
		});
		commandInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String command = event.getActionCommand();
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
			}
		});
		commandInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				switch (event.getKeyCode()) {
				case KeyEvent.VK_UP:
					if (cmdptr > 0) {
						cmdptr--;
						commandInput.setText(commands.get(cmdptr));
					} else {
						cmdptr = -1;
						commandInput.setText(null);
					}
					event.consume();
					break;
				case KeyEvent.VK_DOWN:
					if (cmdptr < commands.size() - 1) {
						cmdptr++;
						commandInput.setText(commands.get(cmdptr));
					} else {
						cmdptr = commands.size();
						commandInput.setText(null);
					}
					event.consume();
					break;
				case KeyEvent.VK_PAGE_UP:
				case KeyEvent.VK_PAGE_DOWN:
					scrollListener.keyPressed(event);
					break;
				}
			}
		});
		commands = new LinkedList<String>();
	}

	/**
	 * 监听并处理PageUp、PageDown和鼠标的滚卷事件。
	 */
	class ScrollListener extends KeyAdapter implements MouseWheelListener {

		@Override
		public void keyPressed(KeyEvent event) {
			switch (event.getKeyCode()) {
			case KeyEvent.VK_PAGE_UP:
				scrollPageUp();
				event.consume();
				break;
			case KeyEvent.VK_PAGE_DOWN:
				scrollPageDown();
				event.consume();
				break;
			}
		}

		public void mouseWheelMoved(MouseWheelEvent event) {
			if (event.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
				int i = event.getUnitsToScroll();
				if (i < 0)
					scrollUp(-i);
				else
					scrollDown(i);
				event.consume();
			}
		}
	}

	private void syncScroll(JScrollPane scrollPane) {
		JScrollBar sb = scrollPane.getVerticalScrollBar();
		scrollBar.setMinimum(sb.getMinimum());
		scrollBar.setMaximum(sb.getMaximum());
		scrollBar.setVisibleAmount(sb.getVisibleAmount());
		scrollBar.setValue(sb.getValue());
	}

	private void lockScroll(final int units) {
		if (scrollBar.getVisibleAmount() >= scrollBar.getMaximum())
			return;
		scrollLocked = true;
		if (!scrollPane0.isVisible()) {
			scrollPane0.setVisible(true);
			scrollPane0.getParent().doLayout();
			scrollPane0.doLayout();
			scrollPane.doLayout();
			syncScroll(scrollPane0);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					int height = textPane.getHeight();
					Rectangle rect = textPane0.getVisibleRect();
					rect.y = height
							- rect.height
							- (units < 0 ? rect.height : units
									* scrollPane0.getVerticalScrollBar()
											.getUnitIncrement(-1));
					if (rect.y < 0)
						rect.y = 0;
					textPane0.scrollRectToVisible(rect);
					rect = textPane.getVisibleRect();
					rect.y = height - rect.height;
					if (rect.y < 0)
						rect.y = 0;
					textPane.scrollRectToVisible(rect);
				}
			});
		}
	}

	private void unlockScroll() {
		scrollLocked = false;
		if (scrollPane0.isVisible()) {
			scrollPane0.setVisible(false);
			scrollPane0.getParent().doLayout();
			scrollPane.doLayout();
			syncScroll(scrollPane);
		}
	}

	private void scrollPageUp() {
		if (logger.isTraceEnabled()) {
			logger.trace("scroll page up");
		}
		if (!scrollLocked) {
			lockScroll(-1);
		} else {
			Rectangle rect = textPane0.getVisibleRect();
			if (rect.y > 0) {
				rect.y -= rect.height;
				if (rect.y < 0)
					rect.y = 0;
				textPane0.scrollRectToVisible(rect);
			}
		}
	}

	private void scrollPageDown() {
		if (logger.isTraceEnabled()) {
			logger.trace("scroll page down");
		}
		if (scrollLocked) {
			Rectangle rect = textPane0.getVisibleRect();
			rect.y += rect.height;
			if (rect.y + rect.height < textPane0.getHeight()) {
				textPane0.scrollRectToVisible(rect);
			} else {
				unlockScroll();
			}
		}
	}

	private void scrollUp(int units) {
		if (logger.isTraceEnabled()) {
			logger.trace("scroll up: " + units);
		}
		if (!scrollLocked) {
			lockScroll(units);
		} else {
			Rectangle rect = textPane0.getVisibleRect();
			if (rect.y > 0) {
				rect.y -= scrollPane0.getVerticalScrollBar().getUnitIncrement(
						-1)
						* units;
				if (rect.y < 0)
					rect.y = 0;
				textPane0.scrollRectToVisible(rect);
			}
		}
	}

	private void scrollDown(int units) {
		if (logger.isTraceEnabled()) {
			logger.trace("scroll down: " + units);
		}
		if (scrollLocked) {
			Rectangle rect = textPane0.getVisibleRect();
			rect.y += scrollPane0.getVerticalScrollBar().getUnitIncrement(1)
					* units;
			if (rect.y + rect.height < textPane0.getHeight()) {
				textPane0.scrollRectToVisible(rect);
			} else {
				unlockScroll();
			}
		}
	}

	private void scrollToEnd() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Rectangle rect = textPane.getVisibleRect();
				rect.y = textPane.getHeight() - rect.height;
				if (rect.y < 0)
					rect.y = 0;
				textPane.scrollRectToVisible(rect);
			}
		});
	}

	private void initSGR() {
		defaultSGR = new SGR(SGR.DEFAULT);
		defaultStyle = document.addStyle(defaultSGR.getStyle(),
				document.getStyle("default"));
		StyleConstants.setBold(defaultStyle, defaultSGR.isBlink()); // 粗体
		StyleConstants.setItalic(defaultStyle, defaultSGR.isItalic()); // 斜体
		StyleConstants.setUnderline(defaultStyle, defaultSGR.isUnderline()); // 下划线
		StyleConstants.setBackground(defaultStyle,
				defaultSGR.getBackgroundColor()); // 背景色
		StyleConstants.setForeground(defaultStyle, defaultSGR.getTextColor()); // 颜色
		textPane.setBackground(defaultSGR.getBackgroundColor());
		textPane0.setBackground(defaultSGR.getBackgroundColor());
		currentSGR = defaultSGR;
		currentStyle = defaultStyle;
	}

	private void changeSGR(String style) {
		currentSGR = new SGR(style, currentSGR);
		currentStyle = document.getStyle(style);
		if (currentStyle == null) {
			currentStyle = document.addStyle(style, defaultStyle);
			StyleConstants.setBold(currentStyle, currentSGR.isBlink()); // 粗体
			StyleConstants.setItalic(currentStyle, currentSGR.isItalic()); // 斜体
			StyleConstants.setUnderline(currentStyle, currentSGR.isUnderline()); // 下划线
			StyleConstants.setBackground(currentStyle,
					currentSGR.getBackgroundColor()); // 背景色
			StyleConstants.setForeground(currentStyle,
					currentSGR.getTextColor()); // 颜色
		}
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
				changeSGR(s);
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
		String line;
		List<Integer> list = new ArrayList<Integer>(8);
		for (int i = start; i < length; i++) {
			if (bytes[i] == 9)
				list.add(i);
		}
		if (!list.isEmpty()) {
			byte[] tmp = new byte[length + list.size() * 7];
			int n = start, p = 0, l = 0;
			for (int i : list) {
				l = i - n;
				if (l > 0)
					System.arraycopy(bytes, n, tmp, p, l);
				n += i + 1;
				p += l;
				int k = 8 - (p & 7);
				System.arraycopy(TAB, 0, tmp, p, k);
				p += k;
			}
			l = start + length - n;
			if (l > 0) {
				System.arraycopy(bytes, n, tmp, p, l);
				p += l;
			}
			line = new String(tmp, 0, p, charset);
		} else {
			line = new String(bytes, start, length, charset);
		}
		if (!echoForbidden) {
			try {
				if (logger.isTraceEnabled()) {
					logger.trace(currentStyle);
					logger.trace(line);
				}
				document.insertString(document.getLength(), line, currentStyle);
			} catch (BadLocationException e) {
				if (logger.isErrorEnabled()) {
					logger.error(e);
				}
			}
			scrollToEnd();
		}
	}

	private void echo(String message, String sgrString) {
		SGR sgr = currentSGR;
		Style style = currentStyle;
		changeSGR(sgrString);
		try {
			document.insertString(document.getLength(), message, currentStyle);
		} catch (BadLocationException e) {
			if (logger.isErrorEnabled()) {
				logger.error(e);
			}
		}
		currentSGR = sgr;
		currentStyle = style;
		scrollToEnd();
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
		JFrame frame = new JFrame();
		StyledDocument doc = new DefaultStyledDocument();
		JTextPane textPane = new JTextPane(doc);
		textPane.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textPane,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JTextField commandInput = new JTextField();
		JScrollBar scrollBar = new JScrollBar();
		JTextPane textPane0 = new JTextPane(doc);
		textPane0.setEditable(false);
		final JScrollPane scrollPane0 = new JScrollPane(textPane0,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 2;
		panel.add(scrollPane0, c);
		c.gridy = 1;
		c.weighty = 1;
		panel.add(scrollPane, c);
		Style style = doc.addStyle("default", null);
		StyleConstants.setFontFamily(style, DEFAULT_FONT_FAMILY); // 字体
		StyleConstants.setFontSize(style, DEFAULT_FONT_SIZE); // 大小
		commandInput.setFont(new Font(DEFAULT_FONT_FAMILY, Font.PLAIN,
				DEFAULT_FONT_SIZE));
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.getContentPane().add(scrollBar, BorderLayout.EAST);
		frame.getContentPane().add(commandInput, BorderLayout.SOUTH);
		frame.pack();
		commandInput.requestFocusInWindow();
		frame.setSize(new Dimension(800, 600));
		frame.setTitle("Mud Client");
		frame.setLocation(new Point(200, 200));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final MudClient mc = new MudClient("pkuxkx.net", 5555, 10000, "GBK",
				textPane, scrollPane, scrollBar, textPane0, scrollPane0,
				commandInput);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent event) {
				try {
					scrollPane0.setVisible(false);
					panel.doLayout();
					mc.connect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void windowClosing(WindowEvent event) {
				try {
					mc.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		frame.setVisible(true);
	}
}
