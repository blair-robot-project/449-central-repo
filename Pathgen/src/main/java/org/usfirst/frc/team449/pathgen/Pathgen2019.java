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
public class Pathgen2019 {

    public static void main(String[] args) throws IOException {

        //Calculated by driving each wheel n inches in opposite directions, then taking the angle moved, θ, and finding
        // the circumference of a circle moved by the robot via C = 360 * n / θ
        //You then find the diameter via C / π.

        final double robot2019Wheelbase = 1.833;

        //Naming: side, then position. HAB to first hatch uses suffix "Hatch". To load is suffix "ToLoad". From load is prefix "loadTo".
        Waypoint[] StartToLF = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(193.750 / 12.0 - 3.0,3,0),
                new Waypoint(193.750 / 12.0, 0, -Math.PI / 2)
        };
        Waypoint[] LFToLoad = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(-3,0,0),
                new Waypoint(-110.625 / 12.0, -259.938 / 12.0, -Math.PI / 2)
        };
        Waypoint[] StartToLM = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(215.5 / 12.0 - 3.0,3,0),
                new Waypoint(215.5 / 12.0, 0, -Math.PI / 2)
        };
        Waypoint[] LMToLoad = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(-3,0,0),
                new Waypoint(-110.625 / 12.0, -281.688 / 12.0, -Math.PI / 2)
        };
        Waypoint[] LoadToLM = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(-3,0,0),
                new Waypoint(-281.688 / 12.0, -110.625 / 12.0, -Math.PI / 2)
        };
        Waypoint[] StartToLB = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(237.25 / 12.0 - 3.0,3,0),
                new Waypoint(237.25 / 12.0, 0, -Math.PI / 2)
        };
        Waypoint[] LBToLoad = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(-3,0,0),
                new Waypoint(-110.625 / 12.0, -303.438 / 12.0, -Math.PI / 2)
        };
        Waypoint[] LoadToLB = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(-3,0,0),
                new Waypoint(-303.438 / 12.0, -110.625 / 12.0, -Math.PI / 2)
        };
        Waypoint[] StartToRF = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(193.750 / 12.0 - 3.0,-3.0,0),
                new Waypoint(193.750 / 12.0, 0, Math.PI / 2)
        };
        Waypoint[] RFToLoad = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(-3,0,0),
                new Waypoint(110.625 / 12.0, 259.938 / 12.0 / 12.0, Math.PI / 2)
        };
        Waypoint[] StartToRM = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(215.5 / 12.0 - 3.0,-3,0),
                new Waypoint(215.5 / 12.0, 0, Math.PI / 2)
        };
        Waypoint[] RMToLoad = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(-3,0,0),
                new Waypoint(-105.69 / 12.0, 281.688 / 12.0, Math.PI / 2)
        };
        Waypoint[] LoadToRM = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(-3,0,0),
                new Waypoint(-281.688 / 12.0, 110.625 / 12.0, Math.PI / 2)
        };
        Waypoint[] StartToRB = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(237.25 / 12 - 3,-3,0),
                new Waypoint(237.25 / 12.0, 0, Math.PI / 2)
        };
        Waypoint[] RBToLoad = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(-3,0,0),
                new Waypoint(-110.625 / 12.0, 303.438 / 12.0, Math.PI / 2)
        };
        Waypoint[] LoadToRB = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(-3,0,0),
                new Waypoint(-303.438 / 12.0, 110.625 / 12.0, Math.PI / 2)
        };
        Waypoint[] StartToFL = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(136.0 / 12.0,26.0 / 12.0,0)
        };
        Waypoint[] StartToFR = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(136.0 / 12.0,-26.0 / 12.0,0)
        };

        /*Waypoint[] FLToLoad = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(-220/12,0,Math.PI/2),
                new Waypoint(0,105.69/12+6,Math.PI),//changed x - Anika
                new Waypoint(-,105.69/12,Math.PI)
        };
        Waypoint[] loadToFL = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(-220/12,,Math.PI)
        };
        Waypoint[] FRToLoad = new Waypoint[]{
                new Waypoint(0,0,0),
                new Waypoint(-220/12,,Math.PI)
        };

        Waypoint[] loadToFR = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint()
        };*/

        Map<String, Waypoint[]> profiles = new HashMap<>();
        profiles.put("StartToLF", StartToLF);
        profiles.put("StartToLM", StartToLM);
        profiles.put("StartToLB", StartToLB);
        profiles.put("StartToRF", StartToRF);
        profiles.put("StartToRM", StartToRM);
        profiles.put("StartToRB", StartToRB);
        profiles.put("StartToFL", StartToFL);
        profiles.put("StartToFR", StartToFR);
//        profiles.put("LFToLoad", LFToLoad);
//        profiles.put("LMToLoad", LMToLoad);
//        profiles.put("LBToLoad", LBToLoad);
//        profiles.put("RFToLoad", RFToLoad);
//        profiles.put("RMToLoad", RMToLoad);
//        profiles.put("RBToLoad", RBToLoad);
//        profiles.put("LoadToLM", LoadToLM);
//        profiles.put("LoadToLB", LoadToLB);
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