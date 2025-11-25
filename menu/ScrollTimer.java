package menu;

/**
 * Velocity's basic event-driven timer system. Allows easy time tracking
 * and timer firing in game code. For only tracking elapsed time @see
 * util.Counter. (Yes I stole my own code >:D)
 */
class ScrollTimer {
    /**
     * Unit for milliseconds. Convert from ns.
     */
    public static final int DUR_MS = 1_000_000;

    /**
     * Internal time offset for timer comparisons.
     */
    private long start;

    /**
     * Duration for this timer to run.
     */
    private long origDur;

    /**
     * Actual duration for the timer (after accounting for decay)
     */
    private long targetDur;

    /**
     * Timestamp for this timer to end.
     */
    private long end;

    /**
     * Current state of the counter.
     */
    private String state;

    /**
     * Time to index the decay function to.
     */
    private int cycles = 0;
    
    /**
     * Create a new timer that fires after the given time interval. If requested,
     * the timer will be reset automatically and the timer will re-run.
     * 
     * @param dms Timer duration (milliseconds)
     * @param recurring Whether the timer loops.
     */
    public ScrollTimer(int dms, String state) {
        this.start = System.nanoTime();
        this.origDur = dms * (long)DUR_MS;
        this.targetDur = origDur;
        this.end = this.start + this.origDur;
        this.state = state;
    }

    /**
     * Set this timer's duration.
     * 
     * @param dur New duration.
     */
    public void setDuration(int dur) {
        this.origDur = dur * (long)DUR_MS;
        this.targetDur = origDur;
        this.cycles = 0;
    }

    /**
     * Reset this timer and restart the counter.
     */
    public void reset() {
        this.start = System.nanoTime();
        this.end = this.start + this.targetDur;
    }

    /** 
     * Forcibly expire this timer. 
     */
    public void expire() {
        this.end -= this.start;
    }

    /**
     * Reset the timer if a state change was observed (different input)
     * Resets the timer to the original repeat time.
     */
    public boolean resetIfDifferentState(String state) {
        if (!this.state.equals(state)) {
            this.state = state;
            this.cycles = 0;
            this.targetDur = origDur;
            reset();
            return true;
        }

        return false;
    }

    /**
     * Poll the timer. This is the only way to know whether the timer has
     * fired or not. Will return true once the timer has finished counting.
     * 
     * @return Whether this timer has fired or not.
     */
    public boolean tick() {
        if (System.nanoTime() <= this.end)
            return false;

        this.reset();
        this.targetDur = this.decay(this.cycles++);    
        return true;
    }

    /**
     * Cause the timer to decay and loop faster up to a point.
     * Will use a piecewise function.
     * 
     * @return The newest delay time.
     */
    private long decay(int cycles) {
        if (cycles >= 0 && cycles < 3)
            return 300L * DUR_MS;
        else if (cycles >= 3 && cycles < 8)
            return 100L * DUR_MS;
        else if (cycles >= 8 && cycles < 30)
            return 66L * DUR_MS;
        else if (cycles >= 30)
            return 30L * DUR_MS;

        return 0L;
    }
}
