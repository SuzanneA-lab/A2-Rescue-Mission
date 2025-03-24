//2AA4 Assignment 2 - Island Exploration
package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

//MakeDecision class responsible for managing direction of drone and deciding next move based on radar input
public class MakeDecision{
    private String direction;

    //Constructor that takes in info JSONOBJECT in initializatino stage to get direction
    public MakeDecision(JSONObject info){
        direction = info.getString("heading");
    }
    
    //decideNextMove method responsible that takes in radarData and returns either instructions to "fly" or the direction the drone should turn in
    public String decideNextMove(JSONObject radarData){
        String radar = radarData.getString("found");
        if ("GROUND".equals(radar)) { //drone flies if ground is located in front of it
            return "fly";
        }

        else { //Drone turns to its left if no ground is located in front of it
            if (direction.equals("N")){
                direction = "W";
            }

            else if (direction.equals("E")){
                direction = "N";   
            }

            else if (direction.equals("S")){
                direction = "E";
            }

            else if (direction.equals("W")){
                direction = "S";
            }

            return direction;
        }
    }
}
