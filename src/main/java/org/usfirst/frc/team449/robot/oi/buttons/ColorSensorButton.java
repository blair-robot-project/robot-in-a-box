package org.usfirst.frc.team449.robot.oi.buttons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.I2C;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedButton;

/**
 * A button that gets triggered by a reaching a certain color threshold.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ColorSensorButton extends MappedButton {

	public final static boolean ALLIANCE_RED = true;

	/**
	 * The I2C this class is a wrapper on.
	 */
	protected final I2C i2c;

	private final int redThreshold, blueThreshold;

	/**
	 * Default constructor.
	 *
	 * @param port          The I2C port the device is connected to.
	 * @param deviceAddress The address of the device on the I2C bus.
	 * @param redThreshold  The threshold for red, over which the sensed object will be identified as red.
	 * @param blueThreshold The threshold for blue, over which the sensed object will be identified as blue.
	 */
	@JsonCreator
	public ColorSensorButton(@JsonProperty(required = true) I2C.Port port,
	                         @JsonProperty(required = true) Integer deviceAddress,
	                         @JsonProperty(required = true) Integer redThreshold,
	                         @JsonProperty(required = true) Integer blueThreshold) {
		i2c = new I2C(port, deviceAddress);
		this.redThreshold = redThreshold;
		this.blueThreshold = blueThreshold;
	}

	private int[] readRGB() {
		byte[] receivedData = new byte[3];
		i2c.transaction(new byte[0], 0, receivedData, 3);

		int[] rgb = new int[3];
		for (int i = 0; i < receivedData.length; i++) {
			if ((int) receivedData[i] < 0) {
				rgb[i] = (0x000000FF) & receivedData[i];
			} else {
				rgb[i] = (int) receivedData[i];
			}
		}

		return rgb;
	}

	/**
	 * Returns whether or not the trigger is active.
	 * <p>
	 * <p>This method will be called repeatedly a command is linked to the Trigger.
	 *
	 * @return whether or not the trigger condition is active.
	 */
	@Override
	public boolean get() {
		int[] rgb = readRGB();
		if (ALLIANCE_RED) {
			return rgb[2] > blueThreshold;
		} else {
			return rgb[0] > redThreshold;
		}
	}

}
