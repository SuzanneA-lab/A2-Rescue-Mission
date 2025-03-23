package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

//DroneStatus class manages battery and range status of drone
public class DroneStatus {
    private int battery;
    private String status = ""; 
    //private String direction = ""; // maybe better to remove ?

    //constructor that takes in info JSONOBJECT at initialization stage to get current battery
    public DroneStatus(JSONOBJECT info){
        battery = info.getInt("budget");
    }

    //takes in cost of previous move and deducts it from current battery
    private void UpdateBattery(int cost){
        battery = battery - cost;
    }

    //maybe not neccesary? could be completely replaced with getStatus method
    private int getBattery(){ 
        b = battery;
        return b;
    }

    //getStatus method checks both the battery and range status of drone through the response JSONOBJECT
    //returns true if drone is still fine to proceed, and false if it is either out of battery or out of range
    private Boolean getStatus(JSONOBJECT response){
        status = response.getString("status");
        
        if (battery > 0 && status.equals("OK")){
            return true;
        }

        else {
            return false;
        }
    }
}
