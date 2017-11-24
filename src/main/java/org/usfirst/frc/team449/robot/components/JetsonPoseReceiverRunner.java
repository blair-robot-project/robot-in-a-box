package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedRunnable;

/**
 * An object to constantly get pose from the Jetson and give it to the the pose estimator.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class JetsonPoseReceiverRunner implements MappedRunnable{

	/**
	 * Receives data from the Jetson and gives it to a pose estimator.
	 */
	@NotNull
	private final JetsonPoseReceiver poseReceiver;

	/**
	 * Whether or not to stop after the next pose received.
	 */
	private boolean stop;

	/**
	 * Default constructor.
	 *
	 * @param poseReceiver Receives data from the Jetson and gives it to a pose estimator.
	 */
	@JsonCreator
	public JetsonPoseReceiverRunner(@NotNull @JsonProperty(required = true) JetsonPoseReceiver poseReceiver) {
		this.poseReceiver = poseReceiver;
		stop = false;
	}

	/**
	 * Constantly read data until stop() is called.
	 */
	@Override
	public void run() {
		while (!stop){
			poseReceiver.readData();
		}
		stop = false;
	}

	/**
	 * Stop reading data from the Jetson.
	 */
	public void stop(){
		stop = true;
	}
}
