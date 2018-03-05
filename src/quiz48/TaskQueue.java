/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vasya
 */
public final class TaskQueue {
    private static final Object sSynch = new Object();
    private static TaskQueue sQueue = null;
    
    public static TaskQueue instance() {
        synchronized(sSynch) {
            if(sQueue == null) { sQueue = new TaskQueue(); }
            return sQueue;
        }
    }
    
    private interface ExitThread extends Runnable { }
    
    private final LinkedList<Runnable> m_Queue = new LinkedList<>();
    private Thread m_Thread = null;
    private final Object m_StartLock = new Object(),
            m_StopLock = new Object();
    private boolean m_Running = false, m_QueueLock = false;
    private final Runnable m_TaskQueue = () -> {
        synchronized(m_StartLock) {
            m_Running = true;
            m_StartLock.notifyAll();
        }
                
        try {
            while(true) {
                Runnable currentTask = null;
                
                synchronized(m_Queue) {
                    currentTask = m_Queue.pollFirst();
                }
                
                if(currentTask != null) {
                    boolean exit = (currentTask instanceof ExitThread);
                    currentTask.run();
                    if(exit) { break; }
                }
                else {
                    try {
                        synchronized(m_Queue) { m_Queue.wait(); }
                    }
                    catch(InterruptedException ex) { 
                        break;
                    }
                }
            }
        }
        finally {
            synchronized(m_StopLock) {
                synchronized(m_StartLock) { m_Running = false; }
                
                m_StopLock.notifyAll();
            }
        }
    };
    
    public TaskQueue(boolean start) {
        if(start) { startQueue(); }
    }
    
    public TaskQueue() {
        this(true);
    }
    
    public final void startQueue() {
        if(isRunning()) { return; }
        
        synchronized(m_StartLock) {
            m_Thread = new Thread(m_TaskQueue, "Thread Queue");
            m_Thread.start();
            try { m_StartLock.wait(); } catch(InterruptedException e) { }
        }
    }
    
    public final boolean isQueueRunning() {
        synchronized(m_StartLock) { return !m_QueueLock; }
    }
    
    public final boolean isRunning() {
        synchronized(m_StartLock) { return m_Running; }
    }
    
    public final boolean addNewTask(Runnable newTask) {
        synchronized(m_StartLock) {
            if(m_QueueLock) { return false; }
            synchronized(m_Queue) {
                m_Queue.offerLast(newTask);
                m_Queue.notify();
                return true;
            }
        }
    }
    
    public final void close(Runnable lastTask) {
        addNewTask(new ExitThread() {
            @Override
            public void run() {
                try {
                    if(lastTask != null) { lastTask.run(); }
                }
                catch(Exception e) { }
            }
        });
        
        synchronized(m_StartLock) { 
            if(!m_QueueLock) { m_QueueLock = true; }
            else { return; }
        }
    }
    
    public final void close() {
        close(null);
    }
    
    public final void interrupt(long timeout) {
        synchronized(m_StartLock) {
            if(isRunning()) {
                m_Thread.interrupt();
                if(timeout > 0) {
                    try {
                        m_Thread.join(timeout);
                    } catch (InterruptedException ex) { }
                }
                else {
                    try {
                        m_Thread.join();
                    } catch (InterruptedException ex) { }
                }
            }
        }
    }
    
    public final void interrupt() {
        interrupt(-1);
    }
    
    public final boolean waitExitExecute(long timeout) {
        synchronized(m_StopLock) {
            if(isRunning()) {
                try {
                    if(timeout >= 0) {
                        long _begin = System.currentTimeMillis();
                        m_StopLock.wait(timeout);
                        long _end = System.currentTimeMillis();
                        if((_end - _begin) <= timeout) { return true; }
                    }
                    else {
                        m_StopLock.wait();
                        return true;
                    }
                } catch (InterruptedException ex) { }
            }
        }
        
        return false;
    }
    
    public final void waitExitExecuteInfinitely() {
        waitExitExecute(-1);
    }
}