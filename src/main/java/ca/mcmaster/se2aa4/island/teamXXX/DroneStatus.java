//2AA4 Assignment 2 - Island Exploration
package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

//DroneStatus class manages battery and range status of the drone
public class DroneStatus {
    private int battery;
    private String status = "";
    private String heading = ""; //Direction the drone is facing

    //Constructor that takes in info JSONObject at initialization stage to get current battery and heading
    public DroneStatus(JSONObject info) {
        battery = info.getInt("budget");
        heading = info.getString("heading");
    }

    //Default constructor for cases where no initialization data is provided
    public DroneStatus() {
        battery = 7000; //Default battery level
        heading = "N"; //Default heading
    }

    //Takes in the cost of the previous move and deducts it from the current battery
    public void updateBattery(int cost) {
        battery -= cost;
    }

    //Returns the current battery level
    public int getBattery() {
        return battery;
    }

    //Returns the current heading of the drone
    public String getHeading() {
        return heading;
    }

    //Updates the heading of the drone
    public void setHeading(String newHeading) {
        heading = newHeading;
    }

    //Checks both the battery and range status of the drone through the response JSONObject
    //Returns true if the drone is still fine to proceed, and false if it is either out of battery or out of range
    public Boolean getStatus(JSONObject response) {
        status = response.optString("status", "UNKNOWN");
        return battery > 0 && "OK".equals(status);
    }
}
