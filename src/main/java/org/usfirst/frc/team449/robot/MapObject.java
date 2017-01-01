package org.usfirst.frc.team449.robot;

import com.google.protobuf.Message;

/**
 * NOTE: This class is probably useless. It's here because Noah hasn't gotten around to deleting it.
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
     * @param message The protobuf message with the data for this object.
     */
    public MapObject(Message message) {
        this.message = message;
    }
}