package project1;

import java.util.Scanner;

public class CargoShip extends Ship {
	private double cargoValue;
	private double cargoVolume;
	private double cargoWeight;
	
	public CargoShip(Scanner sc) {
		super(sc);
		this.cargoWeight = sc.nextDouble();
		this.cargoVolume = sc.nextDouble();
		this.cargoValue = sc.nextDouble();
	}

	public String toString() {
		return "";
	}

	public double getCargoValue() {
		return cargoValue;
	}

	public void setCargoValue(double cargoValue) {
		this.cargoValue = cargoValue;
	}

	public double getCargoVolume() {
		return cargoVolume;
	}

	public void setCargoVolume(double cargoVolume) {
		this.cargoVolume = cargoVolume;
	}

	public double getCargoWeight() {
		return cargoWeight;
	}

	public void setCargoWeight(double cargoWeight) {
		this.cargoWeight = cargoWeight;
	}
}
