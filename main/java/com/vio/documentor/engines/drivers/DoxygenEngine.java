package com.vio.documentor.engines.drivers;

import com.vio.documentor.Application;
import com.vio.documentor.engines.AbstractEngine;
import com.vio.documentor.engines.annotations.Engine;
import com.vio.utils.Registry;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: nikelin
 * Date: May 14, 2010
 * Time: 3:08:52 PM
 * To change this template use File | Settings | File Templates.
 */
@Engine( id = "doxygen" )
public class DoxygenEngine extends AbstractEngine {
    private static final Logger log = Logger.getLogger( DoxygenEngine.class );

    @Override
    protected String generateConfigInstructions() {
        StringBuilder instructions = new StringBuilder();

        for ( String property : this.getProperties().keySet() ) {
            Object value = this.getProperty(property);

            instructions.append( property )
                        .append(" = ")
                        .append( value )
                        .append(" \n");
        }

        log.info("Instructions: " + instructions.toString() );

        return instructions.toString();
    }

    public String getExecutionCommand() throws IOException {
        StringBuilder commandBuilder = new StringBuilder();

        commandBuilder.append( this.getExecutablePath() );
        commandBuilder.append( " " );
        commandBuilder.append( this.generateConfig() );

        return commandBuilder.toString();
    }

    /**
     * Generate temprorary config file and return path
     */
    protected String generateConfig() throws IOException {
        String configPath = (String) this.getProperty("configPath");
        if ( configPath != null ) {
            return configPath;
        }

        /**
         * @FIXME: расширить интерфейс IApplication или придумать чё-то другое оставил на потом
         */
        String tmpPath = ( (Application) Registry.getApplication() ).getRootDir() + "/temp/";
        configPath = tmpPath + "/" + this.getName() + "-" + Thread.currentThread().getId() + ".cfg";

        log.info("Config file path: " + configPath );

        File configFile = new File(configPath);
        configFile.delete();
        configFile.createNewFile();
        configFile.setWritable(true);
        configFile.setReadable(true);

        BufferedWriter configWriter = new BufferedWriter( new FileWriter( configFile ) );
        configWriter.write( this.generateConfigInstructions() );
        configWriter.close();

        return configFile.getAbsolutePath();
    }

}
