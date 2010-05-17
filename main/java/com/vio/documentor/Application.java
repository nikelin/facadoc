package com.vio.documentor;

import com.vio.applications.ApplicationException;
import com.vio.applications.IApplication;
import com.vio.config.IConfig;
import com.vio.documentor.config.Config;
import com.vio.config.readers.ConfigReaderException;
import com.vio.documentor.engines.EnginesFactory;
import com.vio.documentor.engines.GenerationException;
import com.vio.documentor.engines.IEngine;
import com.vio.utils.PackageLoader;
import com.vio.utils.ResourcesLoader;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: nikelin
 * Date: May 17, 2010
 * Time: 11:15:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class Application implements IApplication {
    private static final String CONFIG_PATH_ARG = "config";
    private static final String ENGINE_ARG = "engine";
    private static final String EXECUTABLE_PATH_ARG = "executable";
    private static final String HELP_ARG = "help";
    private static final String PROJECT_ARG = "project";

    private static ResourcesLoader resourcesLoader = new ResourcesLoader();
    private static PackageLoader packageLoader = new PackageLoader();

    private static final String[] importantProperties = new String[] {
                                            ENGINE_ARG, EXECUTABLE_PATH_ARG 
                                        };


    private static final Logger log = Logger.getLogger( Application.class );
    private Config config;
    private String[] envArgs;
    private Map<String, String> parameters = new HashMap<String, String>();

    public Application( String[] args ) {
        this.envArgs = args;
    }

    public void start() throws ApplicationException {
        try {
            this.initializeEnvironment();

            if ( !this.isEnvArg( CONFIG_PATH_ARG ) ) {
                this.processFromEnv();
            } else {
                this.processFromConfig( this.config = new Config( this.getEnvArg( CONFIG_PATH_ARG ) ) );
            }
        } catch ( GenerationException e ) {
            log.error("Generator engine exception!", e );
        } catch ( IllegalArgumentException e ) {
            log.error("Illegal argument value or insufficient arguments count!", e );
        } catch ( InstantiationException e ) {
            log.error("Cannot startup requested documentor engine instance!", e );
        } catch ( ConfigReaderException e ) {
            log.error("Unable to read initialization data from config.", e );
        } catch ( Throwable e ) {
            log.error("It seems that something goes wrong :-( ", e);
        }
    }

    protected void processFromConfig( Config config ) throws ConfigReaderException, GenerationException, InstantiationException {
        if ( !isEnvArg( PROJECT_ARG ) ) {
            for ( String projectId : config.getProjectsList() ) {
                this.processProject( projectId );
            }
        } else {
            if ( !getEnvArg( PROJECT_ARG ).contains(",") ) {
                this.processProject( getEnvArg( PROJECT_ARG ) );
            } else {
                for ( String projectId : getEnvArg( PROJECT_ARG ).split(",") ) {
                    this.processProject(projectId);
                }
            }
        }
    }

    protected void processProject( String projectId ) throws ConfigReaderException, GenerationException, InstantiationException {
        log.info("Processing documentation for project #" + projectId );

        this.validateConfigArgs(config, projectId);

        IEngine engine = EnginesFactory.getDefault().getEngine( config.getProjectEngine(projectId) );
        log.info("Engine ID: " + projectId );

        Map<String, Object> parameters = ( (Config) this.getConfig() ).getProjectParameters(projectId);
        log.info("Parameters: " + String.valueOf( parameters ) );
        for ( String property : parameters.keySet() ) {
            log.info("Parameter " + property + " with value " + String.valueOf( parameters.get(property) ) );
            engine.setProperty( property, parameters.get(property) );
        }

        engine.process();
        log.info("Documentation for project #" + projectId + " processed!");
    }

    protected void processFromEnv() throws GenerationException, ApplicationException, InstantiationException  {
        this.validateEnvArgs();

        String engineId = getEnvArg( ENGINE_ARG );
        if ( !EnginesFactory.getDefault().isExists(engineId) ) {
            throw new ApplicationException("Requested engine does not exists!");
        }

        IEngine engine = EnginesFactory.getDefault().getEngine( engineId );
        engine.setExecutablePath( getEnvArg( EXECUTABLE_PATH_ARG ) );

        for ( String property : this.getParameters().keySet() ) {
            engine.setProperty( property, this.getParameters().get(property) );
        }

        engine.process();

        log.info("Success!");
        System.exit(1);
    }

    public void stop() {
        log.info("Application exit.");
        System.exit(1);
    }

    public IConfig getConfig() {
        return this.config;
    }

    public Integer getCurrentVersion() {
        return 1;
    }

    public PackageLoader getPackagesLoader() {
        return packageLoader;
    }

    public ResourcesLoader getResourcesLoader() {
        return resourcesLoader;
    }

    public String getTempPath() {
        return this.getResourcesDir() + "/temp";
    }

    protected String _getRootDir() {
        return getEnvArg("root") != null ? getEnvArg("root") : Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    public String getRootDir() {
        return this._getRootDir().endsWith(".jar") ? this._getRootDir().substring( 0, this._getRootDir().lastIndexOf("/") ) : this._getRootDir();
    }

    public String getResourcesDir() {
        return getRootDir() + "/resources";
    }

    public String getExecutionMode() {
        return null;
    }

    private void setEnvArg( String name, String value ) {
        this.parameters.put( name, value );
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public boolean isEnvArg( String name ) {
        return this.parameters.containsKey(name);
    }

    public String getEnvArg( String name ) {
        return this.parameters.get(name);
    }

    private void initializeEnvironment() {
        String currName = null;
        for ( String arg : this.envArgs ) {
            if ( arg.startsWith("-") ) {
                currName = arg.substring(1);
            } else {
                if ( null != currName ) {
                    setEnvArg( currName, arg );
                }
            }
        }
    }

    private void validateConfigArgs( Config config, String projectId ) throws ConfigReaderException {
        for ( String id : config.getProjectsList() ) {
            if ( id == projectId ) {
                if ( config.getProjectEngine( id ) == null || config.getProjectEngine(id).isEmpty() ) {
                    throw new ConfigReaderException("There is no engine applied for project with #id=" + id );
                }

                if ( config.getProjectInputPath( id ) == null || config.getProjectEngine(id).isEmpty() ) {
                    throw new ConfigReaderException("There is no input path applied for project with #id=" + id );
                }

                if ( config.getProjectOutputPath( id ) == null || config.getProjectEngine(id).isEmpty() ) {
                    throw new ConfigReaderException("There is no output path applied for project with #id=" + id );
                }

                break;
            }
        }
    }

    private void validateEnvArgs() {
        if ( this.isEnvArg( HELP_ARG ) ) {
            return;
        }

        for ( String property : importantProperties ) {
            if ( !this.isEnvArg(property) || this.getEnvArg(property).isEmpty() ) {
                throw new IllegalArgumentException("Property `" + property + "` value must be given!");
            }
        }
    }
}
