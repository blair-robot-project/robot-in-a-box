package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.protobuf.InvalidProtocolBufferException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.other.MotionProfileData;
import org.zeromq.ZMQ;
import proto.PathOuterClass;
import proto.PathRequestOuterClass;

/**
 * The object that requests a motion profile from the Jetson.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
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
    @JsonCreator
    public PathRequester(@NotNull @JsonProperty(required = true) String address) {
        ZMQ.Context context = ZMQ.context(1);
        socket = context.socket(ZMQ.REQ);
        socket.bind(address);
    }

    /**
     * Request a motion profile path for a given x, y, and angular displacement.
     *
     * @param x         The x displacement, in any unit.
     * @param y         The y displacement, in any unit.
     * @param theta     The angular displacement, in degrees.
     * @param deltaTime The time between setpoints in the profile, in seconds.
     */
    public void requestPath(double x, double y, double theta, double deltaTime) {
        //Send the request
        pathRequest = PathRequestOuterClass.PathRequest.newBuilder();
        pathRequest.setX(x);
        pathRequest.setY(y);
        pathRequest.setTheta(Math.toRadians(theta));
        pathRequest.setDt((int) (deltaTime * 1000)); //Convert to milliseconds
        socket.send(pathRequest.build().toByteArray());
    }

    /**
     * Get a motion profile path for a given x, y, and angular displacement.
     *
     * @param inverted      Whether or not to invert the profiles.
     * @param resetPosition Whether or not to reset position when the profile starts.
     * @return Null if the Jetson hasn't replied yet, a list of one profile if theta is 0, or a list of left, right
     * profiles in that order otherwise.
     */
    @Nullable
    public MotionProfileData[] getPath(boolean inverted, boolean resetPosition) {
        //Read from Jetson
        output = socket.recv(ZMQ.NOBLOCK);
        if (output == null) {
            return null;
        }

        //Make these local variables and not fields so that this thread doesn't retain any connection to it.
        MotionProfileData leftMotionProfileData, rightMotionProfileData = null;

        try {
            //Read the response
            path = path.getParserForType().parseFrom(output);
            leftMotionProfileData = new MotionProfileData(path.getPosLeftList(), path.getVelLeftList(),
                    path.getAccelLeftList(), path.getDeltaTime(), inverted, false, resetPosition);
            if (path.getPosRightCount() != 0) {
                rightMotionProfileData = new MotionProfileData(path.getPosRightList(), path.getVelRightList(),
                        path.getAccelRightList(), path.getDeltaTime(), inverted, false, resetPosition);
            }
        } catch (InvalidProtocolBufferException e) {
            System.out.println("Error reading proto!");
            e.printStackTrace();
            return null;
        }

        //Return stuff
        if (rightMotionProfileData == null) {
            return new MotionProfileData[]{leftMotionProfileData};
        } else {
            return new MotionProfileData[]{leftMotionProfileData, rightMotionProfileData};
        }
    }
}
