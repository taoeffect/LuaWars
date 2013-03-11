package rts.utils;

/**
 * This class represent a timer.
 * 
 * It can be used to wait between two actions.
 * 
 * Use:
 * 
 * Timer t = new Timer(5000);
 * 
 * In update method:
 * 
 * 		//Don't forget to update the timer
 * 		t.update(delta);
 * 		if(t.isTimeComplete()){
 * 			//5 seconds are passed, we can do something
 * 			//reset the timer or not
 * 			t.resetTime();
 * 		}
 * 
 * @author Vincent PIRAULT
 *
 */
public class Timer {

	private float eventTime;
	private float deltaStock;
	private int limit;
	private int limitCounter;

	/**
	 * A simple timer, would be complete after the event time.
	 * 
	 * @param eventTime Time in ms before the complete state of the timer.
	 */
	public Timer(int eventTime) {
		this.eventTime = eventTime;
	}

	/**
	 * A timer with a limit.
	 * 
	 * @param eventTime Time in ms before the complete state of the timer.
	 * @param limit Number of time the timer could be reset before being totally complete.
	 */
	public Timer(int eventTime, int limit) {
		this(eventTime);
		this.limit = limit;
	}
	
	/**
	 * The classic update method.
	 * 
	 * You must call this method, if not the timer will not be updated.
	 * 
	 * @param delta the delta parameter.
	 */
	public void update(int delta) {
		deltaStock += delta;
		if (deltaStock >= eventTime && !(limit != 0 && limitCounter == limit)) {
			deltaStock = eventTime;
			limitCounter++;
		}
	}

	/**
	 * Check if the timer is complete, means the limit timer is complete.
	 * 
	 * A simple timer with no limit is never complete.
	 * 
	 * @return true if the timer is complete, false otherwise.
	 */
	public boolean isComplete() {
		return (limitCounter == limit && limit != 0);
	}

	/**
	 * Check if the time of the timer is passed.
	 * 
	 * @return true if the timer is passed, false otherwise.
	 */
	public boolean isTimeComplete() {
		return deltaStock >= eventTime;
	}

	/**
	 * Reset the time of the timer.
	 */
	public void resetTime() {
		deltaStock = 0;
	}
	
	/**
	 * CHeck if the timer time is reset.
	 * 
	 * @return true if the time is reset,false otherwise.
	 */
	public boolean isTimeReset(){
		return deltaStock == 0;
	}

	/**
	 * Reset the time and the limit counter of the timer.
	 */
	public void reset() {
		limitCounter = 0;
		resetTime();
	}

	/**
	 * Set the counter time of the timer to the same value of the time passed.
	 */
	public void setTimeComplete() {
		deltaStock = eventTime;
	}

	/**
	 * Get the advancement of the timer before the next time complete.
	 * 
	 * @return the percentage in float ( 0 - 1 )
	 */
	public float getPercentage() {
		return deltaStock / eventTime;
	}

}
