package project4;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;

import javax.swing.table.DefaultTableModel;

public class Job extends Thing implements Runnable {
	private static final String JOB_RUNNING = "RUNNING";
	private static final String JOB_WAITING = "WAITING";
	private static final String JOB_SUSPENDED = "SUSPENDED";
	private static final String JOB_DONE = "DONE";
	private static final String JOB_CANCELED = "CANCELED";
	private static final String JOB_CANCEL = "CANCEL";
	private static final String SHIP_RELEASED = "RELEASED";
	private static final String NONE = "NONE";
	private static final String TERMINATED = "--";
	private static final int COLUMN_JOB = 0;
	private static final int COLUMN_REQUIREMENTS = 1;
	private static final int COLUMN_PORT = 2;
	private static final int COLUMN_DOCK = 3;
	private static final int COLUMN_SHIP = 4;
	private static final int COLUMN_PROGRESS_BAR = 5;
	private static final int COLUMN_STATUS_BUTTON = 6;
	private static final int COLUMN_CANCEL_BUTTON = 7;
	private static final int COLUMN_JOB_INDEX = 8;
	private static final int COLUMN_PORT_INDEX = 9;
	private static final int COLUMN_DOCK_INDEX = 10;
	private static final int COLUMN_SHIP_INDEX = 11;
	private static final int MAX_PORT_INDEX = 19999;
	private static final int MAX_DOCK_INDEX = 29999;
	private static final int MAX_SHIP_INDEX = 49999;
	
	private int dockIndex = -1;
	private int shipIndex = -1;
	private double duration;
	private ArrayList<String> requirements = new ArrayList<String>();
	private Thread thread = null;
	private SeaPort port = null;
	private DefaultTableModel jobTableModel = null;
	private int jobTableRow = -1;
    private boolean goFlag = true;
    private boolean noKillFlag = true;
	
	public Job(Scanner sc, DefaultTableModel jobTableModel, DefaultTableModel personTableModel, int jobTableRow) {
		super(sc);
		
		this.duration = sc.nextDouble();
		
		while(sc.hasNext()) {
			requirements.add(sc.next());
		}
		
		this.jobTableModel = jobTableModel;
		this.jobTableRow = jobTableRow;
		this.thread = new Thread(this, this.getName());
		
		int parent = this.getParent();
		if(parent > MAX_PORT_INDEX && parent <= MAX_DOCK_INDEX) {
			this.dockIndex = parent;
		}else if (parent > MAX_DOCK_INDEX && parent <= MAX_SHIP_INDEX){
			this.shipIndex = parent;
		}
	}
	
	// case where no job is required and ship is in dock
	public Job(int shipIndex, int dockIndex, SeaPort port) {
		this.shipIndex = shipIndex;
		this.dockIndex = dockIndex;
		this.port = port;
		this.thread = new Thread(this);
	}
	
	public void buildJob() {
		Object[] rowData = new Object[12];
		rowData[COLUMN_JOB] = this.getName();
		rowData[COLUMN_PORT] = port.getName(); 
		rowData[COLUMN_PROGRESS_BAR] = 0;
		rowData[COLUMN_STATUS_BUTTON] = TERMINATED;
		rowData[COLUMN_CANCEL_BUTTON] = JOB_CANCEL;
		rowData[COLUMN_JOB_INDEX] = this.getIndex();
		rowData[COLUMN_PORT_INDEX] = port.getIndex();
				
		if(requirements.size() == 0) {
			rowData[COLUMN_REQUIREMENTS] = NONE;			
		}else {
			String st = "";
			for(String requirement: requirements) {
				st += requirement + " ";
			}
			rowData[COLUMN_REQUIREMENTS] = st;
		}
		
		Ship ship = port.getShips().get(shipIndex);
		Dock dock = port.getDocks().get(ship.getDockIndex());
		
		if(dock == null) {
			rowData[COLUMN_DOCK] = NONE;
			rowData[COLUMN_DOCK_INDEX] = NONE;
		}else {
			rowData[COLUMN_DOCK] = dock.getName();
			rowData[COLUMN_DOCK_INDEX] = dock.getIndex(); 
		}
		
		rowData[COLUMN_SHIP] = ship.getName();
		rowData[COLUMN_SHIP_INDEX] = ship.getIndex(); 

		jobTableModel.addRow(rowData);
	}
			  			
