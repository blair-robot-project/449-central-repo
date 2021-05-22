package org.usfirst.frc.team449.robot.auto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.StringIdGenerator;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.WRAPPER_OBJECT,
        property = "@class")
@JsonIdentityInfo(generator = StringIdGenerator.class)
public class AutonomousRoutine extends SequentialCommandGroup {

    double executionTime = 0;

    @JsonCreator
    public AutonomousRoutine(@JsonProperty(required = true) List<AutonomousCommand> commandList){
        for(AutonomousCommand command : commandList){
            addCommands(command.getAutoCommand());
            executionTime += command.getRunTimeSeconds() == null ? 0 : command.getRunTimeSeconds();
        }
        if(executionTime >= 15){
            DriverStation.reportWarning("The selected autonomous routine exceeds an execution time of 15 seconds" +
                    " Optimize the routine or it won't finish during play!",false);
        }
    }

}
