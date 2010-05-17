package com.vio.documentor.engines.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by IntelliJ IDEA.
 * User: nikelin
 * Date: May 14, 2010
 * Time: 3:08:36 PM
 * To change this template use File | Settings | File Templates.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Engine {

    public String id();

}
