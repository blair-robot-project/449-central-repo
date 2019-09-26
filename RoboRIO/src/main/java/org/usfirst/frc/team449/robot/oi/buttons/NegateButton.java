package org.usfirst.frc.team449.robot.oi.buttons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedButton;

/**
 * Negates another {@link MappedButton}.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class NegateButton extends MappedButton {

    /**
     * The button to negate.
     */
    @NotNull
    private final Button toNegate;

    /**
     * Default constructor.
     *
     * @param toNegate The button to negate.
     */
    @JsonCreator
    public NegateButton(@NotNull @JsonProperty(required = true) MappedButton toNegate) {
        this.toNegate = toNegate;
    }

    /**
     * Get the opposite of toNegate's {@link MappedButton#get()}.
     *
     * @return true if toNegate gets false, false otherwise.
     */
    @Override
    public boolean get() {
        return !toNegate.get();
    }
}
