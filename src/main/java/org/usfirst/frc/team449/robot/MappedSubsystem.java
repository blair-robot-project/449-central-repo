package org.usfirst.frc.team449.robot;

import com.google.protobuf.Message;
import com.google.protobuf.TextFormat;
import edu.wpi.first.wpilibj.command.Subsystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * The base of all subsystems linked to maps. Holds a Message that will be set
 * by subclasses to their own map
 */
public abstract class MappedSubsystem extends Subsystem {
	public final Message map;

	/**
	 * Creates a mapped subsystem and sets its map
	 *
	 * @param map the map of constants relevant to this subsystem
	 */
	public MappedSubsystem(Message map) {
		this.map = map;
	}

	/**
	 * This creates a Message from a .cfg file referenced by the given
	 * path.
	 *
	 * @param path the path to the <code>.cfg</code> from which to read the message.
	 *
	 * @return the Message created from the given file, which is also put in dest.
	 */
	public static Message readConfig(String path, Message.Builder builder) throws IOException {
		File cfg = new File(path);
		if (!cfg.exists()) {
			throw new RuntimeException("Configuration file does not exist!");
		}
		BufferedReader br = new BufferedReader(new FileReader(cfg));
		TextFormat.getParser().merge(br, builder);
		return builder.build();
	}
}
