package org.usfirst.frc.team449.robot.oi.unidirectional.arcade;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.oi.throttles.Throttle;

/**
 * A simple, two-stick arcade drive OI.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class OIArcadeSimple extends OIArcade {

    /**
     * Left (rotation control) stick's throttle
     */
    @NotNull
    private final Throttle rotThrottle;

    /**
     * Right (fwd/rev control) stick's throttle
     */
    @NotNull
    private final Throttle velThrottle;

    /**
     * Default constructor
     *
     * @param rotThrottle The throttle for rotating the robot.
     * @param velThrottle The throttle for driving straight.
     */
    @JsonCreator
    public OIArcadeSimple(@NotNull @JsonProperty(required = true) Throttle rotThrottle,
                          @NotNull @JsonProperty(required = true) Throttle velThrottle) {
        this.rotThrottle = rotThrottle;
        this.velThrottle = velThrottle;
    }

    /**
     * @return rotational velocity component
     */
    @Override
    public double getRot() {
        return rotThrottle.getValue();
    }

    /**
     * @return forward velocity component
     */
    @Override
    public double getFwd() {
        return velThrottle.getValue();
    }

    /**
     * Get the name of this object.
     *
     * @return A string that will identify this object in the log file.
     */
    @NotNull
    @Override
    public String getName() {
        return "OI";
    }
}
