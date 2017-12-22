package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.*;
import edu.wpi.first.wpilibj.DigitalInput;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.loggable.Loggable;

import java.util.ArrayList;
import java.util.List;

/**
 * A series of roboRIO digital input pins.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedDigitalInput implements Loggable{

    /**
     * The digitalInputs this class represents
     */
    @JsonIgnore
    protected final List<DigitalInput> digitalInputs;

    /**
     * Value of the inputs. Field to avoid garbage collection.
     */
    private List<Boolean> digitalValues;

    /**
     * Construct a MappedDigitalInput.
     *
     * @param ports The ports to read from, in order.
     */
    @JsonCreator
    public MappedDigitalInput(@NotNull @JsonProperty(required = true) int[] ports) {
        digitalInputs = new ArrayList<>();
        for (int portNum : ports) {
            DigitalInput tmp = new DigitalInput(portNum);
            digitalInputs.add(tmp);
        }
    }

    /**
     * Get the status of each pin specified in the map, in the order they were specified.
     *
     * @return A list of booleans where 1 represents the input receiving a signal and 0 represents no signal.
     */
    @JsonIgnore
    @NotNull
    public List<Boolean> getStatus() {
        digitalValues = new ArrayList<>();
        for (DigitalInput digitalInput : digitalInputs) {
            //Negated because, by default, false means signal and true means no signal, and that's dumb.
            digitalValues.add(!digitalInput.get());
        }
        return digitalValues;
    }

    /**
     * Get the headers for the data this subsystem logs every loop.
     *
     * @return An N-length array of String labels for data, where N is the length of the Object[] returned by getData().
     */
    @NotNull
    @Override
    public String[] getHeader() {
        String[] toRet = new String[digitalInputs.size()];
        for (int i = 0; i < toRet.length; i++){
            toRet[i] = Integer.toString(digitalInputs.get(i).getChannel());
        }
        return toRet;
    }

    /**
     * Get the data this subsystem logs every loop.
     *
     * @return An N-length array of Objects, where N is the number of labels given by getHeader.
     */
    @NotNull
    @Override
    public Object[] getData() {
        return this.getStatus().toArray();
    }

    /**
     * Get the name of this object.
     *
     * @return A string that will identify this object in the log file.
     */
    @NotNull
    @Override
    public String getName() {
        return "DigitalInput";
    }
}