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

        final double CENTER_TO_FRONT = 27. / 2.;
        final double CENTER_TO_BACK = 27. / 2. + 3.25;
        final double CENTER_TO_SIDE = 29. / 2. + 3.25;
        final double BACK_FROM_PEG = -5;
        //DO NOT TOUCH THE ONES BELOW
        final double CARRIAGE_LEN = 3.63;
        final double BLUE_WALL_TO_CENTER_PEG = 114.;
        final double BLUE_WALL_TO_SIDE_PEG = 130.5;
        final double BLUE_BACK_CORNER_TO_SIDE_PEG = 89.;
        final double BLUE_HALF_KEY_LENGTH = 152. / 2.;
        final double BLUE_KEY_CORNER_TO_SIDE_PEG = 16.;
        final double RED_WALL_TO_CENTER_PEG = 113.5;
        final double RED_WALL_TO_SIDE_PEG = 131.;
        final double RED_BACK_CORNER_TO_SIDE_PEG = 97.;
        final double RED_HALF_KEY_LENGTH = 152. / 2.;
        final double RED_KEY_CORNER_TO_SIDE_PEG = 21.;
        final double AIRSHIP_PARALLEL_OFFSET_BLUE = 1.;
        final double AIRSHIP_PARALLEL_OFFSET_RED = 2.;

        final double PEG_BASE_TO_CENTER = CENTER_TO_FRONT + CARRIAGE_LEN + BACK_FROM_PEG;

        Waypoint[] points = new Waypoint[]{ //Units are feet and radians.
                new Waypoint(0, 0, 0),
                new Waypoint(100. / 12., 0, 0)
        };

        Waypoint[] blueLeft = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint((BLUE_WALL_TO_SIDE_PEG - CENTER_TO_BACK - 0.5 * PEG_BASE_TO_CENTER + AIRSHIP_PARALLEL_OFFSET_BLUE * Math.cos(5. * Math.PI / 6.)) / 12.
                        , -(BLUE_BACK_CORNER_TO_SIDE_PEG - CENTER_TO_SIDE - (Math.sqrt(3.) / 2.) * PEG_BASE_TO_CENTER + AIRSHIP_PARALLEL_OFFSET_BLUE * Math.sin(5. * Math.PI / 6.)) / 12., -Math.PI / 3.)
        };

        Waypoint[] blueRight = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint((BLUE_WALL_TO_SIDE_PEG - CENTER_TO_BACK - 0.5 * PEG_BASE_TO_CENTER + AIRSHIP_PARALLEL_OFFSET_BLUE * Math.cos(5. * Math.PI / 6.)) / 12.
                        , (BLUE_BACK_CORNER_TO_SIDE_PEG - CENTER_TO_SIDE - (Math.sqrt(3.) / 2.) * PEG_BASE_TO_CENTER + AIRSHIP_PARALLEL_OFFSET_BLUE * Math.sin(5. * Math.PI / 6.)) / 12., Math.PI / 3.)
        };

        Waypoint[] blueCenter = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint((BLUE_WALL_TO_CENTER_PEG - CENTER_TO_BACK - PEG_BASE_TO_CENTER) / 12., 0, 0)
        };

        Waypoint[] redLeft = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint((RED_WALL_TO_SIDE_PEG - CENTER_TO_BACK - 0.5 * PEG_BASE_TO_CENTER + AIRSHIP_PARALLEL_OFFSET_RED * Math.cos(5. * Math.PI / 6.)) / 12.
                        , -(RED_BACK_CORNER_TO_SIDE_PEG - CENTER_TO_SIDE - (Math.sqrt(3.) / 2.) * PEG_BASE_TO_CENTER + AIRSHIP_PARALLEL_OFFSET_RED * Math.sin(5. * Math.PI / 6.)) / 12., -Math.PI / 3.)
        };

        Waypoint[] redRight = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint((RED_WALL_TO_SIDE_PEG - CENTER_TO_BACK - 0.5 * PEG_BASE_TO_CENTER + AIRSHIP_PARALLEL_OFFSET_RED * Math.cos(5. * Math.PI / 6.)) / 12.
                        , (RED_BACK_CORNER_TO_SIDE_PEG - CENTER_TO_SIDE - (Math.sqrt(3.) / 2.) * PEG_BASE_TO_CENTER + AIRSHIP_PARALLEL_OFFSET_RED * Math.sin(5. * Math.PI / 6.)) / 12., Math.PI / 3.)
        };

        Waypoint[] redCenter = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint((RED_WALL_TO_CENTER_PEG - CENTER_TO_BACK - PEG_BASE_TO_CENTER) / 12., 0, 0)
        };

        Waypoint[] redPegToKey = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint((PEG_BASE_TO_CENTER * Math.cos(Math.toRadians(180)) + RED_WALL_TO_SIDE_PEG * Math.cos(Math.toRadians(-60)) + RED_KEY_CORNER_TO_SIDE_PEG * Math.cos(Math.toRadians(30))
                        + RED_HALF_KEY_LENGTH * Math.cos(Math.toRadians(75)) + CENTER_TO_BACK * Math.cos(Math.toRadians(165))) / 12.,
                        (RED_WALL_TO_SIDE_PEG * Math.sin(Math.toRadians(-60)) + RED_KEY_CORNER_TO_SIDE_PEG * Math.sin(Math.toRadians(30))
                                + RED_HALF_KEY_LENGTH * Math.sin(Math.toRadians(75)) + CENTER_TO_BACK * Math.sin(Math.toRadians(165))) / 12.,
                        -Math.toRadians(16))
        };

        Waypoint[] bluePegToKey = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint((PEG_BASE_TO_CENTER * Math.cos(Math.toRadians(180)) + BLUE_WALL_TO_SIDE_PEG * Math.cos(Math.toRadians(60)) + BLUE_KEY_CORNER_TO_SIDE_PEG * Math.cos(Math.toRadians(-30))
                        + BLUE_HALF_KEY_LENGTH * Math.cos(Math.toRadians(-75)) + CENTER_TO_BACK * Math.cos(Math.toRadians(-165))) / 12.,
                        (BLUE_WALL_TO_SIDE_PEG * Math.sin(Math.toRadians(60)) + BLUE_KEY_CORNER_TO_SIDE_PEG * Math.sin(Math.toRadians(-30))
                                + BLUE_HALF_KEY_LENGTH * Math.sin(Math.toRadians(-75)) + CENTER_TO_BACK * Math.sin(Math.toRadians(-165))) / 12.,
                        Math.toRadians(16))
        };

        Waypoint[] backupRed = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(3, 1, Math.PI / 3)
        };

        Waypoint[] backupBlue = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(3, -1, -Math.PI / 3)
        };

        Waypoint[] blueLoadingToLoading = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(22, -5, 0)
        };

        Waypoint[] blueBoilerToLoading = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(5, 0, 0),
                new Waypoint(22, -15, 0)
        };

        Waypoint[] redLoadingToLoading = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(22, 5, 0)
        };

        Waypoint[] redBoilerToLoading = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(5, 0, 0),
                new Waypoint(22, 15, 0)
        };

        Map<String, Waypoint[]> profiles = new HashMap<>();
        profiles.put("RedLeft", redLeft);
        profiles.put("RedRight", redRight);
        profiles.put("RedMid", redCenter);
        profiles.put("BlueLeft", blueLeft);
        profiles.put("BlueRight", blueRight);
        profiles.put("BlueMid", blueCenter);
        profiles.put("RedShoot", redPegToKey);
        profiles.put("BlueShoot", bluePegToKey);
        profiles.put("RedBackup", backupRed);
        profiles.put("BlueBackup", backupBlue);
        profiles.put("forward100In", points);
        profiles.put("BlueLoadingToLoading", blueLoadingToLoading);
        profiles.put("BlueBoilerToLoading", blueBoilerToLoading);
        profiles.put("RedLoadingToLoading", redLoadingToLoading);
        profiles.put("RedBoilerToLoading", redBoilerToLoading);
//		profiles.put("forward100In", points);

        final String ROBOT_NAME = "calcifer";

        //Calculated by driving each wheel n inches in opposite directions, then taking the angle moved, θ, and finding
        // the circumference of a circle moved by the robot via C = 360 * n / θ
        //You then find the diameter via C / π.
        double balbasaurWheelbase = 30. / 12.;

        double calciferWheelbase = 26.6536 / 12.;

        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH,
                0.05, 5., 4.5, 9.); //Units are seconds, feet/second, feet/(second^2), and feet/(second^3)

        for (String profile : profiles.keySet()) {
            Trajectory trajectory = Pathfinder.generate(profiles.get(profile), config);

            TankModifier tm = new TankModifier(trajectory).modify(calciferWheelbase); //Units are feet

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
