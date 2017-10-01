package project3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

public class Job extends Thing {
	static Random random = new Random();
	private JPanel parent;
	private double duration;
	private ArrayList<String> requirements = new ArrayList<String>();
	
	private JProgressBar pm = new JProgressBar();
    private boolean goFlag = true;
    private boolean noKillFlag = true;
	private JButton jbGo = new JButton("Stop");
	private JButton jbKill = new JButton ("Cancel");
	Status status = Status.SUSPENDED;
	
	enum Status {RUNNING, SUSPENDED, WAITING, DONE};
	
	public Job(Scanner sc, JPanel parent) {
		super(sc);
		this.duration = sc.nextDouble();
		
		while(sc.hasNext()) {
			requirements.add(sc.next());
		}
		
		this.parent = parent;
		
		JPanel child = new JPanel();		
	    pm = new JProgressBar();
	    pm.setStringPainted(true);
	   
	    child.add(pm);
	    child.add(new JLabel(this.getName() + " " + "Hello", SwingConstants.CENTER));
	    child.add(jbGo);
	    child.add(jbKill);
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
		
//		new Thread(this).start();
	}
	
	public void toggleGoFlag () {
		System.out.println("toggleGoFlag");
		goFlag = !goFlag;
	}
		  
	public void setKillFlag () {
		System.out.println("setKillFlag");
		noKillFlag = false;
		jbKill.setBackground(Color.red);
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
		        jbGo.setText("Waiting turn");
		        break;
		    case DONE:
		        jbGo.setBackground(Color.red);
		        jbGo.setText("Done");
		        break;
		}
	}
	
//	public void run() {
//	    long time = System.currentTimeMillis();
//	    long startTime = time;
//	    long stopTime = (long) (time + duration);
//	    
//	    // get the ship
//	    
//	    // do the job
//	    
//	    // release the ship
//	    
//	    synchronized(worker.party) {
//	    	while(worker.busyFlag) {
//	    		showStatus(Status.WAITING);
//		        try {
//		          worker.party.wait();
//		        }
//		        catch(InterruptedException e) {}
//		    }
//		    worker.busyFlag = true;
//	    }
//
//		while(time < stopTime && noKillFlag) {
//			try {
//				Thread.sleep(100);
//		    } catch(InterruptedException e) {}
//		      
//		    if(goFlag) {
//		    	showStatus(Status.RUNNING);
//		        time += 100;
//		        pm.setValue((int)(((time - startTime) / duration) * 100));
//		    } else {
//		        showStatus (Status.SUSPENDED);
//		    }
//		}
//
//		pm.setValue(100);
//		showStatus(Status.DONE);
//		    
//		synchronized(worker.party) {
//			worker.busyFlag = false; 
//		    worker.party.notifyAll();
//		}
//	}	

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
