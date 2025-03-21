package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

// Explorer class based on IExplorerRaid interface
public class makeDecision{
    private JSONOBJECT converter = new JSONOBJECT(new JSONTokener(new StringReader(s)));
    
    private decideNextMove(JSONOBJECT radarData){
        String radar = converter.getString("");
    }
}
