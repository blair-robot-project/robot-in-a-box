package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.protobuf.InvalidProtocolBufferException;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.UnidirectionalPoseEstimator;
import org.zeromq.ZMQ;
import proto.Pose;

/**
 * The class that interacts with the Jetson's pose publisher.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class JetsonPoseReceiver {

	/**
	 * The pose estimator this is sending the camera pose to.
	 */
	@NotNull
	private final UnidirectionalPoseEstimator poseEstimator;

	/**
	 * The socket the Jetson is sending info over.
	 */
	private final ZMQ.Socket socket;

	/**
	 * The proto to read pose into
	 */
	private Pose.CameraPose pose;

	/**
	 * Default constructor.
	 *
	 * @param poseEstimator The pose estimator this is sending the camera pose to.
	 */
	@JsonCreator
	public JetsonPoseReceiver(@NotNull @JsonProperty(required = true) UnidirectionalPoseEstimator poseEstimator){
		this.poseEstimator = poseEstimator;
		ZMQ.Context context = ZMQ.context(1);
		socket = context.socket(ZMQ.PAIR);
		socket.bind("10.4.49.2:5555");
	}

	/**
	 * Wait for the Jetson to send a proto, then read it and send that info to the pose estimator.
	 */
	public void readData(){
		try {
			pose = pose.getParserForType().parseFrom(socket.recv());
			if (pose.hasNavXTime()){
				poseEstimator.addAbsolutePos(pose.getX(), pose.getY(), pose.getNavXTime(), pose.getYaw());
			} else {
				poseEstimator.addAbsolutePos(pose.getX(), pose.getY(), System.currentTimeMillis(), pose.getYaw());
			}
		} catch (InvalidProtocolBufferException e) {
			System.out.println("Error reading proto!");
			e.printStackTrace();
		}
	}
}
