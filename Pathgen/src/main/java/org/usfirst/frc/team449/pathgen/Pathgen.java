package org.usfirst.frc.team449.pathgen;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates a motion profile that hits any number of waypoints.
 */
public class Pathgen {

    public static void main(String[] args) throws IOException {

        //Calculated by driving each wheel n inches in opposite directions, then taking the angle moved, θ, and finding
        // the circumference of a circle moved by the robot via C = 360 * n / θ
        //You then find the diameter via C / π.

        final double naviWheelbase = 25.5/12.;

        final double LENGTH = 39.5 / 12.;
        final double WIDTH = 34.5 / 12.;
        final double CUBE_LENGTH = 13./12.;
        final double DIAGONAL = Math.sqrt(WIDTH*WIDTH+LENGTH*LENGTH);
        final double INT_ANGLE = Math.atan2(WIDTH, LENGTH);

        Waypoint[] leftXLeft = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(26.-LENGTH-0.5, -1,-Math.PI/5)
        };

        Waypoint[] leftXRight = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint((17.417+21.786)/2.-LENGTH/2.+0.5,-9,-Math.PI/2),
                new Waypoint(26.-LENGTH, WIDTH-7.535-11.092-1, 0)
        };

        Waypoint[] turn150 = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(naviWheelbase*Math.PI*150./360., 0, 0)
        };

        Waypoint[] turnToSwitch = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(naviWheelbase*Math.PI*122.1511/360., 0, 0)
        };

        Waypoint[] sameScaleToCubeV2 = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(7.519406-LENGTH/2.-CUBE_LENGTH/2., 0, 0),
        };

        double angleFromHoriz = 1.102613;
        double deltaAngle = Math.toRadians(158.1527-90)-angleFromHoriz;
        double distFromBackPlateCorner = 7.049404;
        double xDist = 18.9052653910365 - (11.971 + distFromBackPlateCorner*Math.sin(angleFromHoriz));
        double yDist = 6.66871409506997 - (3.001 + distFromBackPlateCorner*Math.cos(angleFromHoriz));
        Waypoint[] cubeToSwitch = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint((xDist*Math.cos(deltaAngle)-yDist*Math.sin(deltaAngle))*10,
                        (xDist*Math.sin(deltaAngle)+yDist*Math.cos(deltaAngle))*10
                        , deltaAngle)
        };

        deltaAngle = Math.toRadians(90 - 157.6559);
        xDist = (18.1336397008387 - (16.333+WIDTH/2.-0.1));
        yDist = (13.5 -LENGTH/2 - 6.29190374820949);
        System.out.println("X: "+xDist);
        System.out.println("Y: "+yDist);
        Waypoint[] cubeToAlignPoint = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(-2, 3, deltaAngle),
//                new Waypoint(xDist*Math.sin(deltaAngle)+yDist*Math.cos(deltaAngle),
//                -(xDist*Math.cos(deltaAngle)-yDist*Math.sin(deltaAngle)), deltaAngle)
        };
        System.out.println("X: "+(xDist*Math.sin(deltaAngle)+yDist*Math.cos(deltaAngle)));
        System.out.println("Y: "+(xDist*Math.cos(deltaAngle)-yDist*Math.sin(deltaAngle))*-1);

        Waypoint[] alignToCube = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(9.82704356541704 - 6.396 - LENGTH/2., -(18.8469404735703-16.333-WIDTH/2.), 0),
                new Waypoint(9.82704356541704 - 4.054 - LENGTH/2., -(18.8469404735703-16.333-WIDTH/2.), 0)
        };

        Map<String, Waypoint[]> profiles = new HashMap<>();
        profiles.put("SameScale", leftXLeft);
        profiles.put("OtherScale", leftXRight);
        profiles.put("TurnToSwitch", turnToSwitch);
        profiles.put("SameScaleToCube2", sameScaleToCubeV2);
        profiles.put("CubeToSwitch", cubeToSwitch);
        profiles.put("CubeToAlign", cubeToAlignPoint);
        profiles.put("AlignToCube", alignToCube);
//		profiles.put("forward100In", points);

        final String ROBOT_NAME = "navi";

        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_HIGH,
                0.05, 7.5, 6, 10.); //Units are seconds, feet/second, feet/(second^2), and feet/(second^3)

        for (String profile : profiles.keySet()) {
            Trajectory trajectory = Pathfinder.generate(profiles.get(profile), config);

            TankModifier tm = new TankModifier(trajectory).modify(naviWheelbase); //Units are feet

            FileWriter lfw = new FileWriter(ROBOT_NAME + "Left" + profile + "Profile.csv", false);
            FileWriter rfw = new FileWriter(ROBOT_NAME + "Right" + profile + "Profile.csv", false);


            lfw.write(tm.getLeftTrajectory().length() + "\n");
            for (int i = 0; i < tm.getLeftTrajectory().length(); i++) {
                lfw.write(tm.getLeftTrajectory().get(i).position + ",\t" + tm.getLeftTrajectory().get(i).velocity + ",\t"
                        + tm.getLeftTrajectory().get(i).acceleration + ",\t" + tm.getLeftTrajectory().get(i).dt);
                lfw.write("\n");
            }

            rfw.write(tm.getRightTrajectory().length() + "\n");
            for (int i = 0; i < tm.getRightTrajectory().length(); i++) {
                rfw.write(tm.getRightTrajectory().get(i).position + ",\t" + tm.getRightTrajectory().get(i).velocity +
                        ",\t" + tm.getLeftTrajectory().get(i).acceleration + ",\t" + tm.getRightTrajectory().get(i).dt);
                rfw.write("\n");
            }

            lfw.flush();
            lfw.close();
            rfw.flush();
            rfw.close();
        }
    }
}
