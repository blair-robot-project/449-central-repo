package org.usfirst.frc.team449.pathgen;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;
import sun.nio.cs.ext.MacArabic;

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

        Waypoint[] turnToSwitch = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(naviWheelbase*Math.PI*122.1511/360., 0, 0)
        };

        Waypoint[] turnToScale = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(naviWheelbase*Math.PI*135./360., 0, 0)
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
        Waypoint[] cubeToAlignPoint = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(-2, 3, deltaAngle),
//                new Waypoint(xDist*Math.sin(deltaAngle)+yDist*Math.cos(deltaAngle),
//                -(xDist*Math.cos(deltaAngle)-yDist*Math.sin(deltaAngle)), deltaAngle)
        };

        Waypoint[] alignToCube = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(9.82704356541704 - 6.396 - LENGTH/2., -(18.8469404735703-16.333-WIDTH/2.), 0),
                new Waypoint(9.82704356541704 - 4.054 - LENGTH/2., -(18.8469404735703-16.333-WIDTH/2.), 0)
        };

        Waypoint[] backupToScale = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(10-5.40400855828333-1,26-17.9364149306724-2,-Math.PI/2)
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

        Waypoint[] crossFromScale = new Waypoint[]{
                new Waypoint(23.8542781528005, 8.65291742739445, Math.toRadians(-153.0016)),
                new Waypoint(21.786, 5.399 + 2,Math.toRadians(-153.0016+10)),
                new Waypoint(17.417+WIDTH/2+1, 0, -Math.PI/2),
                new Waypoint(17.417+WIDTH/2+1,(-6.396-5.313)/2.-2,-Math.PI/2)
        };

        Waypoint[] turnToCrossCube = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(naviWheelbase*(
                        Math.abs(Math.atan2(17.417+WIDTH/2+1-(17.417+16.333)/2.,-2)))/2, 0, 0)
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
                new Waypoint(-14.0087625352467, 8.04061392262376, Math.PI/2),
                new Waypoint(-17, 13-WIDTH/2, 0),
                new Waypoint(-20, 8, -Math.PI/2),
                new Waypoint(-19.5, -4, -Math.PI/2),
                new Waypoint(-21, -8, -5*Math.PI/6)
        };

        Waypoint[] crossBackup = new Waypoint[]{
                new Waypoint(19.323011796857, 6.98691304258737, Math.PI/6),
//                new Waypoint(-24.971,-7.535-LENGTH/2., -Math.PI/2)
                new Waypoint(23.6611187925359, 8.47314064611477, Math.PI/6),
                new Waypoint(26.0144064617474, 10.4006621976282, Math.PI/2)
//                new Waypoint(30, 16, Math.PI/2)
//                new Waypoint(0, 0, 0),
//                new Waypoint(5, 0, 0),
//                new Waypoint(8, 0.5, Math.PI/2.+Math.toRadians(-29.8176))
        };

        Map<String, Waypoint[]> profiles = new HashMap<>();
        profiles.put("SameScale", leftXLeft);
        profiles.put("OtherScale", leftXRight);
        profiles.put("TurnToSwitch", turnToSwitch);
        profiles.put("SameScaleToCube2", sameScaleToCubeV2);
        profiles.put("CubeToSwitch", cubeToSwitch);
        profiles.put("CubeToAlign", cubeToAlignPoint);
        profiles.put("AlignToCube", alignToCube);
        profiles.put("BackupToScale", backupToScale);
        profiles.put("TurnToScale", turnToScale);
        profiles.put("Turn180", turn180);
        profiles.put("OtherScaleToCube", otherScaleToCube);
        profiles.put("CubeToOtherSwitch", cubeToOtherSwitch);
        profiles.put("CrossFromScale", crossFromScale);
        profiles.put("TurnAfterScale", turnAfterScale);
        profiles.put("TurnToCrossCube", turnToCrossCube);
        profiles.put("Forward2", forward2);
        profiles.put("LeftSwitch", leftSwitch);
        profiles.put("Turn90", turn90);
        profiles.put("CrossFromSwitch", crossFromLeftSwitch);
        profiles.put("CrossBackup", crossBackup);
//		profiles.put("forward100In", points);

        final String ROBOT_NAME = "navi";

        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_HIGH,
                0.05, 7.5, 7, 10.); //Units are seconds, feet/second, feet/(second^2), and feet/(second^3)

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