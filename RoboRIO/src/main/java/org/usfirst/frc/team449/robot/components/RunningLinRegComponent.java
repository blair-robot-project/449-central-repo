package org.usfirst.frc.team449.robot.components;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj.CircularBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * A component that does a running linear regression with a finite window.
 */
public class RunningLinRegComponent {

    /**
     * Buffers holding the x and y values that will eventually need to be subtracted from the sum when they leave the window.
     */
    @NotNull
    private final CircularBuffer xBuffer, yBuffer;
    /**
     * The maximum number of points to take the linear regression over.
     */
    private final int bufferSize;
    /**
     * Running sum of the past bufferSize x's and y's, respectively.
     */
    private double xSum, ySum;
    /**
     * Running sum of the past bufferSize x^2's.
     */
    private double xSquaredSum;
    /**
     * Running sum of the past bufferSize x*y's.
     */
    private double xySum;
    /**
     * The number of points currently in the buffer.
     */
    private int numPoints;

    /**
     * The X and Y most recently popped from the buffer. Fields to avoid garbage collection.
     */
    private double backX, backY;

    /**
     * The denominator of the slope calculation equation. Field to avoid garbage collection
     */
    private double denominator;

    /**
     * Default constructor.
     *
     * @param bufferSize The maximum number of points to take the linear regression over.
     */
    @JsonCreator
    public RunningLinRegComponent(@JsonProperty(required = true) int bufferSize) {
        xBuffer = new CircularBuffer(bufferSize);
        yBuffer = new CircularBuffer(bufferSize);
        numPoints = 0;
        xSum = 0;
        ySum = 0;
        this.bufferSize = bufferSize;
    }

    /**
     * @return The current slope of the linear regression line.
     */
    public double getSlope() {
        //Avoid div by 0
        if(numPoints < 2){
            return 0;
        }
        denominator = (xSquaredSum / numPoints) - Math.pow(xSum / numPoints, 2);
        if (denominator == 0){
            return 0;
        }

        //Covariance over variance gets the slope
        return (xySum - xSum * ySum / numPoints) / (numPoints - 1) / //Covariance
                denominator; //Variance
    }

    /**
     * @return The current y-intercept of the linear regression line.
     */
    public double getIntercept() {
        return ySum / numPoints - getSlope() * xSum / numPoints;
    }

    /**
     * Add an x and y point to the buffer and pop out old points if necessary.
     *
     * @param x The x point to add.
     * @param y The y point to add
     */
    public void addPoint(double x, double y) {
        if (numPoints >= bufferSize) {
            //Pop the last point and remove it from the sums
            backX = xBuffer.removeLast();
            backY = yBuffer.removeLast();
            xSum -= backX;
            ySum -= backY;
            xSquaredSum -= backX * backX;
            xySum -= backX * backY;
        } else {
            numPoints++;
        }
        xBuffer.addFirst(x);
        yBuffer.addFirst(y);
        xSum += x;
        ySum += y;
        xSquaredSum += x * x;
        xySum += x * y;
    }

    /**
     * Clone this object.
     *
     * @return A RunningLinRegComponent with the same buffer size as this one.
     */
    @Override
    public RunningLinRegComponent clone(){
        return new RunningLinRegComponent(bufferSize);
    }
}
