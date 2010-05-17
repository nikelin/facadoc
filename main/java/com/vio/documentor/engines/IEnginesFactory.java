package com.vio.documentor.engines;

import java.util.Collection;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: nikelin
 * Date: May 14, 2010
 * Time: 3:09:27 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IEnginesFactory {

    public <T extends IEngine> T getEngine( String id ) throws InstantiationException;

    public <T extends IEngine> T getEngine( Class<T> clazz ) throws InstantiationException;

    public boolean isExists( String id );
    
    public Collection<Class<? extends IEngine>> getEngines();

    public <T extends IEngine> Class<T> getEngineClass( String id );

    public void registerEngine( String id, Class<? extends IEngine> engine );

}
