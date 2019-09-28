package org.usfirst.frc.team449.robot.other;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Data structure containing the array of points for the MP and a method to fill the MP from a csv file
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MotionProfileData {

    /**
     * Whether or not the profile is inverted because we're driving it backwards.
     */
    private final boolean inverted;

    /**
     * Whether or not to reset the talon position when this profile starts.
     */
    private final boolean resetPosition;

    /**
     * Whether to use position PID or not.
     */
    private final boolean velocityOnly;

    /**
     * The time, in milliseconds, that each point runs for.
     */
    private int pointTimeMillis;

    /**
     * A 2D array containing 4 values for each point- position, velocity, acceleration and delta time respectively, in
     * feet, feet per second, feet per (second^2), and milliseconds.
     */
    private double data[][];

    /**
     * Default constructor
     *
     * @param filename      The filename of the .csv with the motion profile data. The first line must be the number of
     *                      other lines.
     * @param inverted      Whether or not the profile is inverted (would be inverted if we're driving it backwards)
     * @param velocityOnly  Whether or not to only use velocity feed-forward. Used for tuning kV and kA. Defaults to
     *                      false.
     * @param resetPosition Whether or not to reset the talon position when this profile starts. Defaults to false.
     */
    @JsonCreator
    public MotionProfileData(@NotNull @JsonProperty(required = true) String filename,
                             @JsonProperty(required = true) boolean inverted,
                             boolean velocityOnly,
                             boolean resetPosition) {
        this.inverted = inverted;
        this.velocityOnly = velocityOnly;
        this.resetPosition = resetPosition;

        pointTimeMillis = 0;
        try {
            readFile(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Data array constructor.
     *
     * @param pos           An array with position setpoints, in feet.
     * @param vel           An array of corresponding velocity setpoints, in feet/sec.
     * @param accel         An array of corresponding acceleration setpoints, in feet/sec^2.
     * @param deltaTime     The time between setpoints, in seconds.
     * @param inverted      Whether or not the profile is inverted (would be inverted if we're driving it backwards)
     * @param velocityOnly  Whether or not to only use velocity feed-forward. Used for tuning kV and kA. Defaults to
     *                      false.
     * @param resetPosition Whether or not to reset the talon position when this profile starts.
     */
    public MotionProfileData(@NotNull double[] pos, @NotNull double[] vel, @NotNull double[] accel,
                             double deltaTime, boolean inverted, boolean velocityOnly, boolean resetPosition) {
        this.inverted = inverted;
        this.velocityOnly = velocityOnly;
        this.resetPosition = resetPosition;
        this.pointTimeMillis = (int) (deltaTime * 1000.);
        data = new double[pos.length][3];
        for (int i = 0; i < pos.length; i++) {
            data[i][0] = pos[i];
            data[i][1] = vel[i];
            data[i][2] = accel[i];
        }
    }

    /**
     * Data list constructor.
     *
     * @param pos           A list with position setpoints, in feet.
     * @param vel           A list of corresponding velocity setpoints, in feet/sec.
     * @param accel         A list of corresponding acceleration setpoints, in feet/sec^2.
     * @param deltaTime     The time between setpoints, in seconds.
     * @param inverted      Whether or not the profile is inverted (would be inverted if we're driving it backwards)
     * @param velocityOnly  Whether or not to only use velocity feed-forward. Used for tuning kV and kA. Defaults to
     *                      false.
     * @param resetPosition Whether or not to reset the talon position when this profile starts.
     */
    public MotionProfileData(@NotNull List<Double> pos, @NotNull List<Double> vel, @NotNull List<Double> accel,
                             double deltaTime, boolean inverted, boolean velocityOnly, boolean resetPosition) {
        this.inverted = inverted;
        this.velocityOnly = velocityOnly;
        this.resetPosition = resetPosition;
        this.pointTimeMillis = (int) (deltaTime * 1000.);
        data = new double[pos.size()][3];
        for (int i = 0; i < pos.size(); i++) {
            data[i][0] = pos.get(i);
            data[i][1] = vel.get(i);
            data[i][2] = accel.get(i);

        }
    }

    /**
     * Read the profile from the given file and store it in data.
     *
     * @param filename The name of the .csv file containing the motion profile data.
     * @throws IOException if that file doesn't exist.
     */
    private void readFile(@NotNull String filename) throws IOException {
        //Instantiate the reader
        BufferedReader br = new BufferedReader(new FileReader(filename));
        int numLines = Integer.parseInt(br.readLine());

        //Instantiate data
        data = new double[numLines][3];

        //Declare the arrays outside the loop to avoid garbage collection.
        String[] line;
        double[] tmp;

        //Iterate through each line of data.
        for (int i = 0; i < numLines; i++) {
            //split up the line
            line = br.readLine().split(",\t");
            //declare as a new double because we already put the old object it referenced in data.
            tmp = new double[4];

            tmp[0] = Double.parseDouble(line[0]);
            tmp[1] = Double.parseDouble(line[1]);
            tmp[2] = Double.parseDouble(line[2]);

            //Only set once
            if (pointTimeMillis == 0) {
                //Convert to milliseconds
                pointTimeMillis = (int) (Double.parseDouble(line[3]) * 1000);
            }
            data[i] = tmp;
        }
        //Close the reader
        br.close();
    }

    /**
     * @return A 2D array containing 3 values for each point- position, velocity, and delta time respectively.
     */
    @NotNull
    public double[][] getData() {
        return data;
    }

    /**
     * @return The time, in milliseconds, that each point runs for.
     */
    public int getPointTimeMillis() {
        return pointTimeMillis;
    }

    /**
     * @return Whether to use position PID or not.
     */
    public boolean isVelocityOnly() {
        return velocityOnly;
    }

    /**
     * @return Whether or not the profile is inverted because we're driving it backwards.
     */
    public boolean isInverted() {
        return inverted;
    }

    /**
     * @return Whether or not to reset the talon position when this profile starts.
     */
    public boolean resetPosition() {
        return resetPosition;
    }
}
