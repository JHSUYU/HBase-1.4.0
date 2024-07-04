//package org.apache.hadoop.hbase.ipc;
//
//import com.google.protobuf.Descriptors;
//import org.apache.hadoop.hbase.Abortable;
//import java.util.Map;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.atomic.AtomicInteger;
//import org.apache.hadoop.hbase.monitoring.MonitoredRPCHandler;
//import org.apache.hadoop.util.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


//public class Handler extends Thread {
//
//    /**
//     * Q to find CallRunners to run in.
//     */
//    final BlockingQueue<CallRunner> q;
//
//    int handlerCount;
//    final double handlerFailureThreshhold;
//
//    // metrics (shared with other handlers)
//    final AtomicInteger activeHandlerCount;
//    AtomicInteger failedHandlerCount;
//
//    // The up-level RpcServer.
//    Abortable abortable;
//
//    private boolean running;
//
//    private static final Logger LOG = LoggerFactory.getLogger(Handler.class);
//
//    Handler(final String name, final double handlerFailureThreshhold,
//            final BlockingQueue<CallRunner> q, final AtomicInteger activeHandlerCount) {
//        super(name);
//        setDaemon(true);
//        this.q = q;
//        this.handlerFailureThreshhold = handlerFailureThreshhold;
//        this.activeHandlerCount = activeHandlerCount;
//    }
//
//    Handler(final String name, final double handlerFailureThreshhold, final int handlerCount,
//            final BlockingQueue<CallRunner> q, final AtomicInteger activeHandlerCount,
//            final AtomicInteger failedHandlerCount, final Abortable abortable) {
//        super(name);
//        setDaemon(true);
//        this.q = q;
//        this.handlerFailureThreshhold = handlerFailureThreshhold;
//        this.activeHandlerCount = activeHandlerCount;
//        this.failedHandlerCount = failedHandlerCount;
//        this.handlerCount = handlerCount;
//        this.abortable = abortable;
//    }
//
//    protected CallRunner getCallRunner() throws InterruptedException {
//        return this.q.take();
//    }
//
//    public void stopRunning() {
//        running = false;
//    }
//
//    @Override
//    public void run() {
//        boolean interrupted = false;
//        running = true;
//        try {
//            while (running) {
//                try {
//                    run(getCallRunner());
//                    //runChecker(getCallRunner());
//                } catch (InterruptedException e) {
//                    interrupted = true;
//                }
//            }
//        } catch (Exception e) {
//            LOG.warn(e.toString(), e);
//            throw e;
//        } finally {
//            if (interrupted) {
//                Thread.currentThread().interrupt();
//            }
//        }
//    }
//
//    public static boolean isDryRun(CallRunner cr){
//        Map<Descriptors.FieldDescriptor, Object> fields = cr.getCall().param.getAllFields();
//        for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : fields.entrySet()) {
//            Descriptors.FieldDescriptor field = entry.getKey();
//            Object value = entry.getValue();
//            if (field.getName().equals("is_retry") && value.equals(true)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//
//    public void run$Checker(CallRunner cr){
//        if (isDryRun(cr)){
//            Handler shadowHandler = this;
//            CallRunner shadowCallRunner = cr;
//            shadowHandler.run(shadowCallRunner);
//        }else{
//            run(cr);
//        }
//    }
//
//
//
//
//
//    public void run(CallRunner cr) {
//        MonitoredRPCHandler status = RpcServer.getStatus();
//        cr.setStatus(status);
//        try {
//            this.activeHandlerCount.incrementAndGet();
//            cr.run$Checker();
//        } catch (Throwable e) {
//            if (e instanceof Error) {
//                int failedCount = failedHandlerCount.incrementAndGet();
//                if (this.handlerFailureThreshhold >= 0
//                        && failedCount > handlerCount * this.handlerFailureThreshhold) {
//                    String message = "Number of failed RpcServer handler runs exceeded threshhold "
//                            + this.handlerFailureThreshhold + "; reason: " + StringUtils.stringifyException(e);
//                    if (abortable != null) {
//                        abortable.abort(message, e);
//                    } else {
//                        LOG.error("Error but can't abort because abortable is null: "
//                                + StringUtils.stringifyException(e));
//                        throw e;
//                    }
//                } else {
//                    LOG.warn("Handler errors " + StringUtils.stringifyException(e));
//                }
//            } else {
//                LOG.warn("Handler  exception " + StringUtils.stringifyException(e));
//            }
//        } finally {
//            this.activeHandlerCount.decrementAndGet();
//        }
//    }
//
//
//}

