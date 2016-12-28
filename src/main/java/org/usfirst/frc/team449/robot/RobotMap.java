package org.usfirst.frc.team449.robot;

import com.google.protobuf.Message;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * This is the base class for robot maps constructed from the json configuration
 * file. This class holds basic map classes like <code>Motor</code>,
 * <code>Encoder</code>, <code>PID</code>.
 * </p>
 */
public abstract class RobotMap {
	protected Message message;

	public RobotMap(Message message){
		this.message = message;
	}
}