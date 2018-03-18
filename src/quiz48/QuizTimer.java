/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48;

/**
 *
 * @author vasya
 */
public final class QuizTimer {
    private long m_QuizTimer,
            m_QuestionTimer, 
            m_TimerValue;
    private boolean m_Started;
    private final Object m_Synch;
    
    public QuizTimer(boolean start) {
        m_Synch = new Object();
        synchronized(m_Synch) {
            m_QuizTimer = 0;
            m_QuestionTimer = 0;
            m_Started = start;
            m_TimerValue = 0;
            if(start) { start(); }
        }
    }
    
    public QuizTimer() { this(false); }
    
    public final void update() {
        synchronized(m_Synch) {
            if(m_Started) {
                long newt = System.currentTimeMillis();
                m_QuestionTimer += newt - m_TimerValue;
                m_QuizTimer += newt - m_TimerValue;
                m_TimerValue = newt;
            }
        }
    }
    
    public final void start() {
        synchronized(m_Synch) {
            if(!m_Started) {
                m_TimerValue = System.currentTimeMillis();
                m_Started = true;
            }
        }
    }
    
    public final void stop() {
        synchronized(m_Synch) {
            if(m_Started) {
                update();
                m_Started = false;
            }
        }
    }
    
    public final long getQuizTimer() {
        synchronized(m_Synch) {
            return m_QuizTimer;
        }
    }
    
    public final long getQuestionTimer() {
        synchronized(m_Synch) {
            return m_QuestionTimer;
        }
    }
    
    public final void resetQuizTimer() {
        synchronized(m_Synch) {
            m_QuizTimer = 0;
        }
    }
    
    public final void resetQuestionTimer() {
        synchronized(m_Synch) {
            m_QuestionTimer = 0;
        }
    }
    
    public static String durationFormat(long d, boolean isTSPC) {
        String format1 = "%1$02d:%2$02d:%3$02d",
                format2 = "%1$02d %2$02d %3$02d";
        int _s = (int)Math.round((double)d / 1000),
                _m = _s / 60,
                _h = _m / 60;
        _m -= _h * 60;
        _s -= _m * 60 + _h * 3600;
        return String.format(isTSPC ? format1 : format2, _h, _m, _s);
    }
}