	public void run() {
		// initially check if ship requires no job and is already at the dock
		synchronized(port) {
			if(shipIndex != -1 && dockIndex != -1) {
				Queue<Integer> queue = port.getQueue();
				
				while(!queue.isEmpty()) {
					Integer queueShipIndex = queue.poll();
					Ship ship = port.getShips().get(queueShipIndex);
					ArrayList<Job> jobs = ship.getJobs();
					
					if(!jobs.isEmpty()) {
						port.getDocks()
						    .get(dockIndex)
						    .setShipIndex(queueShipIndex);
						
						port.getShips()
							.get(queueShipIndex)
							.setDockIndex(dockIndex);
						
						port.getShips()
							.get(shipIndex)
							.setDockIndex(-1);
						
						port.getShips()
							.get(queueShipIndex)
							.setParent(dockIndex);
				
						port.getShips()
							.get(shipIndex)
							.setParent(-1);
						
						port.setQueue(queue);
						break;
					}else {
						Dock skipped = port.getDocks().get(ship.getDockIndex());
						System.out.println(
								"Skipping Ship: " + ship.getName() + 
								", Dock: " + (skipped == null ? NONE: skipped.getName())+ 
								", Port: " + port.getName());
					}
				}
				port.notifyAll();
				return;
			}
			port.notifyAll();
		}
				
		// check first to see if the ship is in dock
		synchronized(port) {
			while(shipInQueue()) {
				updateJobTable(JOB_WAITING, jobTableRow, 6);
				try {
					port.wait();
				} catch (InterruptedException e) { e.printStackTrace(); }
			}
			
			Ship ship = port.getShips().get(shipIndex);
			String dockName = port.getDocks().get(ship.getDockIndex()).getName();
			updateJobTable(dockName, jobTableRow, COLUMN_DOCK);			
		}
		
		// complete the job
		doJob();
	    
		// release the ship, assign queue ship to dock
		synchronized(port) {
			Ship ship = port.getShips().get(shipIndex);
			int dockIndex = ship.getDockIndex();
			ArrayList<Job> jobs = removeJob(ship.getJobs());
			
			port.getShips()
				.get(shipIndex)
				.setJobs(jobs);
			
			updateJobTable(SHIP_RELEASED, jobTableRow, COLUMN_DOCK);
			
			if(jobs.isEmpty()) {
				Queue<Integer> queue = port.getQueue();
				
				while(!queue.isEmpty()) {
					Integer queueShipIndex = queue.poll();
					Ship queueShip = port.getShips().get(queueShipIndex);
					ArrayList<Job> queueJob = queueShip.getJobs();
					
					if(!queueJob.isEmpty()) {
						port.getDocks()
							.get(dockIndex)
							.setShipIndex(queueShipIndex);
						
						port.getShips()
							.get(queueShipIndex)
							.setDockIndex(dockIndex);
						
						port.getShips()
							.get(shipIndex)
							.setDockIndex(-1);
						
						port.getShips()
							.get(queueShipIndex)
							.setParent(dockIndex);
					
						port.getShips()
							.get(shipIndex)
							.setParent(-1);
						
						port.setQueue(queue);
						break;
					}else {
						System.out.println(
								"Skipping Ship: " + queueShip.getName() + 
								", Dock: " + NONE +
								", Port: " + port.getName());
					}
				}
			}
			
			port.notifyAll();
		}
	}
	
	private void doJob() {
	    long time = System.currentTimeMillis();
	    long startTime = time;
	    long stopTime = (long) (time + duration);
	    
	    Ship ship = port.getShips().get(shipIndex);
	    System.out.println(
	    		"Running Job: " + this.getName() + 
	    		", Ship: " + ship.getName() + 
	    		", Dock: " + port.getDocks().get(ship.getDockIndex()).getName() +
	    		", Port: " + port.getName());
	    
	    while(time < stopTime && noKillFlag) {
	    	try {
	    		Thread.sleep(1000);
	    	} catch(InterruptedException e) {}
	    	
	    	if(goFlag) {
				updateJobTable(JOB_RUNNING, jobTableRow, COLUMN_STATUS_BUTTON);
	    		time += 10;
	    		updateJobTable((int)(((time - startTime) / duration) * 100), jobTableRow, COLUMN_PROGRESS_BAR);
	    	} else {
	    		updateJobTable(JOB_SUSPENDED, jobTableRow, COLUMN_STATUS_BUTTON);
	    	}
	    }
	    
	    if(noKillFlag) {
	    	updateJobTable(100, jobTableRow, COLUMN_PROGRESS_BAR);
	    	updateJobTable(JOB_DONE, jobTableRow, COLUMN_STATUS_BUTTON);
	    }else {
	    	updateJobTable(JOB_CANCELED, jobTableRow, COLUMN_STATUS_BUTTON);
	    	updateJobTable(TERMINATED, jobTableRow, COLUMN_CANCEL_BUTTON);
	    }
	}
	
	private ArrayList<Job> removeJob(ArrayList<Job> jobs) {
		int currentIndex = this.getIndex();
		int key = -1;
		
		for(int i = 0; i < jobs.size(); i++) {
			int jobIndex = jobs.get(i).getIndex();
			if(currentIndex == jobIndex) key = i;
		}
		
		if(key != -1) {
			Job removed = jobs.remove(key);
			System.out.println("Removing Job: " + removed.getName() + " Jobs Remaining: " + jobs.size());
		}
		
		return jobs;
	}
	
	private boolean shipInQueue() {
		Ship ship = port.getShips().get(shipIndex);
		
		if(ship.getDockIndex() == -1) {
			return true;
		}else {
			return false;
		}
	}
	
	public void setKillFlag () {
		noKillFlag = false;
	}
	
	public void updateJobTable(Object value, int row, int column) {
		jobTableModel.setValueAt(value, row, column);
	}

	public String toString() {
		return super.toString();
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

	public DefaultTableModel getJobTableModel() {
		return jobTableModel;
	}

	public void setTableModel(DefaultTableModel jobTableModel) {
		this.jobTableModel = jobTableModel;
	}

	public int getJobTableRow() {
		return jobTableRow;
	}

	public void setJobTableRow(int jobTableRow) {
		this.jobTableRow = jobTableRow;
	}

	public boolean isGoFlag() {
		return goFlag;
	}

	public void setGoFlag(boolean goFlag) {
		this.goFlag = goFlag;
	}

	public boolean isNoKillFlag() {
		return noKillFlag;
	}

	public void setNoKillFlag(boolean noKillFlag) {
		this.noKillFlag = noKillFlag;
	}
}
