package ga.base;

public class TimeStep implements Comparable<TimeStep>{
	private int time;
	private boolean[] configuration;
	
	public TimeStep() {}
	
	public TimeStep(int time, boolean[] configuration) {
		this.time = time;
		this.configuration = configuration;
	}
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public boolean[] getConfiguration() {
		return configuration;
	}

	public void setConfiguration(boolean[] configuration) {
		this.configuration = configuration;
	}

	@Override
	public int compareTo(TimeStep o) {
		return Integer.compare(time, o.time);
	}
}