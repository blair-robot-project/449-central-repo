package org.usfirst.frc.team449.robot.other;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedRunnable;

/**
 * A Runnable for updating cached variables.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Updater implements MappedRunnable {

    /**
     * The objects to update.
     */
    @NotNull
    private final Updatable[] updatables;

    /**
     * Default constructor
     *
     * @param updatables The objects to update.
     */
    @JsonCreator
    public Updater(@NotNull @JsonProperty(required = true) Updatable[] updatables) {
        this.updatables = updatables;
    }

    /**
     * Update all the updatables.
     */
    @Override
    public void run() {
        for (Updatable updatable : updatables) {
            updatable.update();
        }
    }
}
