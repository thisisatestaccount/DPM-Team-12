/*
 * EDITED BY:
 * Alex Carruthers
 * Toby Toubiya
 */

import lejos.nxt.*;
public class Navigation extends Thread{
	
	private Odometer odo;
	NXTRegulatedMotor leftMotor = Motor.A;
	NXTRegulatedMotor rightMotor = Motor.B;
	
	//Constants
	private int FORWARD_SPEED = 150;
	private int ROTATE_SPEED = 100;
	private double leftRadius = 2.718;
	private double rightRadius = 2.7;
	private double width = 16;
	private double[] xValues, yValues;
	private int startingPoint = 0;
	private boolean interruptFlag;
	private int wayPointNum;
	
	// constructor
	public Navigation(Odometer odometer) {
		this.odo = odometer;
	}
	
	public void setPath(double[] xValues, double[] yValues){
		this.xValues = xValues;
		this.yValues = yValues;
	}
	
	public void setStartingPoint(int startingPoint){
		this.startingPoint = startingPoint;
	}
	
	public void run() {
		for (int i = startingPoint; i < yValues.length; i++){
			travelTo(xValues[i], yValues[i]);
			//if it is interrupted, exit the loop to stop the thread
			if (interruptFlag == true){
				interruptFlag = false;
				break;
			}
			wayPointNum = i;
		}
	}
	
	public int getWayPointNum(){
		return wayPointNum;
	}
	
	//set the interrupt flag in the class
	public void interrupt(){
		interruptFlag = true;
	}
	
	//convert distance and angle, taken straight from lab 2
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	public void turnTo(double angle){
		double[] pos = new double[3];
		odo.getPosition(pos);
		
		//calculate the angle the robot has to turn to go in the new heading
		double newTheta = Odometer.minimumAngleFromTo(pos[2], angle);
		
		//set the speed of the motors
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		
		//rotate the robot to the correct heading
		leftMotor.rotate(convertAngle(leftRadius, width, newTheta), true);
		rightMotor.rotate(-convertAngle(rightRadius, width, newTheta), false);
	}
	
	//travel in a straight line a certain distance
	public void travelDistance(double distance){
		//set the speed of the motors
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
				
		//travel to the correct position
		leftMotor.rotate(convertDistance(leftRadius, distance), true);
		rightMotor.rotate(convertDistance(rightRadius, distance), false);
	}
	
	public void travelTo(double x, double y){
		try { Thread.sleep(150); } catch (InterruptedException e) {}
		double[] pos = new double[3];
		odo.getPosition(pos);
		//calculate the x and y distances the robot has to go
		double deltaX = (x-pos[0]);
		double deltaY = (y-pos[1]);
				
		//calculate the angle that the robot has to turn to
		double theta0= Math.atan(deltaX/deltaY);
		
		//since atan only gives numbers in the range -pi/2 to pi/2,
		//edit the value if deltaX is 0
		if (deltaY < 0)
			theta0 = Math.PI-theta0;
		
		//turn the robot
		turnTo(theta0 * 180 / Math.PI);
		
		//calculate the distance the robot has to go
		double realDist = Math.sqrt(Math.pow(deltaX,2)+Math.pow(deltaY,2));
		
		//set the speed of the motors
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		
		//travel to the correct position
		leftMotor.rotate(convertDistance(leftRadius, realDist), true);
		rightMotor.rotate(convertDistance(rightRadius, realDist), true);
		
		//if the interrupt flag is thrown, stop the motors and return to the
		//calling method
		double oldDistanceTraveled = odo.getTwoWheeledRobot().getDisplacement();
		while(leftMotor.isMoving() || rightMotor.isMoving()){
			if (interruptFlag){
				leftMotor.stop();
				rightMotor.stop();
				break;
			}
			if (odo.getTwoWheeledRobot().getDisplacement() - oldDistanceTraveled > 60){
				travelTo(x,y);
			}
		}
	}
}
