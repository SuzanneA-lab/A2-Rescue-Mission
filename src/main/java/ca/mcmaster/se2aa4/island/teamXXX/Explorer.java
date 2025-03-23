package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

//Explorer class based on IExplorerRaid interface
public class Explorer implements IExplorerRaid {
    private final Logger logger = LogManager.getLogger();
    private DroneStatus droneStatus = new DroneStatus();
    private MakeDecision decisionMaker = new MakeDecision();
    private ExecuteAction actionExecutor = new DroneExecutor();

    @Override
    public void initialize(String s) {
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        droneStatus = new DroneStatus(info);
        decisionMaker = new MakeDecision(info);
    }

    @Override
    public String takeDecision() {
        logger.info("** Taking decision");
        JSONObject radarData = new JSONObject(actionExecutor.scan());
        String action = decisionMaker.decideNextMove(radarData);
        
        JSONObject decision = new JSONObject();
        if ("fly".equals(action)) {
            decision = new JSONObject(actionExecutor.fly());
        } else {
            decision = new JSONObject(actionExecutor.turn("left"));
        }
        
        logger.info("** Decision: {}", decision.toString());
        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Response received:\n{}", response.toString(2));
        
        int cost = response.getInt("cost");
        droneStatus.updateBattery(cost);
    }

    @Override
    public String deliverFinalReport() {
        logger.info("** Delivering final report");
        return "no creek found";
    }
}