//public class Handler extends Thread {
//
//    /**
//     * Q to find CallRunners to run in.
//     */
//    final BlockingQueue<CallRunner> q;
//
//    int handlerCount;
//    final double handlerFailureThreshhold;
//
//    // metrics (shared with other handlers)
//    final AtomicInteger activeHandlerCount;
//    AtomicInteger failedHandlerCount;
//
//    // The up-level RpcServer.
//    Abortable abortable;
//
//    private boolean running;
//
//    private static final Logger LOG = LoggerFactory.getLogger(Handler.class);
//
//    Handler(final String name, final double handlerFailureThreshhold,
//            final BlockingQueue<CallRunner> q, final AtomicInteger activeHandlerCount) {
//        super(name);
//        setDaemon(true);
//        this.q = q;
//        this.handlerFailureThreshhold = handlerFailureThreshhold;
//        this.activeHandlerCount = activeHandlerCount;
//    }
//
//    Handler(final String name, final double handlerFailureThreshhold, final int handlerCount,
//            final BlockingQueue<CallRunner> q, final AtomicInteger activeHandlerCount,
//            final AtomicInteger failedHandlerCount, final Abortable abortable) {
//        super(name);
//        setDaemon(true);
//        this.q = q;
//        this.handlerFailureThreshhold = handlerFailureThreshhold;
//        this.activeHandlerCount = activeHandlerCount;
//        this.failedHandlerCount = failedHandlerCount;
//        this.handlerCount = handlerCount;
//        this.abortable = abortable;
//    }
//
//    protected CallRunner getCallRunner() throws InterruptedException {
//        return this.q.take();
//    }
//
//    public void stopRunning() {
//        running = false;
//    }
//
//    @Override
//    public void run() {
//        boolean interrupted = false;
//        running = true;
//        try {
//            while (running) {
//                try {
//                    run(getCallRunner());
//                    //runChecker(getCallRunner());
//                } catch (InterruptedException e) {
//                    interrupted = true;
//                }
//            }
//        } catch (Exception e) {
//            LOG.warn(e.toString(), e);
//            throw e;
//        } finally {
//            if (interrupted) {
//                Thread.currentThread().interrupt();
//            }
//        }
//    }
//
//    public void run(CallRunner cr) {
//        MonitoredRPCHandler status = RpcServer.getStatus();
//        cr.setStatus(status);
//        try {
//            this.activeHandlerCount.incrementAndGet();
//            cr.run();
//        } catch (Throwable e) {
//            if (e instanceof Error) {
//                int failedCount = failedHandlerCount.incrementAndGet();
//                if (this.handlerFailureThreshhold >= 0
//                        && failedCount > handlerCount * this.handlerFailureThreshhold) {
//                    String message = "Number of failed RpcServer handler runs exceeded threshhold "
//                            + this.handlerFailureThreshhold + "; reason: " + StringUtils.stringifyException(e);
//                    if (abortable != null) {
//                        abortable.abort(message, e);
//                    } else {
//                        LOG.error("Error but can't abort because abortable is null: "
//                                + StringUtils.stringifyException(e));
//                        throw e;
//                    }
//                } else {
//                    LOG.warn("Handler errors " + StringUtils.stringifyException(e));
//                }
//            } else {
//                LOG.warn("Handler  exception " + StringUtils.stringifyException(e));
//            }
//        } finally {
//            this.activeHandlerCount.decrementAndGet();
//        }
//    }
//
//    public static boolean isDryRun(CallRunner cr) {
//        Map<Descriptors.FieldDescriptor, Object> fields = cr.getCall().param.getAllFields();
//        for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : fields.entrySet()) {
//            Descriptors.FieldDescriptor field = entry.getKey();
//            Object value = entry.getValue();
//            if (field.getName().equals("is_retry") && value.equals(true)) {
//                return true;
//            }
//        }
//        return false;
//    }
//}


