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
    @NotNull
    private final ZMQ.Socket socket;
    /**
     * The yaw of the camera, relative to the robot.
     */
    private final double cameraYaw;
    /**
     * The X and Y offsets of the camera, from the center of the robot, in feet.
     */
    private final double cameraXOffset, cameraYOffset;
    /**
     * The proto to read pose into
     */
    private Pose.CameraPose pose;

    /**
     * Default constructor.
     *
     * @param poseEstimator The pose estimator this is sending the camera pose to.
     * @param address       The address of the port on the RIO to open.
     * @param cameraYaw     The yaw of the camera, relative to the robot. Defaults to 0.
     * @param cameraXOffset The X offset of the camera, from the center of the robot, in feet. Defaults to 0.
     * @param cameraYOffset The Y offset of the camera, from the center of the robot, in feet. Defaults to 0.
     */
    @JsonCreator
    public JetsonPoseReceiver(@NotNull @JsonProperty(required = true) UnidirectionalPoseEstimator poseEstimator,
                              @NotNull @JsonProperty(required = true) String address,
                              double cameraYaw,
                              double cameraXOffset,
                              double cameraYOffset) {
        this.poseEstimator = poseEstimator;
        ZMQ.Context context = ZMQ.context(1);
        socket = context.socket(ZMQ.PAIR);
        socket.bind(address);
        this.cameraYaw = cameraYaw;
        this.cameraXOffset = cameraXOffset;
        this.cameraYOffset = cameraYOffset;
    }

    /**
     * Wait for the Jetson to send a proto, then read it and send that info to the pose estimator.
     */
    public void readData() {
        try {
            pose = pose.getParserForType().parseFrom(socket.recv());
            if (pose.hasNavXTime()) {
                poseEstimator.addAbsolutePos(pose.getX() + cameraXOffset, pose.getY() + cameraYOffset,
                        pose.getNavXTime(), pose.getYaw() + cameraYaw);
            } else {
                poseEstimator.addAbsolutePos(pose.getX() + cameraXOffset, pose.getY() + cameraYOffset,
                        System.currentTimeMillis(), pose.getYaw() + cameraYaw);
            }
        } catch (InvalidProtocolBufferException e) {
            System.out.println("Error reading proto!");
            e.printStackTrace();
        }
    }
}
