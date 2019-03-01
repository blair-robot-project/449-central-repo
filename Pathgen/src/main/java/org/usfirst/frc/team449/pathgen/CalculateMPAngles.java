package org.usfirst.frc.team449.pathgen;

import jaci.pathfinder.Trajectory;

import java.util.Arrays;

public class CalculateMPAngles {

    private double wheelBaseDiameter;

    private double dt;

    public CalculateMPAngles(double wheelBaseDiameter, double dt) {
        this.wheelBaseDiameter = wheelBaseDiameter;
        this.dt = dt;
    }

    public double[] calculateAngles(Trajectory leftTraj, Trajectory rightTraj) {
        //Time, Left X, Left Y, Right X, Right Y, Angle
        double[][] valuesAtAllTimes = new double[leftTraj.length()][6];
        double[] valuesAtCurrentTime = new double[6];
        double perpendicular,
                deltaLeft,
                deltaRight,
                diffTerm,
                theta,
                rightR,
                leftR,
                vectorTheta,
                vectorDistanceWithoutR;
        valuesAtCurrentTime[0] = 0.;
        valuesAtCurrentTime[1] = 0.;
        valuesAtCurrentTime[2] = wheelBaseDiameter / 2.;
        valuesAtCurrentTime[3] = 0.;
        valuesAtCurrentTime[4] = -wheelBaseDiameter / 2.;
        valuesAtCurrentTime[5] = 0.;
        valuesAtAllTimes[0] = valuesAtCurrentTime;
        for (int i = 1; i < leftTraj.length() - 1; i++) {
            valuesAtCurrentTime = Arrays.copyOf(valuesAtAllTimes[i-1], 6);
//    Get the angle the robot is facing.
            perpendicular = valuesAtCurrentTime[5];
//    Add the change in time
            valuesAtCurrentTime[0] += dt;
//    Figure out linear change for each side using position or velocity
            deltaLeft = leftTraj.get(i).position - leftTraj.get(i - 1).position;
            deltaRight = rightTraj.get(i).position - rightTraj.get(i - 1).position;

            diffTerm = deltaRight - deltaLeft;
//    So in this next part, we figure out the turning center of the robot
//    and the angle it turns around that center. Note that the turning center is
//    often outside of the robot.

//    Calculate how much we turn first, because if theta = 0, turning center is infinitely far away and can't be calcualted.
            theta = diffTerm / wheelBaseDiameter;

            valuesAtCurrentTime[5] += theta;

//     If theta is 0, we're going straight and need to treat it as a special case.
            if (theta == 0) {
                valuesAtCurrentTime[1] += deltaLeft * Math.cos(perpendicular);
                valuesAtCurrentTime[2] += deltaLeft * Math.sin(perpendicular);
                valuesAtCurrentTime[3] += deltaRight * Math.cos(perpendicular);
                valuesAtCurrentTime[4] += deltaRight * Math.sin(perpendicular);
            } else {

//      We do this with sectors, so this is the radius of the turning circle for the
//      left and right sides. They just differ by the diameter of the wheelbase.
                rightR = (wheelBaseDiameter / 2) * (deltaRight + deltaLeft) / diffTerm + wheelBaseDiameter / 2;
                leftR = rightR - wheelBaseDiameter;

//      This is the angle for the vector pointing towards the new position of each
//      wheel.
//      To understand why this formula is correct, overlay isosceles triangles on the sectors
                vectorTheta = (valuesAtAllTimes[i-1][5] + valuesAtCurrentTime[5]) / 2;

//      The is the length of the vector pointing towards the new position of each
//      wheel divided by the radius of the turning circle.
                vectorDistanceWithoutR = 2 * Math.sin(theta / 2);

                valuesAtCurrentTime[1] += vectorDistanceWithoutR * leftR * Math.cos(vectorTheta);
                valuesAtCurrentTime[2] += vectorDistanceWithoutR * leftR * Math.sin(vectorTheta);
                valuesAtCurrentTime[3] += vectorDistanceWithoutR * rightR * Math.cos(vectorTheta);
                valuesAtCurrentTime[4] += vectorDistanceWithoutR * rightR * Math.sin(vectorTheta);
            }
            valuesAtAllTimes[i] = valuesAtCurrentTime;
        }
        double[] angles = new double[leftTraj.length()];
        for (int i = 0; i < valuesAtAllTimes.length - 1; i++) {
            valuesAtCurrentTime = valuesAtAllTimes[i];
            angles[i] = Math.toDegrees(valuesAtCurrentTime[5]);
        }
        return (angles);
    }
}