/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.protobuf.Descriptors$FieldDescriptor
 *  org.apache.hadoop.hbase.Abortable
 *  org.apache.hadoop.hbase.ipc.CallRunner
 *  org.apache.hadoop.hbase.ipc.RpcServer
 *  org.apache.hadoop.util.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hadoop.hbase.ipc;

import com.google.protobuf.Descriptors;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.hbase.Abortable;
import org.apache.hadoop.hbase.ipc.CallRunner;
import org.apache.hadoop.hbase.ipc.RpcServer;
import org.apache.hadoop.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Handler
        extends Thread {
    final BlockingQueue<CallRunner> q;
    int handlerCount;
    final double handlerFailureThreshhold;
    final AtomicInteger activeHandlerCount;
    AtomicInteger failedHandlerCount;
    Abortable abortable;
    private boolean running;
    private static final Logger LOG = LoggerFactory.getLogger(Handler.class);

    Handler(String string, double d, BlockingQueue<CallRunner> blockingQueue, AtomicInteger atomicInteger) {
        super(string);
        this.setDaemon(true);
        this.q = blockingQueue;
        this.handlerFailureThreshhold = d;
        this.activeHandlerCount = atomicInteger;
    }

    Handler(String string, double d, int n, BlockingQueue<CallRunner> blockingQueue, AtomicInteger atomicInteger, AtomicInteger atomicInteger2, Abortable abortable) {
        super(string);
        this.setDaemon(true);
        this.q = blockingQueue;
        this.handlerFailureThreshhold = d;
        this.activeHandlerCount = atomicInteger;
        this.failedHandlerCount = atomicInteger2;
        this.handlerCount = n;
        this.abortable = abortable;
    }

    protected CallRunner getCallRunner() throws InterruptedException {
        return this.q.take();
    }

    public static boolean isDryRun(CallRunner object) {
        for (Map.Entry entry : object.getCall().param.getAllFields().entrySet()) {
            Descriptors.FieldDescriptor fieldDescriptor = (Descriptors.FieldDescriptor)entry.getKey();
            Object v = entry.getValue();
            if (!fieldDescriptor.getName().equals("is_retry") || !v.equals(true)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        boolean bl = false;
        boolean bl2 = false;
        this.running = true;
        try {
            while (this.running) {
                try {
                    this.run$Checker(this.getCallRunner());
                }
                catch (InterruptedException interruptedException) {
                    bl = true;
                    bl2 = true;
                }
            }
        }
        catch (Exception exception) {
            LOG.warn(exception.toString(), (Throwable)exception);
            throw exception;
        }
        finally {
            if (bl) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void run(CallRunner callRunner) {
        callRunner.setStatus(RpcServer.getStatus());
        try {
            this.activeHandlerCount.incrementAndGet();
            callRunner.run$Checker();
            this.activeHandlerCount.decrementAndGet();
            return;
        }
        catch (Throwable throwable) {
            try {
                if (throwable instanceof Error) {
                    int n = this.failedHandlerCount.incrementAndGet();
                    if (this.handlerFailureThreshhold >= 0.0 && (double)n > (double)this.handlerCount * this.handlerFailureThreshhold) {
                        String string = "Number of failed RpcServer handler runs exceeded threshhold " + this.handlerFailureThreshhold + "; reason: " + StringUtils.stringifyException((Throwable)throwable);
                        if (this.abortable != null) {
                            this.abortable.abort(string, throwable);
                            return;
                        }
                        LOG.error("Error but can't abort because abortable is null: " + StringUtils.stringifyException((Throwable)throwable));
                        throw throwable;
                    }
                    LOG.warn("Handler errors " + StringUtils.stringifyException((Throwable)throwable));
                    return;
                }
                LOG.warn("Handler  exception " + StringUtils.stringifyException((Throwable)throwable));
                return;
            }
            finally {
                this.activeHandlerCount.decrementAndGet();
            }
        }
    }

    public void run$Checker(CallRunner callRunner) {
        if (this.isDryRun(callRunner)) {
            this.run(callRunner);
        } else {
            this.run(callRunner);
        }
    }

    public void stopRunning() {
        this.running = false;
    }
}
