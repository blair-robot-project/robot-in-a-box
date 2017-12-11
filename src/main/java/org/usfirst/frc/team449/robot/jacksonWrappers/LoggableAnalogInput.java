package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.loggable.Loggable;

/**
 * An analog input with logging functionality.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class LoggableAnalogInput extends MappedAnalogInput implements Loggable{

    /**
     * Default constructor.
     *
     * @param port           The analog input port this object reads analog voltage from.
     * @param oversampleBits The sensor will be oversampled by 2^oversampleBits bits. Oversampling is kinda confusing,
     *                       so just read the wikipedia page on it. Defaults to 0.
     * @param averageBits    The sensor output will be the average of 2^averageBits readings. Defaults to 0.
     */
    @JsonCreator
    public LoggableAnalogInput(@JsonProperty(required = true) int port,
                               int oversampleBits,
                               int averageBits) {
        super(port, oversampleBits, averageBits);
    }

    /**
     * Get the headers for the data this subsystem logs every loop.
     *
     * @return An N-length array of String labels for data, where N is the length of the Object[] returned by getData().
     */
    @NotNull
    @Override
    public String[] getHeader() {
        return new String[]{
                "value"
        };
    }

    /**
     * Get the data this subsystem logs every loop.
     *
     * @return An N-length array of Objects, where N is the number of labels given by getHeader.
     */
    @NotNull
    @Override
    public Object[] getData() {
        return new Object[]{
                getPercentValueCached()
        };
    }

    /**
     * Get the name of this object.
     *
     * @return A string that will identify this object in the log file.
     */
    @NotNull
    @Override
    public String getName() {
        return "Analog_"+this.getChannel();
    }
}
