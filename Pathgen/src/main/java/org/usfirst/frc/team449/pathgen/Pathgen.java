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
                new Waypoint(26.-LENGTH, -2,-Math.PI/6)
        };

        Waypoint[] leftXRight = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint((17.417+21.786)/2.-LENGTH/2.+0.5,-9,-Math.PI/2),
                new Waypoint(26.-LENGTH, WIDTH-7.535-11.092-1, 0)
        };

        Waypoint[] turn150Raw = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(naviWheelbase*Math.PI*150./360., 0, 0)
        };

//        double switchAngle = Math.PI/2-INT_ANGLE;
//        double switchAngle = 0;
        double switchAngle = Math.toRadians(10);

        Waypoint[] sameScaleToCube = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(24.354-16.333-LENGTH/2-CUBE_LENGTH*1.5, 7.654-6.396+CUBE_LENGTH/2., 0),
                new Waypoint(24.354-16.333-DIAGONAL/2*Math.sin(Math.PI-switchAngle-INT_ANGLE),
                        7.654-6.396+CUBE_LENGTH/2.+DIAGONAL/2*Math.cos(Math.PI/2.-switchAngle-INT_ANGLE)-WIDTH/2.,switchAngle)
//                new Waypoint(24.354-16.333-DIAGONAL/2*Math.abs(Math.cos(switchAngle+Math.PI*3/4)), 7.654-6.396+CUBE_LENGTH/2.+0.3,switchAngle)
        };

        double backupAngle= -Math.PI/4;

        Waypoint[] backupFromSwitch = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(2*Math.abs(Math.cos(backupAngle/2)),2*Math.sin(backupAngle/2),backupAngle)
        };

        Waypoint[] backupIntakeLength = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(2.406833-LENGTH/2.+0.16666666+0.2, 0, 0)
        };

        double afterBackupAngle = -switchAngle+Math.abs(backupAngle);
//        double afterBackupXDistance = 19.7205-16.333+LENGTH/2.;
//        double afterBackupYDistance = 6.396-6.6672+WIDTH/2.;
        double afterBackupXDistance = 20;
        double afterBackupYDistance = -20;

        Waypoint[] alignForCubes = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(afterBackupXDistance*Math.cos(afterBackupAngle)-afterBackupYDistance*Math.sin(afterBackupAngle),
                        afterBackupXDistance*Math.sin(afterBackupAngle)+afterBackupYDistance*Math.cos(afterBackupAngle)
                        , afterBackupAngle)
        };

        System.out.println(afterBackupXDistance);
        System.out.println(afterBackupYDistance);
        System.out.println(Math.toDegrees(afterBackupAngle));
        System.out.println(afterBackupXDistance*Math.cos(afterBackupAngle)+afterBackupYDistance*Math.sin(afterBackupAngle));
        System.out.println(afterBackupXDistance*Math.sin(afterBackupAngle)+afterBackupYDistance*Math.cos(afterBackupAngle));


        Map<String, Waypoint[]> profiles = new HashMap<>();
        profiles.put("SameScale", leftXLeft);
        profiles.put("OtherScale", leftXRight);
        profiles.put("Turn150Raw", turn150Raw);
        profiles.put("SameScaleToCube", sameScaleToCube);
        profiles.put("BackupIntakeLength", backupIntakeLength);
        profiles.put("BackupFromSwitch", backupFromSwitch);
        profiles.put("AlignForCubes", alignForCubes);
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
