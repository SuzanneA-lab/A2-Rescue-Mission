package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

// MakeDecision class responsible for managing direction of drone and deciding next move based on radar input
public class MakeDecision{
    //private JSONOBJECT converter = new JSONOBJECT(new JSONTokener(new StringReader(s)));
    private String direction;

    //constructor that takes in info JSONOBJECT in initializatino stage to get direction
    public MakeDecision(JSONObject info){
        direction = info.getString("heading");
    }
    
    //decideNextMove method responsible that takes in radarData and returns either instructions to "fly" or the direction the drone should turn in
    private String decideNextMove(JSONOBJECT radarData){
        String radar = converter.getString("found");
        if (radar == "GROUND"){
            return "fly";
        }

        else { //drone turns to its left if no ground is located in front of it
            if (direction.equals("N")){
                direction = "W";
            }

            else if (direction.equals("E")){
                direction = "N";   
            }

            else if (direction.equals("S")){
                direction = "E"
            }

            else if (direction.equals("W")){
                direction = "S";
            }

            return direction;
        }
    }
}
