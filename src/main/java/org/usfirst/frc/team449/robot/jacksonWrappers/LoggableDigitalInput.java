package org.usfirst.frc.team449.robot.jacksonWrappers;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.loggable.Loggable;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class LoggableDigitalInput extends MappedDigitalInput implements Loggable {

    /**
     * Construct a MappedDigitalInput.
     *
     * @param ports The ports to read from, in order.
     */
    @JsonCreator
    public LoggableDigitalInput(@NotNull @JsonProperty(required = true) int[] ports) {
        super(ports);
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
