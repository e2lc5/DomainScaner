package com.tscale.ttouch.thread.v2.runnable;

/**
 * 浼樺厛绾х嚎绋�
 *
 * @author fish
 */
public abstract class PriorityThread extends Thread {

    public PriorityThread() {
        super();
        setName(getThreadName());
    }

    public PriorityThread(int priority) {
        super();
        setName(getThreadName());
        setPriority(priority);
        // Timber.i("绾跨▼鍚姩 " + this.toString());
    }

    @Override
    public abstract void run();

    public abstract String getThreadName();

}
