package org.usfirst.frc.team449.robot.components.maps;

import org.json.JSONObject;
import org.usfirst.frc.team449.robot.MapObject;

/**
 * Created by Blair Robot Project on 12/8/2016.
 */
public class CANTalonSRXMap  extends MapObject {
    public int port;
    public int feedbackDevice;
    public boolean reverseSensor;
    public boolean reverseOutput;
    public boolean isInverted;
    public double nominalOutVoltage;
    public double peakOutVoltage;
    public int profile;
    public double kP;
    public double kI;
    public double kD;
    public double kF;
    public boolean fwdLimNormOpen;
    public boolean revLimNormOpen;
    public boolean fwdLimEnabled;
    public boolean revLimEnabled;
    public boolean fwdSoftLimEnabled;
    public double fwdSoftLimVal;
    public boolean revSoftLimEnabled;
    public double revSoftLimVal;

    /**
     * This creates a Map based on the
     * <code>JSONObject</code> given to it
     * and a path down to this object
     *
     * @param json      the <code>JSONObject</code>
     *                  containing the values for this
     *                  object
     * @param objPath   the path to find this object
     *                  in the
     *                  <code>JSONObject</code>
     * @param enclosing <code>Class</code> one up
     *                  from this <code>MapObject</code>
     */
    public CANTalonSRXMap(JSONObject json, String objPath, Class enclosing) {
        super(json, objPath, enclosing);
    }
}
