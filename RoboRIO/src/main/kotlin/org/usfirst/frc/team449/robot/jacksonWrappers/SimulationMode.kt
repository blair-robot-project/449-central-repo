package org.usfirst.frc.team449.robot.jacksonWrappers

/** When to construct simulated hardware component instances.  */
enum class SimulationMode {
    /** Simulate all instances of the hardware component.  */
    ALWAYS,

    /** Simulate all instances of the hardware component when the robot runs in a simulation.  */
    WHEN_SIMULATING,

    /**
     * Attempt to determine whether the hardware component physically exists and only simulate
     * components that can be proven not to exist.
     */
    WHEN_NECESSARY,

    /** Never simulate the hardware component.  */
    NEVER
}