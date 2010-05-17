package com.vio.documentor.engines;

import com.vio.config.readers.ConfigReaderException;
import com.vio.documentor.config.Config;
import com.vio.documentor.engines.annotations.Engine;
import com.vio.utils.InterfacesFilter;
import com.vio.utils.PackageLoaderException;
import com.vio.utils.Registry;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: nikelin
 * Date: May 14, 2010
 * Time: 3:09:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class EnginesFactory implements IEnginesFactory {
    private static final Logger log = Logger.getLogger( EnginesFactory.class );
    
    private static IEnginesFactory defaultInstance = new EnginesFactory();
    private Map<String, Class<? extends IEngine>> engineClasses = new HashMap<String, Class<? extends IEngine>>();
    private Map<Class<? extends IEngine>, IEngine> engineInstances = new HashMap<Class<? extends IEngine>, IEngine>();

    public EnginesFactory() {
        this.initializeEngines();
    }

    public static IEnginesFactory getDefault() {
        return defaultInstance;
    }

    public <T extends IEngine> T getEngine( String id ) throws InstantiationException {
        Class<T> engineClazz = this.getEngineClass( id );
        if ( engineClazz == null ) {
            throw new InstantiationException("Engine for id (" + id + ") not found");
        }

        return this.getEngine(engineClazz);
    }

    public <T extends IEngine> T getEngine( Class<T> clazz ) throws InstantiationException {
        T engine = (T) this.engineInstances.get(clazz);
        try {
            engine = clazz.newInstance();
            this.engineInstances.put( clazz, engine );
        } catch ( Throwable e ) {
            throw new InstantiationException();
        }

        try {
            Config config = ( (Config) Registry.getApplication().getConfig() );
            
            String engineId = this.getEngineId(clazz );
            String engineExecutablePath = config.getEngineExecutablePath( engineId );

            log.info("Engine " + engineId + " related to executable " +  engineExecutablePath );

            engine.setExecutablePath( engineExecutablePath );
        } catch( ConfigReaderException e ) {
            throw new InstantiationException("Engine configuration exception");
        }

        return engine;
    }

    public boolean isExists( String id ) {
        return this.engineClasses.containsKey(id);
    }

    public <T extends IEngine> Class<T> getEngineClass( String id ) {
        return (Class<T>) this.engineClasses.get(id);
    }

    public <T extends IEngine> String getEngineId( Class<T> clazz ) {
        for( String id : this.engineClasses.keySet() ) {
            if ( this.engineClasses.get(id).equals(clazz) ) {
                return id;
            }
        }

        return null;
    }

    public Collection<Class<? extends IEngine>> getEngines() {
        return this.engineClasses.values();
    }

    public void registerEngine( String id, Class<? extends IEngine> clazz ) {
        if ( this.engineClasses.containsKey(id) ) {
            return;
        }

        this.engineClasses.put( id, clazz );
    }

    protected void initializeEngines() {
        try {
            Class<? extends IEngine>[] engines = Registry.getApplication().getPackagesLoader().<IEngine>getClasses("com.vio.documentor.engines.drivers", true, new InterfacesFilter( new Class[]{ IEngine.class }, new Class[] { Engine.class }, true ) );
            if ( engines.length == 0 ) {
                log.info("No engine class type present in classpath");
                return;
            }

            for ( Class<? extends IEngine> engineClazz : engines ) {
                String id = engineClazz.getAnnotation(Engine.class).id();
                this.engineClasses.put( id, engineClazz );
                log.info("Engine with ID=" + id + " successfully registered!" );
            }
        } catch ( PackageLoaderException e ) {
            log.error("Packages loading exception", e );
        }
    }

}
