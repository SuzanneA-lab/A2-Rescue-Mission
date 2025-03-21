package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class DroneStatus {
    private int battery = 100;
    private String status = ""; 
    private String direction = "";

    private void UpdateBattery(int cost){
        battery = battery - cost;
    }

    private int getBattery(){
        b = battery;
        return b;
    }

    private String getDirection(){

    }
}
