package project1;

import javax.swing.JFrame;

/*
 * File: SeaPortProgram.java
 * Date: Sep 3, 2017
 * Author: Kenneth Korcal
 * Purpose:
 * 
 * */

/*
 * application requirements
 * 1. create JFrame GUI
 * 2. user selects data file using JFileChooser 
 * 3. create data structure w/ JScrollPane and JTextArea
 * 4. add user search capabilities
 * 		• JTextField to specify search target (name, index, skill)
 * 		• don't create new data structure
 * 		• you can create structure of found items as a return value
 * */

public class SeaPortProgram extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 300;
	private static final int HEIGHT = 300;
	private World world;
	
	public static void main(String[] args) {
		
	}
	
	public SeaPortProgram() {
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
}
