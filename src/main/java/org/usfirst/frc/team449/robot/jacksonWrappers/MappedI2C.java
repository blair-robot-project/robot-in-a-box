package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.I2C;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.loggable.Loggable;
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable;

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
	 * The number of bytes to read from the I2C.
	 */
	private final int numBytes;

	/**
	 * Cached value.
	 */
	private byte[] cachedBytes;

	/**
	 * Default constructor.
	 *
	 * @param port          The I2C port the device is connected to
	 * @param deviceAddress The address of the device on the I2C bus.
	 * @param numBytes      The number of bytes to read from the I2C.
	 */
	@JsonCreator
	public MappedI2C(@JsonProperty(required = true) I2C.Port port,
	                 int deviceAddress,
	                 int numBytes) {
		i2c = new I2C(port, deviceAddress);
		this.numBytes = numBytes;
	}

	/**
	 * Read bytes from the I2C.
	 *
	 * @return An array containing the bytes read from the I2C.
	 */
	public byte[] readBytes() {
		byte[] buffer = new byte[numBytes];
		i2c.readOnly(buffer, numBytes);
		return buffer;
	}

	/**
	 * Get the cached bytes read from the I2C.
	 *
	 * @return A cached array containing the bytes read from the I2C.
	 */
	public byte[] getCachedBytes() {
		return cachedBytes;
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
				"bytes"
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
				getCachedBytes()
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
		cachedBytes = readBytes();
	}
}
