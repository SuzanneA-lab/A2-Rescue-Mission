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
    private int n = 1; //represents the current turn: 1 == radar, 2 == move, 3 == scan
    private String decision; //represents the current decision the drone has made about which action to take next

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
        if (n==1){  //if current move is using radar
            decision = (new JSONObject(radar(droneStatus.getHeading()))).toString();
        }

        else if (n==3){ //if current move is using scan
            decision = (new JSONObject(scan())).toString();
        }

        logger.info("** Decision executed: {}", decision.toString());
        return decision; //other cases, drone should use decision set in acknowledgeResults method
        
        } catch (Exception e) {
            logger.error("Error during scan: {}", e.getMessage());
            return "{}"; //Return an empty decision in case of failure
        }
    }

    @Override
    public void acknowledgeResults(String s) {
        try {
            //Parse the response JSON string
            JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
            logger.info("** Response received:\n{}", response.toString(2));

            //if previous move was radar
            if (n==1){
                String action = decisionMaker.decideNextMove(response);

                if ("fly".equals(action)) {
                    decision = (new JSONObject(fly())).toString();
                } 
                
                else {
                    decision = (new JSONObject(turn(action))).toString(); //Use the direction returned by MakeDecision
                }
            
                n++;
            }

            //if previous move was flying or turning
            else if (n==2){
                n++;
            }

            //if previous move was scan
            else {
                //check for creeks and emergency site
                if ("CREEK".equals(response.optString("found"))) {
                    foundCreeks.add(response.toString());
                    logger.info("** Creek found and added to the list!");
                } else if ("EMERGENCY_SITE".equals(response.optString("found"))) {
                    emergencySite = response.optString("id", "UNKNOWN");
                    logger.info("** Emergency site found with ID: {}", emergencySite);
                }

                n=1;
            }
            
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

    //Implementing ExecuteAction methods directly in Explorer
    @Override
    public String fly() {
        // Returns a JSON string representing the "fly" action
        return new JSONObject().put("action", "fly").toString();
    }

    @Override
    public String turn(String newDirection) {
        JSONObject command = new JSONObject();
        JSONObject parameters = new JSONObject();

        parameters.put("direction", newDirection);
        command.put("action","heading");
        command.put("parameters", parameters);

        return command.toString();
    }

    @Override
    public String scan() {
        //Returns a JSON string representing the "scan" action
        return new JSONObject().put("action", "scan").toString();
    }

    @Override
    public String radar(String Direction) {
        //Returns a JSON string representing the "radar" action with the current direction
        JSONObject command = new JSONObject();
        JSONObject parameters = new JSONObject();

        parameters.put("direction", Direction);
        command.put("action","echo");
        command.put("parameters", parameters);

        return command.toString();
    }
}
