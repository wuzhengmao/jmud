package org.mingy.jmud.client;

import static org.jboss.netty.channel.Channels.pipeline;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;

/**
 * Telnet客户端实现，使用Netty框架。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class TelnetClient {

	/** 日志 */
	private static final Log logger = LogFactory.getLog(TelnetClient.class);

	/** 关闭状态 */
	public static int STATE_CLOSED = 0;
	/** 连接中状态 */
	public static int STATE_CONNECTING = 1;
	/** 已连接状态 */
	public static int STATE_CONNECTED = 2;
	/** 正在关闭状态 */
	public static int STATE_CLOSING = 3;

	/** 主机名 */
	private String hostname;
	/** 端口 */
	private int port;
	/** TCP客户端 */
	private ClientBootstrap bootstrap;
	/** NIO通道 */
	private Channel channel;
	/** 最后一次发送数据的结果 */
	private ChannelFuture writeFuture;
	/** 监听器 */
	private TelnetClientListener listener;
	/** 状态 */
	private int state = STATE_CLOSED;

	/**
	 * 构造器。
	 * 
	 * @param hostname
	 *            主机名或IP
	 * @param port
	 *            端口
	 * @param listener
	 *            监听器
	 */
	public TelnetClient(String hostname, int port, TelnetClientListener listener) {
		this.hostname = hostname;
		this.port = port;
		this.listener = listener;
	}

	/**
	 * 开始连接。
	 * <p>
	 * 异步操作，连接成功后会回调{@link TelnetClientListener#onConnected()}方法。
	 * </p>
	 * 
	 * @param timeout
	 *            连接超时（毫秒）
	 */
	public void connect(int timeout) {
		if (state != STATE_CLOSED)
			throw new IllegalStateException("client state: " + state);
		state = STATE_CONNECTING;
		bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = pipeline();
				pipeline.addLast("decoder", new ByteArrayDecoder());
				pipeline.addLast("handler", new TelnetClientHandler());
				return pipeline;
			}
		});
		bootstrap.setOption("connectTimeoutMillis", timeout);
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(
				hostname, port));
		channel = future.awaitUninterruptibly().getChannel();
		if (!future.isSuccess()) {
			if (logger.isErrorEnabled()) {
				logger.error("failed to connect " + hostname + ":" + port,
						future.getCause());
			}
			close();
		}
	}

	/**
	 * 断开连接并关闭客户端。
	 * <p>
	 * 异步操作，连接断开后会回调{@link TelnetClientListener#onDisconnected()}方法。
	 * </p>
	 * 
	 * @param timeout
	 *            连接超时（毫秒）
	 */
	public void close() {
		closeInternal();
		if (bootstrap != null) {
			bootstrap.releaseExternalResources();
			bootstrap = null;
		}
	}

	/**
	 * 返回客户端的状态。
	 * 
	 * @return 状态码
	 */
	public int getState() {
		return state;
	}

	/**
	 * 是否连接已建立。
	 * 
	 * @return true是才能收发数据
	 */
	public boolean isAvailable() {
		return state == STATE_CONNECTED;
	}

	private boolean closeInternal() {
		if (state == STATE_CLOSING || state == STATE_CLOSED)
			return false;
		state = STATE_CLOSING;
		onDisconnected();
		if (writeFuture != null) {
			writeFuture.awaitUninterruptibly();
			writeFuture = null;
		}
		if (channel != null) {
			channel.close().awaitUninterruptibly();
			channel = null;
		}
		state = STATE_CLOSED;
		return true;
	}

	private void onConnected() {
		state = STATE_CONNECTED;
		if (listener != null)
			listener.onConnected();
	}

	private void onDisconnected() {
		if (listener != null)
			listener.onDisconnected();
	}

	private void onReceived(byte[] data) {
		if (listener != null)
			listener.onReceived(data);
	}

	/**
	 * 发送一个字节。
	 * 
	 * @param b
	 *            字节
	 */
	public void write(int b) {
		if (state != STATE_CONNECTED)
			throw new IllegalStateException("client state: " + state);
		ChannelBuffer buffer = ChannelBuffers.buffer(1);
		buffer.writeByte(b);
		writeFuture = channel.write(buffer);
	}

	/**
	 * 发送多个字节。
	 * 
	 * @param bytes
	 *            字节数组
	 */
	public void write(byte[] bytes) {
		if (state != STATE_CONNECTED)
			throw new IllegalStateException("client state: " + state);
		ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(bytes);
		writeFuture = channel.write(buffer);
	}

	/**
	 * 解码器，返回字节数组。
	 */
	class ByteArrayDecoder extends ReplayingDecoder<VoidEnum> {

		/**
		 * 解码完成以下功能：
		 * <p>
		 * <li>解码Telnet命令</li>
		 * <li>保证SGR参数不会被打断</li>
		 * <li>每行结束返回一个数据帧</li>
		 * <li>读完后返回一个数据帧</li>
		 * </p>
		 */
		@Override
		protected Object decode(ChannelHandlerContext ctx, Channel channel,
				ChannelBuffer buffer, VoidEnum state) throws Exception {
			int i = buffer.readerIndex();
			while (buffer.writerIndex() > buffer.readerIndex()) {
				buffer.markReaderIndex();
				int b = buffer.readByte() & 0xff;
				if (b == 7) {
					if (buffer.readerIndex() - i > 1) {
						buffer.resetReaderIndex();
						break;
					}
					if (listener != null)
						listener.beep();
					return null;
				} else if (b == 255) {
					if (buffer.readerIndex() - i > 1) {
						buffer.resetReaderIndex();
						break;
					}
					b = buffer.readByte() & 0xff;
					if (b == 251) {
						b = buffer.readByte() & 0xff;
						if (b == 1) {
							if (listener != null)
								listener.echoOff();
						} else if (b == 86) {
							write(new byte[] { (byte) 255, (byte) 254,
									(byte) 86 });
						}
					} else if (b == 252) {
						b = buffer.readByte() & 0xff;
						if (b == 1) {
							if (listener != null)
								listener.echoOn();
						} else if (b == 86) {
							// ignore
						}
					} else if (b == 253) {
						b = buffer.readByte() & 0xff;
					} else if (b == 254) {
						b = buffer.readByte() & 0xff;
					}
					return null;
				} else if (b == 27) {
					b = buffer.readByte() & 0xff;
					if (b == 91) {
						while (true) {
							b = buffer.readByte() & 0xff;
							if (b == 109 || b == 103 || b == 110)
								break;
						}
					}
				} else if (b == 10) {
					break;
				}
			}
			int n = buffer.readerIndex() - i;
			return n > 0 ? buffer.copy(i, n).array() : null;
		}
	}

	/**
	 * 处理器，监听并处理TCP事件。
	 */
	class TelnetClientHandler extends SimpleChannelUpstreamHandler {

		@Override
		public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e)
				throws Exception {
			if (e instanceof ChannelStateEvent) {
				if (logger.isDebugEnabled()) {
					logger.debug(e.toString());
				}
			}
			super.handleUpstream(ctx, e);
		}

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
			byte[] data = (byte[]) e.getMessage();
			if (logger.isTraceEnabled()) {
				logger.trace("received " + data.length + " byte(s)");
			}
			onReceived(data);
		}

		@Override
		public void channelConnected(ChannelHandlerContext ctx,
				ChannelStateEvent e) throws Exception {
			if (logger.isInfoEnabled()) {
				logger.info("connected to " + hostname + ":" + port);
			}
			onConnected();
		}

		@Override
		public void channelDisconnected(ChannelHandlerContext ctx,
				ChannelStateEvent e) throws Exception {
			if (logger.isInfoEnabled()) {
				logger.info("lost connection of " + hostname + ":" + port);
			}
			closeInternal();
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
			if (logger.isWarnEnabled()) {
				logger.warn("unexpected exception from downstream",
						e.getCause());
			}
			closeInternal();
		}
	}
}
