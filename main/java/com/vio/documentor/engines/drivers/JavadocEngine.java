package com.vio.documentor.engines.drivers;

import com.vio.documentor.engines.AbstractEngine;
import com.vio.documentor.engines.annotations.Engine;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: nikelin
 * Date: May 17, 2010
 * Time: 4:07:57 PM
 * To change this template use File | Settings | File Templates.
 */
@Engine( id = "javadoc" )
public class JavadocEngine extends AbstractEngine {
    private static final Logger log = Logger.getLogger( DoxygenEngine.class );

    public String getExecutionCommand() {
        StringBuilder commandBuilder = new StringBuilder();

        commandBuilder.append( this.getExecutablePath() )
                      .append(" ")
                      .append( this.generateConfigInstructions() );

        return commandBuilder.toString();
    }
}
