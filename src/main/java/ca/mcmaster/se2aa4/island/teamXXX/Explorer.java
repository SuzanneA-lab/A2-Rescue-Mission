//2AA4 Assignment 2 - Island Exploration
package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;

import eu.ace_design.island.bot.IExplorerRaid;

//Explorer class based on IExplorerRaid interface
public class Explorer implements IExplorerRaid, ExecuteAction {
    private final Logger logger = LogManager.getLogger();
    private DroneStatus droneStatus;
    private MakeDecision decisionMaker;
    private List<String> foundCreeks; //List to store found creeks/emergency sites
    private String emergencySite; //Identifier for the emergency site

    //Constructor
    public Explorer() {
        this.droneStatus = new DroneStatus(); //Default initialization
        this.decisionMaker = null; //Will be initialized in the "initialize" method
        this.foundCreeks = new ArrayList<>(); //Initialize the list of found creeks
        this.emergencySite = null; // Initialize the emergency site as null
    }

    @Override
    public void initialize(String s) {
        try {
            // Parse the initialization JSON string
            JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));

            // Initialize DroneStatus and MakeDecision with the parsed JSON
            droneStatus = new DroneStatus(info);
            decisionMaker = new MakeDecision(info);

            logger.info("** Initialization successful");
        } catch (Exception e) {
            logger.error("Error during initialization: {}", e.getMessage());
        }
    }

    @Override
    public String takeDecision() {
        logger.info("** Taking decision");
        JSONObject radarData;

        try {
            //Perform a scan and parse the radar data
            radarData = new JSONObject(scan());
            logger.info("** Radar data received: {}", radarData.toString());

            //Check if a creek or emergency site is found
            if ("CREEK".equals(radarData.optString("found", ""))) {
                foundCreeks.add(radarData.toString());
                logger.info("** Creek found and added to the list!");
            } else if ("EMERGENCY_SITE".equals(radarData.optString("found", ""))) {
                emergencySite = radarData.optString("id", "UNKNOWN");
                logger.info("** Emergency site found with ID: {}", emergencySite);
            }
        } catch (Exception e) {
            logger.error("Error during scan: {}", e.getMessage());
            return "{}"; //Return an empty decision in case of failure
        }

        //Use MakeDecision to determine the next action
        String action = decisionMaker.decideNextMove(radarData);
        JSONObject decision = new JSONObject();

        try {
            //Execute the appropriate action based on the decision
            if ("fly".equals(action)) {
                decision = new JSONObject(fly());
            } else {
                decision = new JSONObject(turn(action)); //Use the direction returned by MakeDecision
            }
            logger.info("** Decision executed: {}", decision.toString());
        } catch (Exception e) {
            logger.error("Error executing action: {}", e.getMessage());
        }

        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        try {
            //Parse the response JSON string
            JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
            logger.info("** Response received:\n{}", response.toString(2));

            //Update the drone's battery based on the cost of the last action
            int cost = response.getInt("cost");
            droneStatus.updateBattery(cost); //Use DroneStatus to update the battery
            logger.info("** Battery updated. Remaining: {}", droneStatus.getBattery());

            //Check the drone's status
            if (!droneStatus.getStatus(response)) {
                logger.warn("** Drone status is not OK: {}", response.optString("status", "UNKNOWN"));
            }
        } catch (Exception e) {
            logger.error("Error acknowledging results: {}", e.getMessage());
        }
    }

    @Override
    public String deliverFinalReport() {
        logger.info("** Delivering final report");
        //Return a report based on whether the drone is still operational and the creeks/emergency site found
        String report = droneStatus.getBattery() > 0
                ? "Mission complete! Creeks found: " + foundCreeks
                : "Mission failed: Out of battery!";
        if (emergencySite != null) {
            report += " Emergency site located with ID: " + emergencySite;
        } else {
            report += " Emergency site not located.";
        }
        return report;
    }

    //Main game loop
    public void startMission(String initializationData) {
        initialize(initializationData);

        boolean missionComplete = false;
        while (!missionComplete) {
            //Take a decision and execute it
            String decision = takeDecision();
            acknowledgeResults(decision);

            //Check if the mission should terminate
            missionComplete = checkTerminationConditions();
        }

        //Deliver the final report at the end of the mission
        String finalReport = deliverFinalReport();
        logger.info("** Final Report: {}", finalReport);
    }

    private boolean checkTerminationConditions() {
        //Terminate if the battery is too low or both the emergency site and at least one creek are found
        return droneStatus.getBattery() <= 10 || (emergencySite != null && !foundCreeks.isEmpty());
    }

    //Implementing ExecuteAction methods directly in Explorer
    @Override
    public String fly() {
        // Returns a JSON string representing the "fly" action
        return new JSONObject().put("action", "fly").toString();
    }

    @Override
    public String turn(String newDirection) {
        //Returns a JSON string representing the "turn" action with the specified direction
        return new JSONObject().put("action", "turn").put("direction", newDirection).toString();
    }

    @Override
    public String scan() {
        //Returns a JSON string representing the "scan" action
        return new JSONObject().put("action", "scan").toString();
    }
}
