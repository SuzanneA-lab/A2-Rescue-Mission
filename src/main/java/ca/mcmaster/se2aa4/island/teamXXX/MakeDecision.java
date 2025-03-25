//2AA4 Assignment 2 - Island Exploration
package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

//MakeDecision class responsible for managing direction of drone and deciding next move based on radar input
public class MakeDecision{
    private String direction;
    private int turncounter = 0; //manages how many times in a row drone has turned, if it's made a complete 360, will fly forward even if no land is in front of it

    //Constructor that takes in info JSONOBJECT in initializatino stage to get direction
    public MakeDecision(JSONObject info){
        direction = info.getString("heading");
    }
    
    //decideNextMove method responsible that takes in radarData and returns either instructions to "fly" or the direction the drone should turn in
    public String decideNextMove(JSONObject radarData){
        String radar = radarData.getString("found");

        if ("GROUND".equals(radar) || turncounter == 4) { //drone flies if ground is located in front of it or if it's already turned in all directions
            turncounter = 0;
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

            turncounter++;
            return direction;
        }
    }
}
