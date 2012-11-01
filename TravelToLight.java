/* EDITED BY:
 * Alex Carruthers
 * Toby Toubiya
 */
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

//this class travels to the light beacon, updating the beacon's location as it goes
public class TravelToLight extends Thread {
	private Odometer odo;
	private DetectLight dl;
	private Navigation nav;
	private UltrasonicSensor us;
	private int beaconHeading;

	public TravelToLight(Odometer odo, DetectLight dl, int initialHeading){
		this.odo = odo;
		this.dl = dl;
		this.nav = odo.getNavigation();
		this.beaconHeading = initialHeading;
		us = new UltrasonicSensor(SensorPort.S2);
	}
	public void run(){
		while(us.getDistance() > 25){
			boolean exit = false;
			
			double[] pos = new double[3];
			odo.getPosition(pos);
			//turn towards the beacon
			nav.turnTo(pos[2] + beaconHeading);
			//check the ultrasonic sensor 20 times to make sure there are no errors in the sensor
			for (int i = 0; i<20;i++){
				if (us.getDistance()<25)
					exit = true;
			}
			if (exit) break;
			//travel towards the beacon
			nav.travelDistance(20);
			dl.findLight();
			beaconHeading = dl.getLightHeading();
		}
	}
}
