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
public class Pathgen2019 {

    public static void main(String[] args) throws IOException {

        //Calculated by driving each wheel n inches in opposite directions, then taking the angle moved, θ, and finding
        // the circumference of a circle moved by the robot via C = 360 * n / θ
        //You then find the diameter via C / π.

        // Measured
        final double robot2019Wheelbase = 2.14;

        //Naming: side, then position. HAB to first hatch uses suffix "Hatch". To load is suffix "ToLoad". From load is prefix "loadTo".
        Waypoint[] ToLoadLeftRev = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(-3,3, -Math.PI / 2)
        };
        Waypoint[] LoadToLeftRev = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(-10, -5, Math.PI / 2)
        };
        Waypoint[] StartToLF = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(180 / 12.0 - 3.0,3,0),
                new Waypoint(180 / 12.0, 0, -Math.PI / 2)
        };
        Waypoint[] LFToLoad = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(1.5,1.25,Math.PI / 2),
                new Waypoint(0,2.5, 1 * Math.PI),
                new Waypoint(-259.938 / 12.0 + 3,110.625 / 12.0 - 3., 1 * Math.PI)
        };
        Waypoint[] LoadToLF = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(260.963 / 12. - 10.,-110.625 / 12. + 5,-Math.PI / 2.)
        };
        Waypoint[] StartToLM = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(215.5 / 12.0 - 3.0,3,0),
                new Waypoint(215.5 / 12.0, 0, -Math.PI / 2)
        };
        Waypoint[] LMToLoad = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(1.5,1.25,Math.PI / 2),
                new Waypoint(0,2.5, 1 * Math.PI),
                new Waypoint(-281.688 / 12.0 + 3, 110.625 / 12.0 - 3, 1 * Math.PI)
        };
        Waypoint[] LoadToLM = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(1.5, -1.25,-Math.PI / 2.),
                new Waypoint(1.5, -4.25,-Math.PI / 2.),
                new Waypoint(-110.625 / 12.0 + 5,  -281.688 / 12.0 + 10, -1 * Math.PI)
        };
        Waypoint[] StartToLB = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(237.25 / 12.0 - 3.0,3,0),
                new Waypoint(237.25 / 12.0, 0, -Math.PI / 2)
        };
        Waypoint[] LBToLoad = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(1.5,1.25,Math.PI / 2),
                new Waypoint(0,2.5, 1 * Math.PI),
                new Waypoint(-303.438 / 12.0 + 3, 110.625 / 12.0 - 3, 1 * Math.PI)
        };
        Waypoint[] LoadToLB = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(1.5, -1.25,-Math.PI / 2.),
                new Waypoint(1.5, -4.75,-Math.PI / 2.),
                new Waypoint(-110.625 / 12.0 + 5, -303.438 / 12.0 + 10, 1. * Math.PI)
        };
        Waypoint[] ToLoadRightRev = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(-3,-3, -Math.PI / 2)
        };
        Waypoint[] LoadToRightRev = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(-10, 5, Math.PI / 2)
        };
        Waypoint[] StartToRF = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(185 / 12.0 - 3.0,-5.0,Math.PI/6.),
                new Waypoint(185 / 12.0, 0, Math.PI / 2.)
        };
        Waypoint[] RFToLoad = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(1.5,-1.25,Math.PI / 2),
                new Waypoint(0,-2.5, 1 * Math.PI),
                new Waypoint(259.938 / 12.0 - 3, 110.625 / 12.0 - 9, 1 * Math.PI)
        };
        Waypoint[] LoadToRF = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(260.963 / 12. - 10.,110.625 / 12. - 5,Math.PI / 2.)
        };
        Waypoint[] StartToRM = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(215.5 / 12.0 - 3.0,-3,0),
                new Waypoint(215.5 / 12.0, 0, Math.PI / 2)
        };
        Waypoint[] RMToLoad = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(1.5,-1.25,Math.PI / 2),
                new Waypoint(0,-2.5, 1 * Math.PI),
                new Waypoint(281.688 / 12.0 - 3, 110.625 / 12.0 - 9, 1 * Math.PI)
        };
        Waypoint[] LoadToRM = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(-1.5, -1.25,-Math.PI / 2.),
                new Waypoint(-1.5, -4.25,-Math.PI / 2.),
                new Waypoint(110.625 / 12.0 - 5, -281.688 / 12.0 + 10, 1. * Math.PI)
        };
        Waypoint[] StartToRB = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(237.25 / 12 - 3,-3,0),
                new Waypoint(237.25 / 12.0, 0, Math.PI / 2)
        };
        Waypoint[] RBToLoad = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(1.5,-1.25,Math.PI / 2),
                new Waypoint(0,-2.5, 1 * Math.PI),
                new Waypoint(303.438 / 12.0 - 3, 110.625 / 12.0 - 9, 1 * Math.PI)
        };
        Waypoint[] LoadToRB = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(-1.5, -1.25,-Math.PI / 2.),
                new Waypoint(-1.5, -4.75,-Math.PI / 2.),
                new Waypoint(110.625 / 12.0 - 5, -303.438 / 12.0 + 10, 1. * Math.PI)
        };
        Waypoint[] StartToF = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(136.0 / 12.0,0,0)
        };
        Waypoint[] FToLoadRev = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(-135.82 / 12.,0,0)
        };
        Waypoint[] FLToLoadFwd = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(124.413 / 12.,124.413 / 12.,Math.PI / 2.)
        };
        Waypoint[] FRToLoadFwd = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(124.413 / 12.,-124.413 / 12.,-Math.PI / 2.)
        };

        Map<String, Waypoint[]> profiles = new HashMap<>();
        profiles.put("FToLoadRev", FToLoadRev);
        profiles.put("FLToLoadFwd", FLToLoadFwd);
        profiles.put("FRToLoadFwd", FRToLoadFwd);
        profiles.put("LoadToLeftRev", LoadToLeftRev);
        profiles.put("LoadToRightRev", LoadToRightRev);
//        profiles.put("ToLoadLeftRev", ToLoadLeftRev);
//        profiles.put("ToLoadRightRev", ToLoadRightRev);
//        profiles.put("StartToLF", StartToLF);
//        profiles.put("StartToLM", StartToLM);
//        profiles.put("StartToLB", StartToLB);
//        profiles.put("StartToRF", StartToRF);
//        profiles.put("StartToRM", StartToRM);
//        profiles.put("StartToRB", StartToRB);
        profiles.put("StartToF", StartToF);
//        profiles.put("LFToLoad", LFToLoad);
//        profiles.put("LMToLoad", LMToLoad);
//        profiles.put("LBToLoad", LBToLoad);
//        profiles.put("RFToLoad", RFToLoad);
//        profiles.put("RMToLoad", RMToLoad);
//        profiles.put("RBToLoad", RBToLoad);
        profiles.put("LoadToLF", LoadToLF);
//        profiles.put("LoadToLM", LoadToLM);
//        profiles.put("LoadToLB", LoadToLB);
        profiles.put("LoadToRF", LoadToRF);
//        profiles.put("LoadToRM", LoadToRM);
//        profiles.put("LoadToRB", LoadToRB);

        final String ROBOT_NAME = "robot2019";

        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_HIGH,
                0.05, 5., 5., 15.); //Units are seconds, feet/second, feet/(second^2), and feet/(second^3)

        for (String profile : profiles.keySet()) {
            Trajectory trajectory = Pathfinder.generate(profiles.get(profile), config);

            TankModifier tm = new TankModifier(trajectory).modify(robot2019Wheelbase); //Units are feet

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