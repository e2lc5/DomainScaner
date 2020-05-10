package com.tscale.ttouch.thread.v2.executor;

import com.tscale.ttouch.thread.v2.runnable.PriorityThread;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 处理优先线程的线程池
 *
 * @author fish
 */
public class PriorityPoolExecutor extends ThreadPoolExecutor {
    private static final PriorityComparator comparator = new PriorityComparator();

    public PriorityPoolExecutor(int corePoolSize, int maximumPoolSize,
                                long keepAliveTime, TimeUnit unit) {
        this(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                new PriorityBlockingQueue<Runnable>(maximumPoolSize, comparator));
    }

    public PriorityPoolExecutor(int corePoolSize, int maximumPoolSize,
                                long keepAliveTime, TimeUnit unit,
                                PriorityBlockingQueue<Runnable> priorityBlockingQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit,
                priorityBlockingQueue);
    }

}

/**
 * 实现匹配器
 *
 * @author fish
 */
class PriorityComparator implements Comparator<Runnable> {

    @Override
    public int compare(Runnable lhs, Runnable rhs) {
        if (lhs instanceof PriorityThread && rhs instanceof PriorityThread) {
            PriorityThread lpr = ((PriorityThread) lhs);
            PriorityThread rpr = ((PriorityThread) rhs);
            return rpr.getPriority() - lpr.getPriority();
        } else {
            return 0;
        }
    }
}
