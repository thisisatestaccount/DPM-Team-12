/* EDITED BY:
 * Alex Carruthers
 * Toby Toubiya
 */

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;

//this class is used to detect the light as the sensor is rotating
public class DetectLight{
	private RotateSensor rs;
	private LightSensor ls;
	private int lightHeading;
	public DetectLight() {
		ls = new LightSensor(SensorPort.S1);
	}
	
	public void findLight(){
		int maxLightValue = 0;
		int maxLightAngle = 0;
		rs = new RotateSensor();
		rs.start();
		//get the max light value of the sensor while it rotates back and forth
		while(rs.isAlive()){
			if(ls.getLightValue() > maxLightValue){
				maxLightValue = ls.getLightValue();
				maxLightAngle = rs.getHeadingOfSensor();
			}
		}
		lightHeading = maxLightAngle;
	}
	
	//get the heading that was found
	public int getLightHeading(){
		return lightHeading;
	}
	
}
