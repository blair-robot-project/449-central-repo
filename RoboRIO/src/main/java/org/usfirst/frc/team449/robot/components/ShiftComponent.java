package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.*;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDoubleSolenoid;

import java.util.List;

/**
 * A component that a subsystem can use to handle shifting gears.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ShiftComponent {

    /**
     * All objects that should be shifted when this component's piston is.
     */
    @NotNull
    protected final List<Shiftable> otherShiftables;

    /**
     * The piston that shifts.
     */
    @NotNull
    private final MappedDoubleSolenoid piston;

    /**
     * The gear this component is currently in.
     */
    protected int currentGear;

    /**
     * Default constructor.
     *
     * @param otherShiftables All objects that should be shifted when this component's piston is.
     * @param piston          The piston that shifts.
     * @param startingGear    The gear to start in. Can be null, and if it is, the starting gear is gotten from the
     *                        piston's position.
     */
    @JsonCreator
    public ShiftComponent(@NotNull @JsonProperty(required = true) List<Shiftable> otherShiftables,
                          @NotNull @JsonProperty(required = true) MappedDoubleSolenoid piston,
                          @Nullable Shiftable.gear startingGear) {
        this.otherShiftables = otherShiftables;
        this.piston = piston;

        if (startingGear != null) {
            this.currentGear = startingGear.getNumVal();
        } else {
            //Get the starting gear from the piston's position if it's not provided
            this.currentGear = piston.get() == DoubleSolenoid.Value.kForward ? Shiftable.gear.LOW.getNumVal() : Shiftable.gear.HIGH.getNumVal();
        }

        //Set all the shiftables to the starting gear.
        for (Shiftable shiftable : otherShiftables) {
            shiftable.setGear(currentGear);
        }
    }

    /**
     * Shifts to a given gear.
     *
     * @param gear The gear to shift to.
     */
    public void shiftToGear(int gear) {
        //Do nothing if we try to switch to the current gear.
        if (!(gear == currentGear)) {
            for (Shiftable shiftable : otherShiftables) {
                shiftable.setGear(gear);
            }
            shiftPiston(gear);
            this.currentGear = gear;
        }
    }

    /**
     * @return The gear the shifter is currently in.
     */
    public int getCurrentGear() {
        return currentGear;
    }

    /**
     * Shifts the piston to the given gear.
     *
     * @param gear The gear to shift to
     */
    protected void shiftPiston(int gear) {
        if (gear == Shiftable.gear.LOW.getNumVal()) {
            //Switch to the low gear pos
            piston.set(DoubleSolenoid.Value.kForward);
        } else {
            //If we want to switch to high gear and the low gear pos is reverse, switch to forward
            piston.set(DoubleSolenoid.Value.kReverse);
        }
    }

}
