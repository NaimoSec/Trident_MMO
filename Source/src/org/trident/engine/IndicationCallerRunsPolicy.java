package org.trident.engine;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.logging.Logger;

/**
 * A handler for rejected tasks that runs the rejected task directly in the
 * calling thread of the <code>execute()</code> method, unless the executor has
 * been shut down, in which case the task is discarded. The difference between
 * this handler and {@link CallerRunsPolicy} is that this will print off an
 * indication of what happened.
 * 
 * @author lare96
 */
public class IndicationCallerRunsPolicy extends CallerRunsPolicy {

    /** The logger for printing information. */
    private static Logger logger = Logger.getLogger(IndicationCallerRunsPolicy.class.getSimpleName());

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        super.rejectedExecution(r, e);
        logger.warning(e.isShutdown() ? "Task discarded by thread pool: " + e.toString()
            : "Task executed on calling thread by thread pool: " + e.toString());
    }
}