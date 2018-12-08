package org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor.AnalogMotorPosition;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class CarouselRotate extends Command {
    /**
     * Variable for tracking number of turns
     */
    @NotNull
    private int ticks;

    /**
     * The motor to execute this command on
     */
    @NotNull
    private final AnalogMotorPosition motor;

    @JsonCreator
    public CarouselRotate(@NotNull @JsonProperty(required = true) AnalogMotorPosition motor){
        this.motor = motor;

        requires(motor);
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize(){
        Logger.addEvent("CarouselRotate init.", this.getClass());
        ticks = 0;
    }

    /**
     * Motor spins one thick (out of 12) per a second
     */
    @Override
    protected void execute(){
        if (ticks%50 == 0){
            motor.set(ticks/50/12);
        }
        ticks++;
    }

    /**
     * Stop when finished.
     *
     * @return true if finished, false otherwise.
     */
    @Override
    protected boolean isFinished() {
        return false;
    }

    /**
     * Log that the command has ended.
     */
    @Override
    protected void end(){
        Logger.addEvent("CarouselRotate end", this.getClass());
    }

    /**
     * Log that the command has been interrupted.
     */
    @Override
    protected void interrupted(){
        Logger.addEvent("CarouselRotate interrupted!", this.getClass());
    }
}
