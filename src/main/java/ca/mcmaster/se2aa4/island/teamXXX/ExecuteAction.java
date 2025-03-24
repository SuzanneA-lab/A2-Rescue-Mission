//2AA4 Assignment 2 - Island Exploration
package ca.mcmaster.se2aa4.island.teamXXX;

//Interface for executing drone actions
public interface ExecuteAction {
    String fly();
    String turn(String newDirection);
    String scan();
}
