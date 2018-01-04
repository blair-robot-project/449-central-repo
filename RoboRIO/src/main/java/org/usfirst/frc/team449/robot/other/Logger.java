package org.usfirst.frc.team449.robot.other;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.loggable.Loggable;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A logger that logs telemetry data and individual events. Should be run as a separate thread from the main robot
 * loop.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Logger implements Runnable {

    /**
     * A list of all events that subsystems and commands have logged that haven't yet been written to a file.
     */
    @NotNull
    private static List<LogEvent> events = new ArrayList<>();

    /**
     * The file path for the event log.
     */
    @NotNull
    private final String eventLogFilename;

    /**
     * The file path for the telemetry data log.
     */
    @NotNull
    private final String telemetryLogFilename;

    /**
     * An array of all the subsystems with telemetry data to log.
     */
    @NotNull
    private final Loggable[] subsystems;

    /**
     * A 2d array of the entries of the each datum logged by each subsystem. Organized as
     * itemNames[subsystem][dataIndex].
     */
    @NotNull
    private final NetworkTableEntry[][] itemNames;

    /**
     * The networkTable entries for the most recent event, the time, and the clock time.
     */
    @NotNull
    private final NetworkTableEntry eventNTEntry, timeNTEntry, clockTimeNTEntry;

    /**
     * Filewriters for the log files. Fields to avoid garbage collection.
     */
    private FileWriter eventLogWriter, telemetryLogWriter;

    /**
     * The time, in milliseconds, since the robot code started. Field to avoid garbage collection.
     */
    private long time;

    /**
     * The period of the loop running this logger, in seconds.
     */
    private final double loopTimeSecs;

    /**
     * The time this that logging started. We don't use {@link Clock} because this is a separate thread.
     */
    private final long startTime;

    /**
     * Default constructor.
     *
     * @param subsystems           The subsystems to log telemetry data from.
     * @param loopTimeSecs         The period of the loop for collecting telemetry data, in seconds.
     * @param eventLogFilename     The filepath of the log for events. Will have the timestamp and file extension
     *                             appended onto the end.
     * @param telemetryLogFilename The filepath of the log for telemetry data. Will have the timestamp and file
     *                             extension appended onto the end.
     * @throws IOException If the file names provided from the log can't be written to.
     */
    @JsonCreator
    public Logger(@NotNull @JsonProperty(required = true) Loggable[] subsystems,
                  @JsonProperty(required = true) double loopTimeSecs,
                  @NotNull @JsonProperty(required = true) String eventLogFilename,
                  @NotNull @JsonProperty(required = true) String telemetryLogFilename) throws IOException {
        //Set up the file names, using a time stamp to avoid overwriting old log files.
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        startTime = System.currentTimeMillis();
        this.eventLogFilename = eventLogFilename + timeStamp + ".csv";
        this.telemetryLogFilename = telemetryLogFilename + timeStamp + ".csv";

        //Set up network table
        NetworkTable table = NetworkTableInstance.getDefault().getTable("log");
        eventNTEntry = table.getEntry("event");
        timeNTEntry = table.getEntry("time");
        clockTimeNTEntry = table.getEntry("clockTime");

        //Set the loop time variable
        this.loopTimeSecs = loopTimeSecs;

        //Set up the list of loggable subsystems.
        this.subsystems = subsystems;

        //Construct itemNames.
        itemNames = new NetworkTableEntry[this.subsystems.length][];

        eventLogWriter = new FileWriter(this.eventLogFilename);
        telemetryLogWriter = new FileWriter(this.telemetryLogFilename);
        //Write the file headers
        eventLogWriter.write("time,class,message" + "\n");
        //We use a StringBuilder because it's better for building up a string via concatenation.
        StringBuilder telemetryHeader = new StringBuilder();
        telemetryHeader.append("time,Clock.time,");
        for (int i = 0; i < this.subsystems.length; i++) {
            String[] items = this.subsystems[i].getHeader();
            String subsystemName = this.subsystems[i].getLogName();
            //Initialize itemNames rows
            itemNames[i] = new NetworkTableEntry[items.length];
            //For each datum
            for (int j = 0; j < items.length; j++) {
                //Format name as Subsystem.dataName
                itemNames[i][j] = table.getSubTable(subsystemName).getEntry(items[j]);
                telemetryHeader.append(subsystemName).append(".").append(items[j]).append(",");
            }
        }
        //Delete the trailing comma
        telemetryHeader.deleteCharAt(telemetryHeader.length() - 1);

        telemetryHeader.append("\n");
        //Write the telemetry file header
        telemetryLogWriter.write(telemetryHeader.toString());
        eventLogWriter.close();
        telemetryLogWriter.close();
    }

    /**
     * Log an event to be written to the event log file.
     *
     * @param message The text of the event to log.
     * @param caller  The class causing the event. Almost always will be this.getClass().
     */
    public static void addEvent(@NotNull String message, @NotNull Class caller) {
        events.add(new LogEvent(message, caller));
    }

    /**
     * Print out all logged events to the event log and write all the telemetry data to the telemetry log.
     */
    @Override
    public void run() {
        try {
            eventLogWriter = new FileWriter(eventLogFilename, true);
        } catch (IOException e) {
            System.out.println("Event log not found!");
            e.printStackTrace();
        }
        try {
            telemetryLogWriter = new FileWriter(telemetryLogFilename, true);
        } catch (IOException e) {
            System.out.println("Telemetry log not found!");
            e.printStackTrace();
        }
        try {
            //Log each event to a file
            for (LogEvent event : events) {
                eventLogWriter.write(event.toString() + "\n");
                eventNTEntry.setString(event.toString());
            }
        } catch (IOException e) {
            System.out.println("Logging failed!");
            e.printStackTrace();
        }
        //Collect telemetry data and write it to Shuffleboard and a file.
        events = new ArrayList<>();
        //We use a StringBuilder because it's better for building up a string via concatenation.
        StringBuilder telemetryData = new StringBuilder();

        //Log the times
        time = System.currentTimeMillis() - startTime;
        telemetryData.append(time).append(",");
        telemetryData.append(Clock.currentTimeMillis()).append(",");
        timeNTEntry.setNumber(time);
        clockTimeNTEntry.setNumber(Clock.currentTimeMillis());

        //Loop through each datum
        for (int i = 0; i < subsystems.length; i++) {
            Object[] data = subsystems[i].getData();
            for (int j = 0; j < data.length; j++) {
                Object datum = data[j];
                if (datum != null) {
                    try {
                        itemNames[i][j].setValue(datum);
                    } catch (IllegalArgumentException e){
                        //If it's not a type NetworkTables recognizes, convert to string.
                        System.out.println("You forgot to convert to String in getData!!");
                        e.printStackTrace();
                        itemNames[i][j].setValue(datum.toString());
                    }
                    telemetryData.append(datum.toString());
                } else {
                    itemNames[i][j].setString("null");
                    telemetryData.append("null");
                }

                //Build up the line of data
                telemetryData.append(",");
            }
        }

        String telemetryString = telemetryData.toString();
        telemetryString = telemetryString.substring(0, telemetryString.length() - 1);
        telemetryString += "\n";
        //Log the data to a file.
        try {
            telemetryLogWriter.write(telemetryString);
        } catch (IOException e) {
            System.out.println("Logging failed!");
            e.printStackTrace();
        }
        try {
            telemetryLogWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            eventLogWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return The map-specified loop period of this logger, in seconds.
     */
    public double getLoopTimeSecs() {
        return loopTimeSecs;
    }
}
