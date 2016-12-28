package org.usfirst.frc.team449.robot;

import com.google.protobuf.Message;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static org.usfirst.frc.team449.robot.RobotMap.*;

/**
 * <p>
 * This is an abstract class for any object in the map. This allows for
 * polymorphism and constructors for map objects.
 * </p>
 */
public abstract class MapObject {

    protected Message message;

    /**
     * Standard constructor.
     *
     * @param message
     */
    public MapObject(Message message) {
        this.message = message;
    }
}