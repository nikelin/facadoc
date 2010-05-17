package com.vio.documentor;

import com.vio.documentor.engines.EnginesFactory;
import com.vio.documentor.engines.GenerationException;
import com.vio.documentor.engines.IEngine;
import com.vio.utils.Registry;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: nikelin
 * Date: May 14, 2010
 * Time: 2:53:03 PM
 * To change this template use File | Settings | File Templates.
 */
public final class Main {
    private static Logger log = Logger.getLogger( Main.class );

    public static void main( String[] args ) {
        Application app = new Application(args);
        try {
            Registry.setApplication(app);

            app.start();
        } catch ( Throwable e ) {
            log.error("Something going impossibly wrong.", e);
        }
    }


}
