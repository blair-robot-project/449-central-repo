package org.usfirst.frc.team0449.robot;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * This tests cfg.json locally by instantiating all the maps and printing an
 * unescaped and an escaped version of the content of the file.
 */
public abstract class Tester {
	/**
	 * Instantiate all maps and print an unescaped and escaped version of the contents of cfg.json
	 *
	 * @param args command line arguments (not applicable)
	 */
	public static void main(String[] args) {
		JSONObject jo = null;
		try {
			jo = new JSONObject(new String(Files.readAllBytes((new File("src/main/resources/cfg.json")).toPath()),
					StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace(); // if this happens, we're fucked
		}

		String s = jo.toString();
		System.out.println(s);
		System.out.println(s.replaceAll("(?<!\\\\)\"", "\\\\\""));
	}

	/**
	 * This is where subsystem initializations go. Override this method with the subsystem initialization of the
	 * current robot code before running the main method of any implementations of <code>Tester</code>.
	 */
	public abstract void initSubsystems();
}
