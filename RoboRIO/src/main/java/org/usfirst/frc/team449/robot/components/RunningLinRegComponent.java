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
     * Buffers holding the x and y values that will eventually need to be subtracted from the sum when they leave the
     * window.
     */
    @NotNull
    private final CircularBuffer xBuffer, yBuffer;
    /**
     * The maximum number of points to take the linear regression over.
     */
    private final int bufferSize;
    /**
     * The minimum R^2 value considered significant enough to return the regression slope instead of NaN.
     */
    private final double rSquaredThreshhold;
    /**
     * Running sum of the past bufferSize x's and y's, respectively.
     */
    private double xSum, ySum;
    /**
     * Running sum of the past bufferSize x^2's and y^2, respectively.
     */
    private double xSquaredSum, ySquaredSum;
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
     * The variances. Field to avoid garbage collection
     */
    private double xVariance, yVariance, covariance;

    /**
     * Default constructor.
     *
     * @param bufferSize         The maximum number of points to take the linear regression over.
     * @param rSquaredThreshhold The minimum R^2 value considered significant enough to return the regression slope
     *                           instead of NaN. Defaults to 0.
     */
    @JsonCreator
    public RunningLinRegComponent(@JsonProperty(required = true) int bufferSize,
                                  double rSquaredThreshhold) {
        xBuffer = new CircularBuffer(bufferSize);
        yBuffer = new CircularBuffer(bufferSize);
        this.rSquaredThreshhold = rSquaredThreshhold;
        numPoints = 0;
        xSum = 0;
        ySum = 0;
        this.bufferSize = bufferSize;
    }

    /**
     * @return The current slope of the linear regression line.
     */
    public Double getSlope() {
        if (numPoints < 2) {
            return Double.NaN;
        }

        xVariance = (xSquaredSum / numPoints) - Math.pow(xSum / numPoints, 2);
        yVariance = (ySquaredSum / numPoints) - Math.pow(ySum / numPoints, 2);
        covariance = (xySum - xSum * ySum / numPoints) / (numPoints - 1);

        if (covariance * covariance / (xVariance * yVariance) > rSquaredThreshhold) {
            return covariance / xVariance;
        } else {
            return Double.NaN;
        }
    }

    /**
     * @return The current y-intercept of the linear regression line.
     */
    public Double getIntercept() {
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
            ySquaredSum -= backY * backY;
            xySum -= backX * backY;
        } else {
            numPoints++;
        }
        xBuffer.addFirst(x);
        yBuffer.addFirst(y);
        xSum += x;
        ySum += y;
        xSquaredSum += x * x;
        ySquaredSum += y * y;
        xySum += x * y;
    }

    /**
     * Clone this object.
     *
     * @return A RunningLinRegComponent with the same buffer size as this one.
     */
    @Override
    public org.usfirst.frc.team449.robot.components.RunningLinRegComponent clone() {
        return new org.usfirst.frc.team449.robot.components.RunningLinRegComponent(bufferSize, rSquaredThreshhold);
    }
}
