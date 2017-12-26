package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Clock;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * The main class of the robot, constructs all the subsystems and initializes default commands.
 */
public class Robot extends IterativeRobot {

    /**
     * The absolute filepath to the resources folder containing the config files.
     */
    @NotNull
    private static final String RESOURCES_PATH = "/home/lvuser/449_resources/";

    /**
     * The object constructed directly from the yaml map.
     */
    private RobotInABoxMap robotMap;

    /**
     * The Notifier running the logging thread.
     */
    private Notifier loggerNotifier;

    /**
     * The method that runs when the robot is turned on. Initializes all subsystems from the map.
     */
    public void robotInit() {
        Clock.setStartTime();
        Clock.updateTime();

        //Yes this should be a print statement, it's useful to know that robotInit started.
        System.out.println("Started robotInit.");

        Yaml yaml = new Yaml();
        try {
            //Read the yaml file with SnakeYaml so we can use anchors and merge syntax.
            Map<?, ?> normalized = (Map<?, ?>) yaml.load(new FileReader(RESOURCES_PATH + "robot_in_a_box_map.yml"));
            YAMLMapper mapper = new YAMLMapper();
            //Turn the Map read by SnakeYaml into a String so Jackson can read it.
            String fixed = mapper.writeValueAsString(normalized);
            //Use a parameter name module so we don't have to specify name for every field.
            mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
            //Deserialize the map into an object.
            robotMap = mapper.readValue(fixed, RobotInABoxMap.class);
        } catch (IOException e) {
            //This is either the map file not being in the file system OR it being improperly formatted.
            System.out.println("Config file is bad/nonexistent!");
            e.printStackTrace();
        }

        //Read sensors
        robotMap.getUpdater().run();

        //Set fields from the map.
        this.loggerNotifier = new Notifier(robotMap.getLogger());

        //Start running the pose receiver.
        new Thread(robotMap.getPoseReceiverRunner()).start();

        //Log after init
        robotMap.getLogger().run();
    }

    /**
     * Run when we first enable in teleop.
     */
    @Override
    public void teleopInit() {
        //Refresh the current time.
        Clock.updateTime();

        //Read sensors
        robotMap.getUpdater().run();

        //Run startup command
        if (robotMap.getStartupCommand() != null) {
            robotMap.getStartupCommand().getCommand().start();
        }

        //Log
        loggerNotifier.startSingle(0);
    }

    /**
     * Run every tick in teleop.
     */
    @Override
    public void teleopPeriodic() {
        //Refresh the current time.
        Clock.updateTime();

        //Read sensors
        robotMap.getUpdater().run();

        //Run all commands. This is a WPILib thing you don't really have to worry about.
        Scheduler.getInstance().run();

        //Log
        loggerNotifier.startSingle(0);
    }
}
