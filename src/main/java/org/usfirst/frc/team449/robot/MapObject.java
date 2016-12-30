package org.usfirst.frc.team449.robot;

import com.google.protobuf.Message;

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