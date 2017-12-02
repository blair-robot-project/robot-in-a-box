package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.InvalidProtocolBufferException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.other.LoadableMotionProfileData;
import org.zeromq.ZMQ;
import proto.PathOuterClass;
import proto.PathRequestOuterClass;

/**
 * The object that requests a motion profile from the Jetson.
 */
public class PathRequester {

	/**
	 * The socket for communicating with the Jetson.
	 */
	@NotNull
	private final ZMQ.Socket socket;

	/**
	 * The path received from the Jetson. Field to avoid garbage collection.
	 */
	private PathOuterClass.Path path;

	/**
	 * The proto for requesting a motion profile from the Jetson. Field to avoid garbage collection.
	 */
	private PathRequestOuterClass.PathRequest.Builder pathRequest;

	/**
	 * The bytes read from the socket. Field to avoid garbage collection.
	 */
	private byte[] output;

	/**
	 * Default constructor.
	 *
	 * @param address The address of the port on the RIO to open.
	 */
	public PathRequester(@NotNull @JsonProperty(required = true) String address) {
		ZMQ.Context context = ZMQ.context(1);
		socket = context.socket(ZMQ.REQ);
		socket.bind(address);
	}

	/**
	 * Request a motion profile path for a given x, y, and angular displacement.
	 *
	 * @param x             The x displacement, in any unit.
	 * @param y             The y displacement, in any unit.
	 * @param theta         The angular displacement, in radians.
	 */
	public void requestPath(double x, double y, double theta) {
		//Send the request
		pathRequest = PathRequestOuterClass.PathRequest.newBuilder();
		pathRequest.setX(x);
		pathRequest.setY(y);
		pathRequest.setTheta(theta);
		socket.send(pathRequest.build().toByteArray());
	}

	/**
	 * Get a motion profile path for a given x, y, and angular displacement.
	 * @param inverted      Whether or not to invert the profiles.
	 * @param resetPosition Whether or not to reset position when the profile starts.
	 * @return Null if the Jetson hasn't replied yet, a list of one profile if theta is 0, or a list of left, right
	 * profiles in that order otherwise.
	 */
	@Nullable
	public LoadableMotionProfileData[] getPath(boolean inverted, boolean resetPosition) {
		//Read from Jetson
		output = socket.recv(ZMQ.NOBLOCK);
		if (output == null) {
			return null;
		}

		//Make these local variables and not fields so that this thread doesn't retain any connection to it.
		LoadableMotionProfileData leftMotionProfileData, rightMotionProfileData = null;

		try {
			//Read the response
			path = path.getParserForType().parseFrom(output);
			leftMotionProfileData = new LoadableMotionProfileData(path.getPosLeftList(), path.getVelLeftList(),
					path.getAccelLeftList(), path.getDeltaTime(), inverted, false, resetPosition);
			if (path.getPosRightCount() != 0) {
				rightMotionProfileData = new LoadableMotionProfileData(path.getPosRightList(), path.getVelRightList(),
						path.getAccelRightList(), path.getDeltaTime(), inverted, false, resetPosition);
			}
		} catch (InvalidProtocolBufferException e) {
			System.out.println("Error reading proto!");
			e.printStackTrace();
			return null;
		}

		//Return stuff
		if (rightMotionProfileData == null) {
			return new LoadableMotionProfileData[]{leftMotionProfileData};
		} else {
			return new LoadableMotionProfileData[]{leftMotionProfileData, rightMotionProfileData};
		}
	}
}
