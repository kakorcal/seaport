package project4;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class Job extends Thing implements Runnable {
	
	private static final int MAX_PORT_INDEX = 19999;
	private static final int MAX_DOCK_INDEX = 29999;
	private static final int MAX_SHIP_INDEX = 49999;
	private int dockIndex = -1;
	private int shipIndex = -1;
	private double duration;
	private ArrayList<String> requirements = new ArrayList<String>();
	private Thread thread = null;
	private SeaPort port = null;
	private JPanel container = null;
	private JProgressBar progressBar = new JProgressBar();
    private boolean goFlag = true;
    private boolean noKillFlag = true;
	private JButton statusBtn = new JButton("Stop");
	private JButton cancelBtn = new JButton ("Cancel");
	Status status = Status.SUSPENDED;
	
	enum Status {RUNNING, SUSPENDED, WAITING, DONE};
	
	public Job(Scanner sc, JPanel container) {
		super(sc);
		
		this.duration = sc.nextDouble();
		
		while(sc.hasNext()) {
			requirements.add(sc.next());
		}
		
		this.container = container;
		this.thread = new Thread(this, this.getName());
		
		int parent = this.getParent();
		if(parent > MAX_PORT_INDEX && parent <= MAX_DOCK_INDEX) {
			this.dockIndex = parent;
		}else if (parent > MAX_DOCK_INDEX && parent <= MAX_SHIP_INDEX){
			this.shipIndex = parent;
		}
	}
	
	public void buildJob() {
		progressBar.setStringPainted(true);
		container.add(progressBar);
		container.add(new JLabel(port.getShips().get(shipIndex).getName()));
		container.add(new JLabel(this.getName()));
		container.add(statusBtn);
		container.add(cancelBtn);		
	}
	
	public void toggleGoFlag () {
		goFlag = !goFlag;
	}
		  
	public void setKillFlag () {
		noKillFlag = false;
		cancelBtn.setBackground(Color.red);
		cancelBtn.validate();
	}
	
	void showStatus (Status st) {
		status = st;
		switch (status) {
			case RUNNING:
				statusBtn.setBackground(Color.green);
				statusBtn.setText("Running");
		        break;
		    case SUSPENDED:
		    	statusBtn.setBackground(Color.yellow);
		    	statusBtn.setText("Suspended");
		        break;
		    case WAITING:
		    	statusBtn.setBackground(Color.orange);
		    	statusBtn.setText("Waiting Turn");
		        break;
		    case DONE:
		    	statusBtn.setBackground(Color.red);
		        statusBtn.setText("Done");
		        break;
		}
		statusBtn.validate();
	}
	
	void showProgressBar(int value) {
		progressBar.setValue(value);
		progressBar.validate();
	}
	
	public void run() {
		// check first to see if the ship is in dock
		synchronized(port) {
			while(shipInQueue()) {
				try {
					port.wait();
				} catch (InterruptedException e) { e.printStackTrace(); }
			}			
		}
		
		// complete the job
	    long time = System.currentTimeMillis();
	    long startTime = time;
	    long stopTime = (long) (time + duration);
	    
	    int dockInt = port.getShips().get(shipIndex).getParent();
	    System.out.println("Running Job: " + this.getName() + ", Ship index: " + shipIndex + ", At what dock: " + port.getDocks().get(dockInt).getName());
	    while(time < stopTime && noKillFlag) {
	    	try {
	    		Thread.sleep(1000);
	    	} catch(InterruptedException e) {}
	    	
	    	if(goFlag) {
	    		showStatus(Status.RUNNING);
	    		time += 10;
	    		showProgressBar((int)(((time - startTime) / duration) * 100));
	    		progressBar.setValue((int)(((time - startTime) / duration) * 100));
	    		progressBar.validate();
	    	} else {
	    		showStatus(Status.SUSPENDED);
	    	}
	    }
	    
	    showProgressBar(100);
	    showStatus(Status.DONE);
	    
		// if the ship is in dock, acquire the dock
		synchronized(port) {
			port.notifyAll();
		}
		
		// do the jobs
		
		
		// release the ship from dock, 
		// check if jobs in ship are complete, add ship from queue to dock if it is
//		synchronized(port) {
//			
//		}
	}
	
	private boolean shipInQueue() {
		Ship ship = port.getShips().get(shipIndex);
		
		if(ship.getDockIndex() == -1) {
			return true;
		}else {
			return false;
		}
	}

	public String toString() {
		return "";
	}

	public int getDockIndex() {
		return dockIndex;
	}

	public void setDockIndex(int dockIndex) {
		this.dockIndex = dockIndex;
	}

	public int getShipIndex() {
		return shipIndex;
	}

	public void setShipIndex(int shipIndex) {
		this.shipIndex = shipIndex;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public ArrayList<String> getRequirements() {
		return requirements;
	}

	public void setRequirements(ArrayList<String> requirements) {
		this.requirements = requirements;
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public SeaPort getPort() {
		return port;
	}

	public void setPort(SeaPort port) {
		this.port = port;
	}

	public JPanel getContainer() {
		return container;
	}

	public void setContainer(JPanel container) {
		this.container = container;
	}	
}
