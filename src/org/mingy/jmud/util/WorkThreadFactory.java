package org.mingy.jmud.util;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 工作线程的工厂类，可以用于判断一个线程是否是工作线程。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class WorkThreadFactory implements ThreadFactory {

	private static final AtomicInteger poolNumber = new AtomicInteger(1);
	private static final Map<Thread, Long> workThreads = new WeakHashMap<Thread, Long>();
	private static ThreadFactory instance;
	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;

	public WorkThreadFactory() {
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
				.getThreadGroup();
		namePrefix = "pool-" + poolNumber.getAndIncrement() + "-work-thread-";
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r, namePrefix
				+ threadNumber.getAndIncrement(), 0);
		if (t.isDaemon())
			t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY)
			t.setPriority(Thread.NORM_PRIORITY);
		workThreads.put(t, t.getId());
		return t;
	}

	/**
	 * 取得单例的线程工厂实现。
	 * 
	 * @return 线程工厂类
	 */
	public static ThreadFactory getInstance() {
		if (instance == null) {
			synchronized (WorkThreadFactory.class) {
				if (instance == null) {
					instance = new WorkThreadFactory();
				}
			}
		}
		return instance;
	}

	/**
	 * 判断当前是否在工作线程内。
	 * 
	 * @return true为是工作线程
	 */
	public static final boolean inWorkThread() {
		Thread t = Thread.currentThread();
		Long id = workThreads.get(t);
		return id != null && id.equals(t.getId());
	}
}
