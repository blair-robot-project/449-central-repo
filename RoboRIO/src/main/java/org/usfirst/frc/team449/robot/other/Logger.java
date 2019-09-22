package org.usfirst.frc.team449.robot.other;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.loggable.Loggable;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A logger that logs telemetry data and individual events. Should be run as a separate thread from the main robot
 * loop.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Logger implements Runnable {

    /**
     * A list of all events that have been logged that haven't yet been written to a file.
     */
    @NotNull
    private static List<LogEvent<?>> events = new ArrayList<>();

    /**
     * All loggables added to the Logger outside of the constructor.
     */
    private static List<Loggable> addedLoggables = new ArrayList<>();

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
     * An array of all the loggables with telemetry data to log.
     */
    @NotNull
    private final Loggable[] loggables;

    /**
     * A 2d array of the names of the each datum logged by each subsystem. Organized as
     * itemNames[subsystem][dataIndex].
     */
    @NotNull
    private final String[][] itemNames;

    /**
     * The loop time of the logging loop in milliseconds.
     */
    private final int loopTimeMillis;

    /**
     * The notifier that runs this thread.
     */
    private final Notifier notifier;

    /**
     * The time this that logging started. We don't use {@link Clock} because this is a separate thread.
     */
    private final long startTime;

    /**
     * The filewriters for writing to the telemetry and event logs.
     */
    @NotNull
    private final FileWriter telemetryLogWriter, eventLogWriter;

    /**
     * The last time, in milliseconds, that the logger was run.
     */
    private long lastTime;

    /**
     * The type of the datum currently being logged. Field to avoid garbage collection.
     */
    private Class<?> datumClass;

    /**
     * The list of data from the loggable being logged. Field to avoid garbage collection.
     */
    private Object[] data;

    /**
     * The datum currently being logged. Field to avoid garbage collection.
     */
    private Object datum;

    /**
     * The current line of telemetry data being built up. Field to avoid garbage collection.
     */
    private StringBuilder telemetryData;

    /**
     * Default constructor.
     *
     * @param loggables            The loggables to log telemetry data from.
     * @param eventLogFilename     The filepath of the log for events. Will have the timestamp and file extension
     *                             appended onto the end.
     * @param telemetryLogFilename The filepath of the log for telemetry data. Will have the timestamp and file
     *                             extension appended onto the end.
     * @param loopTimeMillis       The loop time of the logging loop in milliseconds.
     * @throws IOException If the file names provided from the log can't be written to.
     */
    @JsonCreator
    public Logger(@NotNull @JsonProperty(required = true) Loggable[] loggables,
                  @NotNull @JsonProperty(required = true) String eventLogFilename,
                  @NotNull @JsonProperty(required = true) String telemetryLogFilename,
                  @JsonProperty(required = true) int loopTimeMillis) throws IOException {
        //Set up the file names, using a time stamp to avoid overwriting old log files.
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        startTime = System.currentTimeMillis();
        this.eventLogFilename = eventLogFilename + timeStamp + ".csv";
        this.telemetryLogFilename = telemetryLogFilename + timeStamp + ".csv";
        this.loopTimeMillis = loopTimeMillis;
        this.notifier = new Notifier(this);

        //Set up the list of loggables.
        this.loggables = Arrays.copyOf(loggables, loggables.length + addedLoggables.size());

        //Add the addedLoggables to the list of loggables
        for (int i = 0; i < addedLoggables.size(); i++) {
            this.loggables[loggables.length + i] = addedLoggables.get(i);
        }

        //Construct itemNames.
        itemNames = new String[this.loggables.length][];

        eventLogWriter = new FileWriter(this.eventLogFilename);
        telemetryLogWriter = new FileWriter(this.telemetryLogFilename);
        //Write the file headers
        eventLogWriter.write("time,class,message" + "\n");
        //We use a StringBuilder because it's better for building up a string via concatenation.
        StringBuilder telemetryHeader = new StringBuilder();
        telemetryHeader.append("time,Clock.time,");
        for (int i = 0; i < this.loggables.length; i++) {
            String[] items = this.loggables[i].getHeader();
            //Initialize itemNames rows
            itemNames[i] = new String[items.length];
            //For each datum
            for (int j = 0; j < items.length; j++) {
                //Format name as Subsystem.dataName
                String itemName = this.loggables[i].getLogName() + "." + items[j];
                itemNames[i][j] = itemName;
                telemetryHeader.append(itemName);
                telemetryHeader.append(",");
            }
        }
        //Delete the trailing comma
        telemetryHeader.deleteCharAt(telemetryHeader.length() - 1);

        telemetryHeader.append("\n");
        //Write the telemetry file header
        telemetryLogWriter.write(telemetryHeader.toString());
        eventLogWriter.flush();
        telemetryLogWriter.flush();
        lastTime = System.currentTimeMillis();
    }

    /**
     * Log an event to be written to the event log file.
     *
     * @param message The text of the event to log.
     * @param caller  The class causing the event. Almost always will be this.getClass().
     */
    public static <T> void addEvent(@NotNull String message, @NotNull Class<T> caller) {
        events.add(new LogEvent<T>(message, caller));
    }

    /**
     * Add a loggable to be logged. This must be called before a Logger is constructed, and so should be called in the
     * constructor of a Loggable.
     *
     * @param loggable The loggable to add.
     */
    public static void addLoggable(@NotNull Loggable loggable) {
        addedLoggables.add(loggable);
    }

    /**
     * Print out all logged events to the event log and write all the telemetry data to the telemetry log.
     */
    @Override
    public void run() {
//        System.out.println("dt: "+(System.currentTimeMillis()-lastTime));
        lastTime = System.currentTimeMillis();

        try {
            //Log each event to a file
            for (LogEvent<?> event : events) {
                eventLogWriter.write(event.toString() + "\n");
            }
            eventLogWriter.flush();
        } catch (Exception e) {
            System.out.println("Logging failed!");
            e.printStackTrace();
        }

        //Collect telemetry data and write it to SmartDashboard and a file.
        events = new ArrayList<>();
        //We use a StringBuilder because it's better for building up a string via concatenation.
        telemetryData = new StringBuilder();

        //Log the times
        telemetryData.append(System.currentTimeMillis() - startTime).append(",");
        telemetryData.append(Clock.currentTimeMillis()).append(",");

        //Loop through each datum
        for (int i = 0; i < loggables.length; i++) {
            try {
                data = loggables[i].getData();
            } catch (ConcurrentModificationException e) {
                data = new Object[0];
            }

            for (int j = 0; j < data.length; j++) {
                datum = data[j];
                datumClass = datum.getClass();
                //We do this big thing here so we log it to SmartDashboard as the correct data type, so we make each
                //thing into a booleanBox, graph, etc.
                if (datum != null) {
                    if (datumClass.equals(boolean.class) || datumClass.equals(Boolean.class)) {
                        SmartDashboard.putBoolean(itemNames[i][j], (boolean) datum);
                    } else if (datumClass.equals(int.class) || datumClass.equals(Integer.class)) {
                        SmartDashboard.putNumber(itemNames[i][j], (int) datum);
                    } else if (datumClass.equals(double.class)) {
                        SmartDashboard.putNumber(itemNames[i][j], (double) datum);
                    } else if (datumClass.equals(Double.class)) {
                        SmartDashboard.putNumber(itemNames[i][j], (Double) datum);
                    } else if (datumClass.equals(long.class) || datumClass.equals(Long.class)) {
                        SmartDashboard.putNumber(itemNames[i][j], (long) datum);
                    } else if (datumClass.equals(Sendable.class)) {
                        SmartDashboard.putData(itemNames[i][j], (Sendable) datum);
                    } else if (datumClass.equals(String.class)) {
                        SmartDashboard.putString(itemNames[i][j], (String) datum);
                    } else {
                        SmartDashboard.putString(itemNames[i][j], datum.toString());
                    }
                    telemetryData.append(datum.toString());
                } else {
                    SmartDashboard.putString(itemNames[i][j], "null");
                    telemetryData.append("null");
                }

                //Build up the line of data
                telemetryData.append(",");
            }
        }

        //Log the data to a file.
        try {
            telemetryLogWriter.write(telemetryData.toString().substring(0, telemetryData.length() - 1) + "\n");
            telemetryLogWriter.flush();
        } catch (Exception e) {
            System.out.println("Logging failed!");
            e.printStackTrace();
        }
    }

    /**
     * Start running the logger.
     */
    public void start() {
        notifier.startPeriodic(loopTimeMillis / 1000.);
    }
}