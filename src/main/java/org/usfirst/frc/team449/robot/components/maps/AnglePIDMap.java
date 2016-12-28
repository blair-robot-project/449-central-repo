package org.usfirst.frc.team449.robot.components.maps;

import com.google.protobuf.Message;

/**
 * Created by Blair Robot Project on 12/8/2016.
 */
public class AnglePIDMap extends PIDMap {
    public double absoluteTolerance;
    public double minimumOutput;
    public boolean minimumOutputEnabled;

    public AnglePIDMap(maps.org.usfirst.frc.team449.robot.components.AnglePIDMap.AnglePID message) {
        super(message.getSuper());
        absoluteTolerance = message.getAbsoluteTolerance();
        minimumOutput = message.getMinimumOutput();
        minimumOutputEnabled = message.getMinimumOutputEnabled();
    }
}
