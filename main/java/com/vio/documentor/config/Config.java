package com.vio.documentor.config;

import com.vio.config.AbstractConfig;
import com.vio.config.readers.ConfigReaderException;
import com.vio.utils.Registry;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: nikelin
 * Date: May 17, 2010
 * Time: 11:07:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class Config extends AbstractConfig {
    public static String DEFAULT_PATH = "resources/config/config.xml";
    public static String NOT_ESCAPE_CMD = "@not-escape";
    public final static Pattern NON_PRINTABLE = Pattern
            .compile("[^\t\n\r\u0020-\u007E\u0085\u00A0-\uD7FF\uE000-\uFFFC]");

    public Config() throws ConfigReaderException, IOException {
        this( DEFAULT_PATH );
    }

    public Config( String path ) throws ConfigReaderException, IOException {
        this( Registry.getApplication().getResourcesLoader().loadFile( path ) );
    }

    public Config( File file ) throws ConfigReaderException {
        super(file);
    }

    public List<String> getProjectsList() throws ConfigReaderException {
        return this.getReader().readList("//project/@name");
    }

    public String getProjectOutputPath( String name ) throws ConfigReaderException {
        return this.getValue("//project[@name='" + name + "']/outputPath/text()");
    }

    public String getProjectInputPath( String name ) throws ConfigReaderException {
        return this.getValue("//project[@name='" + name + "']/inputPath/text()" );
    }

    public String getProjectEngine( String name ) throws ConfigReaderException {
        return this.getValue("//project[@name='" + name + "']/engine/text()");
    }

    public String getEngineExecutablePath( String id ) throws ConfigReaderException {
        return this.getValue("//engine[@id='" + id + "']/executablePath/text()");
    }

    public Map<String, Object> getProjectParameters( String id ) throws ConfigReaderException {
        Map<String, Object> result = new HashMap<String, Object>();

        String setId = this.getValue("//project[@name='" + id + "']/parameters/parameters-set/@id");
        if ( setId != null ) {
            Map<String, Object> set = this.getParametersSet( setId );
            for ( String key : set.keySet() ) {
                result.put( key, set.get(key) );
            }
        }

        List<String> keys = this.getValuesList("//project[@name='" + id + "']/parameters/parameter/@name");
        if ( keys == null || keys.isEmpty() ) {
            return result;
        }

        for ( int i = 0; i < keys.size(); i++ ) {
            String value = this.getValue("//project[@name='" + id + "']/parameters/parameter[@name='" + keys.get(i) + "']/@value");

            result.put( keys.get(i), value );
        }

        return result;
    }

    public Map<String, Object> getParametersSet( String id ) throws ConfigReaderException {
        Map<String, Object> result = new HashMap<String, Object>();

        List<String> keys = this.getValuesList("//parameters-set[@id='" + id + "']/parameter/@name");
        if ( keys == null || keys.isEmpty() ) {
            return result;
        }

        for ( int i = 0; i < keys.size(); i++ ) {
            String value = this.getValue("//parameters-set/parameter[@name='" + keys.get(i) + "']/@value");

            result.put( keys.get(i), value );
        }

        return result;
    }

    @Override
    public String getValue( String path ) throws ConfigReaderException {
        return this.sanitizeValue( super.getValue(path) );

    }

    @Override
    public List<String> getValuesList( String path ) throws ConfigReaderException {
        List<String> values = super.getValuesList(path);

        for ( int i = 0; i < values.size(); i++ ) {
            values.set(i, this.sanitizeValue( values.get(i) ) );
        }

        return values;
    }

    protected String sanitizeValue( String value ) {
        if ( !value.startsWith( NOT_ESCAPE_CMD ) ) {
            Matcher matcher = NON_PRINTABLE.matcher(value);
            if ( matcher.find() ) {
                value = matcher.replaceAll("\n\r");
            }
        } else {
            value = value.substring( NOT_ESCAPE_CMD.length() );
        }

        return value;
    }



}
