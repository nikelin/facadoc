package com.vio.documentor.engines;

import com.vio.documentor.Application;
import com.vio.documentor.engines.annotations.Engine;
import com.vio.utils.Registry;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.*;

/**
 * Created by IntelliJ IDEA.
 * User: nikelin
 * Date: May 14, 2010
 * Time: 5:47:55 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractEngine implements IEngine {
    private static final Logger log = Logger.getLogger( AbstractEngine.class );

    private String outputPath;
    private String inputPath;
    private String executablePath;
    private Map<String, Object> properties = new HashMap<String, Object>();

    public void setInputPath( String value ) {
        this.inputPath = value;
    }

    public String getInputPath() {
        return this.outputPath;
    }

    public String getOutputPath() {
        return this.outputPath;
    }

    public void setOutputPath( String value ) {
        this.outputPath = value;
    }

    public void setExecutablePath( String value ) {
        this.executablePath = value;
    }

    public String getExecutablePath() {
        return this.executablePath;
    }

    public void setProperty( String name, Object value ) {
        this.properties.put( name, value );
    }

    public Object getProperty( String name ) {
        return this.properties.get(name);
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public String getName() {
        return this.getClass().getAnnotation(Engine.class).id();
    }

    public void process() throws GenerationException {
        try {
            final Process process = Runtime.getRuntime().exec( this.generateExecFile().getAbsolutePath() );

            Thread readThread = new Thread() {
                @Override
                public void run() {
                    try {
                        log.info("Documentation result returned: ");
                        BufferedReader returnReader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );

                        String line;
                        while( null != ( line = returnReader.readLine() ) ) {
                            log.info( line );
                        }
                    } catch ( Throwable e ) {
                        log.error("Cannot read from input pipe", e);
                    }
                }
            };

            readThread.start();
        } catch ( Throwable e ) {
            log.error( e.getMessage(), e );
            throw new GenerationException();
        }
    }

    protected File generateExecFile() throws IOException {
        File tempDir = new File( ( (Application) Registry.getApplication() ).getRootDir() + "/temp/" );
        if ( !tempDir.exists() ) {
            tempDir.mkdir();
            tempDir.setWritable(true);
        }

        File execFile = new File( tempDir.getAbsolutePath() + "/" + this.getName() + "_exec.sh" );
        if ( !execFile.exists() ) {
            execFile.setWritable(true);
            execFile.setReadable(true);
            execFile.createNewFile();
        }

        execFile.setExecutable(true);

        FileWriter execWriter = new FileWriter( execFile );
        log.info( "Writing to " + execFile.getAbsolutePath() + ": " + this.getExecutionCommand() );
        execWriter.write( this.getExecutionCommand() );
        execWriter.close();

        return execFile;
    }

    protected String generateConfigInstructions() {
        StringBuilder instructions = new StringBuilder();

        for ( String property : this.getProperties().keySet() ) {
            instructions.append( property )
                        .append(" ")
                        .append( this.getProperty(property) )
                        .append(" ");
        }

        log.info("Instructions: " + instructions.toString() );

        return instructions.toString();
    }

}
