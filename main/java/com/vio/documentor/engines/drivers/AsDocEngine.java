package com.vio.documentor.engines.drivers;

import com.vio.documentor.engines.AbstractEngine;
import com.vio.documentor.engines.annotations.Engine;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: nikelin
 * Date: May 17, 2010
 * Time: 7:00:19 PM
 * To change this template use File | Settings | File Templates.
 */
@Engine( id = "asdoc" )
public class AsDocEngine extends AbstractEngine {
    private static final Logger log = Logger.getLogger( AsDocEngine.class );

    public String getExecutionCommand() {
        StringBuilder commandBuilder = new StringBuilder();

        commandBuilder.append( this.getExecutablePath() )
                      .append(" ")
                      .append( this.generateConfigInstructions() );

        log.info("asdoc command: " + commandBuilder.toString() );

        return commandBuilder.toString();
    }
}
