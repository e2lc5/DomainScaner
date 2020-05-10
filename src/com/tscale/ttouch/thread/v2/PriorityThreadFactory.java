package com.tscale.ttouch.thread.v2;

import com.tscale.ttouch.thread.v2.executor.PriorityPoolExecutor;
import com.tscale.ttouch.thread.v2.runnable.PriorityThread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * PriorityThread
 */
public class PriorityThreadFactory {
    private final PriorityPoolExecutor executor;        // 鍥哄畾澶у皬鐨勭嚎绋嬫睜
    private final ExecutorService singleThreadExecutor;
    private final ScheduledExecutorService scheduledThreadPool;

    private Map<Long, ScheduledFuture> futureMap = new HashMap<Long, ScheduledFuture>();// 鐢ㄤ簬缂撳瓨Scheduled浜嬩欢

    private static PriorityThreadFactory factory = null;

    private PriorityThreadFactory() {
        //		NCPU = CPU鐨勬暟閲�
        //		UCPU = 鏈熸湜瀵笴PU鐨勪娇鐢ㄧ巼 0 鈮� UCPU 鈮� 1
        //		W/C = 绛夊緟鏃堕棿涓庤绠楁椂闂寸殑姣旂巼
        //		濡傛灉甯屾湜澶勭悊鍣ㄨ揪鍒扮悊鎯崇殑浣跨敤鐜囷紝閭ｄ箞绾跨▼姹犵殑鏈�浼樺ぇ灏忎负锛�
        //		绾跨▼姹犲ぇ灏�=NCPU *UCPU(1+W/C)
        //		int ncpus = Runtime.getRuntime().availableProcessors();
        //		corePoolSize = (int) (ncpus * 0.5);

        int cpuCount = Runtime.getRuntime().availableProcessors();
        int corePoolSize = cpuCount * 2 + 1;
        int maximumPoolSize = cpuCount * 4 + 1;
        long keepAlive = 10L;
        executor = new PriorityPoolExecutor(corePoolSize, maximumPoolSize,
                keepAlive, TimeUnit.MILLISECONDS);

        singleThreadExecutor = Executors.newSingleThreadExecutor();

        scheduledThreadPool = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    }

    /**
     * 鍗曚緥妯″紡,鑾峰彇绾跨▼姹�
     *
     * @return
     */
    public static PriorityThreadFactory getPool() {
        if (factory == null) {
            factory = new PriorityThreadFactory();
        }
        return factory;
    }

    public String getPoolInfo() {
        StringBuilder info = new StringBuilder();
        info.append("褰撳墠鎺掗槦鏁�: ").append(this.executor.getQueue().size());
        info.append(" 褰撳墠娲诲姩鏁�: ").append(this.executor.getActiveCount());
        info.append(" 鎵ц瀹屾垚鏁�: ").append(this.executor.getCompletedTaskCount());
        info.append(" 鎬绘暟: ").append(this.executor.getTaskCount());
        info.append(" CorePoolSize: ").append(this.executor.getCorePoolSize());
        info.append(" PoolSize: ").append(this.executor.getPoolSize());
        info.append(" LargestPoolSize: ").append(this.executor.getLargestPoolSize());
        info.append(" MaximumPoolSize: ").append(this.executor.getMaximumPoolSize());
        BlockingQueue<Runnable> queue = executor.getQueue();
        return info.toString();
    }

    /**
     * 鍥哄畾澶у皬鐨勭嚎绋嬫睜鎵ц绾跨▼
     *
     * @param command
     */
    public void execute(PriorityThread command) {
        this.executor.execute(command);
    }

    /**
     * 瀹氭椂绾跨▼姹犳墽琛岀嚎绋�</>
     * 浠ュ浐瀹氱殑棰戠巼鍘绘墽琛屼换鍔★紝鍛ㄦ湡鏄寚姣忔鎵ц浠诲姟鎴愬姛鎵ц涔嬮棿鐨勯棿闅斻��
     *
     * @param command
     * @param delay   绛夊緟鏃堕棿
     * @param period  寰幆鍛ㄦ湡
     */
    public long scheduleAtFixedRate(PriorityThread command, long delay, long period) {
        ScheduledFuture future = this.scheduledThreadPool.scheduleAtFixedRate(command, delay, period, TimeUnit.MILLISECONDS);
        long id = System.currentTimeMillis();
        this.futureMap.put(id, future);
        // Timber.i("thread " + command.getThreadName() + " id " + id + " start");
        return id;
    }

    /**
     * 瀹氭椂绾跨▼姹犳墽琛岀嚎绋�,涓嶅惊鐜�
     *
     * @param command
     * @param delay   绛夊緟鏃堕棿
     * @return
     */
    public ScheduledFuture schedule(PriorityThread command, long delay) {
        return this.scheduledThreadPool.schedule(command, delay, TimeUnit.MILLISECONDS);
    }

    public void cancelSchedule(long schedule_id) {
        ScheduledFuture future = this.futureMap.get(schedule_id);
        if (future != null && !future.isCancelled()) {
            future.cancel(true);
            // Timber.i("thread " + future.toString() + " id " + schedule_id + " canceled");
        }
        this.futureMap.remove(schedule_id);
    }

    public void cancelSchedule(long schedule_id, String threadName) {
        ScheduledFuture future = this.futureMap.get(schedule_id);
        if (future != null && !future.isCancelled()) {
            future.cancel(true);
            // Timber.i("thread " + threadName + " id " + schedule_id + " canceled");
        }
        this.futureMap.remove(schedule_id);
    }


    /**
     * 鍗曚釜绾跨▼绾跨▼姹犳墽琛岀嚎绋�
     *
     * @param command
     */
    public void singleExecute(PriorityThread command) {
        this.singleThreadExecutor.execute(command);
    }
}
