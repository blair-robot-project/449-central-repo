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

        final double naviWheelbase = 2.34;

        final double LENGTH = 39.5 / 12.;
        final double WIDTH = 34.5 / 12.;
        final double CUBE_LENGTH = 13. / 12.;
        final double DIAGONAL = Math.sqrt(WIDTH * WIDTH + LENGTH * LENGTH);
        final double INT_ANGLE = Math.atan2(WIDTH, LENGTH);

        Waypoint[] leftXLeft = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(16, 1.5,0),
                new Waypoint(26. - LENGTH, -1, -Math.PI / 4)
        };

        Waypoint[] turnToSwitch = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(naviWheelbase * Math.PI * 122.1511 / 360., 0, 0)
        };

        Waypoint[] turnToScale = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(naviWheelbase * Math.PI * 135. / 360., 0, 0)
        };

        Waypoint[] sameScaleToCubeV2 = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(7.519406 - LENGTH / 2. - CUBE_LENGTH / 2., 0, 0),
        };

        double angleFromHoriz = 1.102613;
        double deltaAngle = Math.toRadians(158.1527 - 90) - angleFromHoriz;
        double distFromBackPlateCorner = 7.049404;
        double xDist = 18.9052653910365 - (11.971 + distFromBackPlateCorner * Math.sin(angleFromHoriz));
        double yDist = 6.66871409506997 - (3.001 + distFromBackPlateCorner * Math.cos(angleFromHoriz));
        Waypoint[] cubeToSwitch = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint((xDist * Math.cos(deltaAngle) - yDist * Math.sin(deltaAngle)) * 10,
                        (xDist * Math.sin(deltaAngle) + yDist * Math.cos(deltaAngle)) * 10
                        , deltaAngle)
        };

        Waypoint[] cubeToAlignPoint = new Waypoint[]{
                new Waypoint(18.1328517591505, 6.29347010384547, Math.toRadians(-157.6557) + Math.PI),
                new Waypoint(18.8469404735703, 9.82704356541704, Math.PI / 2),
        };

        Waypoint[] alignToCube = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(9.82704356541704 - 6.396 - LENGTH / 2., -(18.8469404735703 - 16.333 - WIDTH / 2.), 0),
                new Waypoint(9.82704356541704 - 4.054 - LENGTH / 2., -(18.8469404735703 - 16.333 - WIDTH / 2.), 0)
        };

        Waypoint[] backupToScale = new Waypoint[]{
                new Waypoint(17.9500470066305, 5.40806356863358, Math.toRadians(-88.33607) + Math.PI),
                new Waypoint(23.9056494181233,9.18286909009784,0)
        };
        Waypoint[] leftXRight = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint((17.417+21.786)/2.-LENGTH/2.+0.5,-9,-Math.PI/2),
                new Waypoint(26.-LENGTH-0.5, WIDTH-7.535-11.092-1, 0)
        };

        Waypoint[] turn180 = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(naviWheelbase*Math.PI/2, 0, 0)
        };

        Waypoint[] turn90 = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(naviWheelbase*Math.PI/4., 0 ,0)
        };

        Waypoint[] otherScaleToCube = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(1, 0, 0),
                new Waypoint(23.859847636863-16.333-CUBE_LENGTH-LENGTH/2.,
                        (6.396 + 5.313)/2. -7.09969547043006, -0.1)
        };

        Waypoint[] cubeToOtherSwitch = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(1, 0, 0)
        };

        Waypoint[] turnAfterScale = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(naviWheelbase*Math.PI*117/360., 0, 0)
        };

        double overshoot = -2;
        Waypoint[] crossFromScale = new Waypoint[]{
                new Waypoint(23.8542781528005, 8.65291742739445, Math.toRadians(-153.0016)),
                new Waypoint(21.786, 5.399 + 2,Math.toRadians(-153.0016+10)),
                new Waypoint(17.417+WIDTH/2+1, 0, -Math.PI/2),
                new Waypoint(17.417+WIDTH/2+1,(-6.396-5.313)/2.+overshoot,-Math.PI/2)
        };

        Waypoint[] turnToCrossCube = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(naviWheelbase*(
                        Math.abs(Math.atan2(17.417+WIDTH/2+1-(17.417+16.333)/2.,overshoot)))/2, 0, 0)
        };

        Waypoint[] forward2 = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(2, 0, 0)
        };

        Waypoint[] leftSwitch = new Waypoint[]{
                new Waypoint(LENGTH/2., 11.092-WIDTH/2., 0),
                new Waypoint(LENGTH/2.+7, 11.092-WIDTH/2.+1, Math.PI/8),
                new Waypoint((11.667+16.333)/2., 6.396+LENGTH/2., -Math.PI/2)
        };

        Waypoint[] crossFromLeftSwitch = new Waypoint[]{
                new Waypoint(14.0087625352467, 8.04061392262376, Math.PI/2),
                new Waypoint(17, 13-WIDTH/2, 0),
                new Waypoint(20, 8, -Math.PI/2),
                new Waypoint(19.5, -4, -Math.PI/2),
                new Waypoint(21, -8, -Math.PI/6)
        };

        Waypoint[] crossBackup = new Waypoint[]{
                new Waypoint(19.323011796857, -6.98691304258737, -Math.PI/6),
                new Waypoint(23.6611187925359, -8.47314064611477, -Math.PI/6),
                new Waypoint(26.0144064617474, -10.4006621976282, -Math.PI/2)
        };

        Waypoint[] forward9 = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(9, 0, 0)
        };

        Waypoint[] forwardLong = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(18,0,0)
        };

        Waypoint[] forwardShort = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(4,0,0)
        };

        Waypoint[] forwardMedium = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(16.5,0,0)
        };

        Map<String, Waypoint[]> profiles = new HashMap<>();
//        profiles.put("CrossBackup", crossBackup); edited
//        profiles.put("CrossFromScale", crossFromScale); edited 6 6
//        profiles.put("CrossFromSwitch", crossFromLeftSwitch); edited
//        profiles.put("OtherScale", leftXRight); edited 4.5 4.5
//        profiles.put("OtherScaleToCube", otherScaleToCube); edited 6 5.5
//        profiles.put("SameSwitch", leftSwitch); //edited 4 4
        profiles.put("SameScale", leftXLeft);
//        profiles.put("TurnToSwitch", turnToSwitch);
//        profiles.put("SameScaleToCube", sameScaleToCubeV2);
//        profiles.put("CubeToSwitch", cubeToSwitch);
//        profiles.put("Turn180", turn180);
//        profiles.put("CubeToOtherSwitch", cubeToOtherSwitch);
//        profiles.put("TurnAfterScale", turnAfterScale);
//        profiles.put("TurnToCrossCube", turnToCrossCube);
//        profiles.put("Forward2", forward2);
//        profiles.put("Forward9", forward9);
//		profiles.put("forward100In", points);
        profiles.put("ForwardLong", forwardLong);
        profiles.put("Turn90", turn90);
        profiles.put("ForwardShort", forwardShort);
        profiles.put("ForwardMedium", forwardMedium);

        final String ROBOT_NAME = "navi";

        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_HIGH,
                0.05, 5., 5., 15.); //Units are seconds, feet/second, feet/(second^2), and feet/(second^3)

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