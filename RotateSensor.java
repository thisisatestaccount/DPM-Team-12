/* EDITED BY:
 * Alex Carruthers
 * Toby Toubiya
 */


import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;

//This thread just rotates the light sensor back and forth
public class RotateSensor extends Thread{
		
	NXTRegulatedMotor lsMotor = Motor.C;
	public RotateSensor(){
		lsMotor.setSpeed(100);
		lsMotor.rotateTo(-135,false);
		
	}
	
	public void run(){
		Sound.beepSequence();
		lsMotor.rotateTo(135);
		lsMotor.rotateTo(-135);
	}
	
	public int getHeadingOfSensor(){
		return lsMotor.getTachoCount();
	}
}
