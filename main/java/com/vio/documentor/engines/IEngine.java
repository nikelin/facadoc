package com.vio.documentor.engines;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: nikelin
 * Date: May 14, 2010
 * Time: 2:54:40 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IEngine {

    public void setExecutablePath( String value );

    public String getExecutablePath();

    public void setProperty( String name, Object value );

    public Object getProperty( String name );

    public String getExecutionCommand() throws IOException;

    public void process() throws GenerationException;
    
}
