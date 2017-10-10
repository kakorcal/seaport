package project4;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;

import javax.swing.JProgressBar;
import javax.swing.table.DefaultTableModel;

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
	private DefaultTableModel tableModel = null;
	private int tableRow = -1;
	private JProgressBar progressBar = new JProgressBar();
    private boolean goFlag = true;
    private boolean noKillFlag = true;
	
	public Job(Scanner sc, DefaultTableModel tableModel, int tableRow) {
		super(sc);
		
		this.duration = sc.nextDouble();
		
		while(sc.hasNext()) {
			requirements.add(sc.next());
		}
		
		this.tableModel = tableModel;
		this.tableRow = tableRow;
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
		progressBar.setStringPainted(true);
		
		rowData[0] = this.getName();
		rowData[2] = port.getName(); 
		rowData[5] = "PROGRESS BAR";
		rowData[6] = Status.JOB_STOP.getStatus();
		rowData[7] = Status.JOB_CANCEL.getStatus();
		rowData[8] = this.getIndex();
		rowData[9] = port.getIndex();
				
		if(requirements.size() == 0) {
			rowData[1] = Status.NONE.getStatus();			
		}else {
			String st = "";
			for(String requirement: requirements) {
				st += requirement + " ";
			}
			rowData[1] = st;
		}
		
		Ship ship = port.getShips().get(shipIndex);
		Dock dock = port.getDocks().get(ship.getDockIndex());
		
		if(dock == null) {
			rowData[3] = Status.NONE.getStatus();
			rowData[10] = Status.NONE.getStatus();
		}else {
			rowData[3] = dock.getName();
			rowData[10] = dock.getIndex(); 
		}
		
		rowData[4] = ship.getName();
		rowData[11] = ship.getIndex(); 

		tableModel.addRow(rowData);
	}
			  
	public void setKillFlag () {
		noKillFlag = false;
	}
		
	void showProgressBar(int value) {
		progressBar.setValue(value);
		progressBar.validate();
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
								", Dock: " + (skipped == null ? Status.NONE.getStatus() : skipped.getName())+ 
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
				tableModel.setValueAt(Status.JOB_WAITING.getStatus(), tableRow, 6);
				try {
					port.wait();
				} catch (InterruptedException e) { e.printStackTrace(); }
			}
			
			Ship ship = port.getShips().get(shipIndex);
			String dockName = port.getDocks().get(ship.getDockIndex()).getName();
			tableModel.setValueAt(dockName, tableRow, 3);			
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
			
			tableModel.setValueAt(Status.SHIP_RELEASED.getStatus(), tableRow, 3);
			
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
								", Dock: " + Status.NONE.getStatus() +
								", Port: " + port.getName() + " Dock: NONE");
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
	    		tableModel.setValueAt(Status.JOB_RUNNING.getStatus(), tableRow, 6);
	    		
	    		time += 10;
	    		showProgressBar((int)(((time - startTime) / duration) * 100));
	    		progressBar.setValue((int)(((time - startTime) / duration) * 100));
	    		progressBar.validate();
	    	} else {
	    		tableModel.setValueAt(Status.JOB_SUSPENDED.getStatus(), tableRow, 6);
	    	}
	    }
	    
	    if(noKillFlag) {
	    	showProgressBar(100);
	    	tableModel.setValueAt(Status.JOB_DONE.getStatus(), tableRow, 6);	    	
	    }else {
	    	tableModel.setValueAt(Status.JOB_STOPPED.getStatus(), tableRow, 6);
	    	tableModel.setValueAt(Status.DASH.getStatus(), tableRow, 7);
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

	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	public void setTableModel(DefaultTableModel tableModel) {
		this.tableModel = tableModel;
	}

	public int getTableRow() {
		return tableRow;
	}

	public void setTableRow(int tableRow) {
		this.tableRow = tableRow;
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
