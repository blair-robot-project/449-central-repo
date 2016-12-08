package org.usfirst.frc.team449.robot;

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

    /**
     * This creates a Map based on the <code>JSONObject</code> given to it
     * and a path down to this object
     *
     * @param json      the <code>JSONObject</code> containing the values for this
     *                  object
     * @param objPath   the path to find this object in the
     *                  <code>JSONObject</code>
     * @param enclosing <code>Class</code> one up from this <code>MapObject</code>
     *                  in the map
     */
    public MapObject(JSONObject json, String objPath, Class enclosing) {
        try {
            Field[] fields = this.getClass().getFields();
            Map<String, Class> inners = new HashMap<>();
            Class[] classes = enclosing.getClasses();
            for (Class c : classes) {
                Class temp = inners.put(c.getSimpleName(), c);
                if (temp != null) {
                    System.err.println("Replaced class "
                            + temp.getCanonicalName() + " with "
                            + c.getCanonicalName());
                }
            }
            for (Field f : fields) {
                String type = f.getType().getSimpleName();
                String path = objPath;
                if (type.equals("double") || type.equals("boolean")
                        || type.equals("int")) {
                    path += "." + f.getName();
                    switch (type) {
                        case "double":
                            f.setDouble(this, getDouble(path, json));
                            break;
                        case "boolean":
                            f.setBoolean(this, getBoolean(path, json));
                            break;
                        case "int":
                            f.setInt(this, getInt(path, json));
                            break;
                    }
                } else if (type.endsWith("[]")) { // shit an array
                    String name = type;
                    type = type.substring(0, type.length() - 2);
                    path += "." + name + ".instances.";
                    int ln = getInt(path + f.getName() + ".length", json);
                    Object[] arr = (Object[]) Array.newInstance(
                            inners.get(type), ln);
                    Constructor moConst = inners.get(type).getConstructor(
                            JSONObject.class, String.class, Class.class);
                    for (int i = 0; i < ln; i++) {
                        arr[i] = moConst.newInstance(json,
                                path + f.getName() + "[" + i + "]",
                                enclosing);
                    }
                    f.set(this, arr);

                } else {
                    Constructor moConst = inners.get(type).getConstructor(
                            JSONObject.class, String.class, Class.class);
                    path += "." + type + ".instances." + f.getName();
                    f.set(this, moConst.newInstance(json, path, enclosing));
                }

            }
        } catch (IllegalAccessException | NoSuchMethodException
                | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}