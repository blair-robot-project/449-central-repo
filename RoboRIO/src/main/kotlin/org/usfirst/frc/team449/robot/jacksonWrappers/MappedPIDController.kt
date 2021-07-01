package org.usfirst.frc.team449.robot.jacksonWrappers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeInfo
import edu.wpi.first.wpilibj.controller.PIDController
import io.github.oblarg.oblog.Loggable
import io.github.oblarg.oblog.annotations.Log

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
class MappedPIDController @JsonCreator constructor(
    Kp: Double, Ki: Double, Kd: Double, name: String?
) : PIDController(Kp, Ki, Kd), Loggable {
    private val name: String

    @get:Log
    var measurement = 0.0
        private set

    @get:Log
    var output = 0.0
        private set

    @Log
    override fun getVelocityError(): Double {
        return super.getVelocityError()
    }

    @Log
    override fun getSetpoint(): Double {
        return super.getSetpoint()
    }

    override fun calculate(measurement: Double): Double {
        this.measurement = measurement
        return super.calculate(measurement).also { output = it }
    }

    override fun configureLogName(): String {
        return name
    }

    init {
        var name = name
        if (name == null) {
            name = "PIDController"
        }
        this.name = name
    }
}