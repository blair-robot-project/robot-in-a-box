package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.I2C;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.loggable.Loggable;
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * A Jackson-compatible wrapper for I2C input.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedI2C implements Loggable, Updatable {

	/**
	 * The I2C this class is a wrapper on.
	 */
	protected final I2C i2c;

	/**
	 * Cached values.
	 */
	private int cachedRed, cachedGreen, cachedBlue;

	/**
	 * For delaying how often the I2C is pinged
	 */
	private int ticks;

	/**
	 * Default constructor.
	 *
	 * @param port          The I2C port the device is connected to
	 * @param deviceAddress The address of the device on the I2C bus.
	 */
	@JsonCreator
	public MappedI2C(@JsonProperty(required = true) I2C.Port port,
	                 int deviceAddress) {
		i2c = new I2C(port, deviceAddress);
		ticks = 0;
	}

	public int[] readRGB() {
		byte[] receivedData = new byte[3];
		i2c.transaction(new byte[0], 0, receivedData, 3);

		int[] rgb = new int[3];
		for (int i = 0; i < receivedData.length; i++) {
			rgb[i] = (0x000000FF) & receivedData[i];
		}

		return rgb;
	}

	/**
	 * Get the red value read from the I2C.
	 *
	 * @return The integer red value read from the I2C.
	 */
	public int getCachedRed() {
		return cachedRed;
	}

	/**
	 * Get the green value read from the I2C.
	 *
	 * @return The integer green value read from the I2C.
	 */
	public int getCachedGreen() {
		return cachedGreen;
	}

	/**
	 * Get the blue value read from the I2C.
	 *
	 * @return The integer blue value read from the I2C.
	 */
	public int getCachedBlue() {
		return cachedBlue;
	}

	/**
	 * Get the headers for the data this subsystem logs every loop.
	 *
	 * @return An N-length array of String labels for data, where N is the length of the Object[] returned by getData().
	 */
	@NotNull
	@Override
	public String[] getHeader() {
		return new String[] {
				"red",
				"green",
				"blue"
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
		return new Object[] {
				getCachedRed(),
				getCachedGreen(),
				getCachedBlue()
		};
	}

	/**
	 * Get the name of this object.
	 *
	 * @return A string that will identify this object in the log file.
	 */
	@Override
	public @NotNull String getLogName() {
		return "I2C";
	}

	/**
	 * Updates all cached values with current ones.
	 */
	@Override
	public void update() {
		if (ticks % 20 == 0) {
			int[] rgb = readRGB();
			cachedRed = rgb[0];
			cachedGreen = rgb[1];
			cachedBlue = rgb[2];
		}
		ticks++;
	}
}
