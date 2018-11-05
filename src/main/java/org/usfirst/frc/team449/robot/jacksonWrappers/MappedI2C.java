package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.I2C;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.loggable.Loggable;
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable;

import java.util.Arrays;

/**
 * A Jackson-compatible wrapper for I2C input.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedI2C implements Loggable, Updatable {

	private final static int TCS34725_ID = 0x12,
			TCS34725_ATIME = 0x01,
			TCS34725_INTEGRATIONTIME_50MS = 0xEB,
			TCS34725_CONTROL = 0x0F,
			TCS34725_GAIN_4X = 0x01,
			TCS34725_ENABLE = 0x00,
			TCS34725_ENABLE_PON = 0x01,
			TCS34725_ENABLE_AEN = 0x02,
			TCS34725_ENABLE_AIEN = 0x10,
			TCS34725_CDATAL = 0x14,
			TCS34725_RDATAL = 0x16,
			TCS34725_GDATAL = 0x18,
			TCS34725_BDATAL = 0x1A;

	/**
	 * The I2C this class is a wrapper on.
	 */
	protected final I2C i2c;

	/**
	 * Cached values.
	 */
	private int cachedRed, cachedGreen, cachedBlue;

	private int clear, red, green, blue;

	/**
	 * For pinging the sensor at specific time intervals.
	 */
	private int time;

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

		time = -1;

		read8(TCS34725_ID);
		write8(TCS34725_ATIME, TCS34725_INTEGRATIONTIME_50MS);
		write8(TCS34725_CONTROL, TCS34725_GAIN_4X);
		write8(TCS34725_ENABLE, TCS34725_ENABLE_PON);

		try {
			Thread.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		write8(TCS34725_ENABLE, TCS34725_ENABLE_PON | TCS34725_ENABLE_AEN);
	}

	public int read8(int registerAddress) {
		byte[] buffer = new byte[1];
		i2c.read(registerAddress, 1, buffer);
		return Byte.toUnsignedInt(buffer[0]);
	}

	public int read16(int registerAddress) {
		byte[] buffer = new byte[2];
		i2c.read(registerAddress, 2, buffer);
		return Byte.toUnsignedInt(buffer[0]) * 255 + Byte.toUnsignedInt(buffer[1]);
	}

	public void write8(int registerAddress, int data) {
		i2c.write(registerAddress, data);
	}

	public void setInterrupt(boolean i) {
		int r = read8(TCS34725_ENABLE);
		if (i) {
			r |= TCS34725_ENABLE_AIEN;
		} else {
			r &= ~TCS34725_ENABLE_AIEN;
		}
		write8(TCS34725_ENABLE, r);
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
		time++;
		if (time == 0) {
			setInterrupt(false);
		}
		if (time * 20 < 60) { //delay for 60 milliseconds
			return;
		}
		if (time * 20 == 60) {
			clear = read16(TCS34725_CDATAL);
			red = read16(TCS34725_RDATAL);
			green = read16(TCS34725_GDATAL);
			blue = read16(TCS34725_BDATAL);
		}
		if (time * 20 < 60 + 50) { //delay for 50 milliseconds
			return;
		}
		if (time * 20 == 60 + 60) {
			int sum = clear;
			float r, g, b;
			r = red;
			r /= sum;
			g = green; g /= sum;
			b = blue; b /= sum;
			r *= 256; g *= 256; b *= 256;
			cachedRed = (int) r;
			cachedGreen = (int) g;
			cachedBlue = (int) b;
			time = -1;
		}
	}
}
