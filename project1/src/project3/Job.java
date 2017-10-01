package project3;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

public class Job extends Thing implements Runnable {
	static Random random = new Random();
	private JPanel parent;
	private double duration;
	private ArrayList<String> requirements = new ArrayList<String>();
	
	private Dock dock;
	private JProgressBar pm = new JProgressBar();
    private boolean goFlag = true;
    private boolean noKillFlag = true;
	private JButton jbGo = new JButton("Stop");
	private JButton jbKill = new JButton ("Cancel");
	Status status = Status.SUSPENDED;
	
	enum Status {RUNNING, SUSPENDED, WAITING, DONE};
	
	public Job(Scanner sc, JPanel jobsContainer, World world) {
		super(sc);
		this.duration = sc.nextDouble();
		
		while(sc.hasNext()) {
			requirements.add(sc.next());
		}
		
		this.parent = jobsContainer;
		
		HashMap<Integer, SeaPort> ports = world.getPorts();		
		Ship ship = null;
		for(Integer key: ports.keySet()) {
			SeaPort port = ports.get(key);
			HashMap<Integer, Ship> ships = port.getShips();
			ship = world.getShipByIndex(this.getParent(), ships);
			if(ship != null) break;			
		}
		
		for(Integer key: ports.keySet()) {
			SeaPort port = ports.get(key);
			HashMap<Integer, Dock> docks = port.getDocks();
			
			if(ship != null) {
				dock = world.getDockByIndex(ship.getParent(), docks);				
			}else {
				dock = world.getDockByIndex(this.getParent(), docks);				
			}
			if(dock != null) break;			
		}
		
		JPanel child = new JPanel();		
	    pm = new JProgressBar();
	    pm.setStringPainted(true);
	   
	    child.add(pm);
	    child.add(new JLabel(this.getName(), SwingConstants.CENTER));
	    child.add(jbGo);
	    child.add(jbKill);
	    jbGo.setOpaque(true);
	    jbKill.setOpaque(true);
	    child.setLayout(new GridLayout(0, 4));
	    parent.add(child);
	    parent.revalidate();
	    
	    jbGo.addActionListener(new ActionListener () {
	    	public void actionPerformed(ActionEvent e) {
	    		toggleGoFlag();
		    }
		});

		jbKill.addActionListener(new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		        setKillFlag();
		    }
		});
		
		new Thread(this).start();
	}
	
	public void toggleGoFlag () {
		goFlag = !goFlag;
	}
		  
	public void setKillFlag () {
		noKillFlag = false;
		jbKill.setBackground(Color.red);
		parent.revalidate();
	}
	
	void showStatus (Status st) {
		status = st;
		switch (status) {
			case RUNNING:
				jbGo.setBackground(Color.green);
		        jbGo.setText("Running");
		        break;
		    case SUSPENDED:
		        jbGo.setBackground(Color.yellow);
		        jbGo.setText("Suspended");
		        break;
		    case WAITING:
		        jbGo.setBackground(Color.orange);
		        jbGo.setText("Waiting Turn");
		        break;
		    case DONE:
		        jbGo.setBackground(Color.red);
		        jbGo.setText("Done");
		        break;
		}
		parent.revalidate();
	}
	
	public void run() {
	    long time = System.currentTimeMillis();
	    long startTime = time;
	    long stopTime = (long) (time + duration);
	    
	    // get access to the dock 
	    synchronized(dock) {
	    	while(dock.busyFlag) {
	    		showStatus(Status.WAITING);
	    		try {
	    			dock.wait();
	    		}
	    		catch(InterruptedException e) {}
	    	}
	    	dock.busyFlag = true;
	    }
	    
	    // do the job
	    while(time < stopTime && noKillFlag) {
	    	try {
	    		Thread.sleep(1000);
	    	} catch(InterruptedException e) {}
	    	
	    	if(goFlag) {
	    		showStatus(Status.RUNNING);
	    		time += 10;
	    		pm.setValue((int)(((time - startTime) / duration) * 100));
	    	} else {
	    		showStatus(Status.SUSPENDED);
	    	}
	    }
	    
	    pm.setValue(100);
	    showStatus(Status.DONE);
	    
	    // release the ship
	    synchronized(dock) {
	    	dock.busyFlag = false; 
	    	dock.notifyAll();
	    }		    
	}	

	public String toString() {
		String st = "Job: " + super.toString();
		
		if(!requirements.isEmpty()) {
			for(int i = 0; i < requirements.size(); i++) {
				st += " " + requirements.get(i);
			}
		}
		
		return st;
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
}
