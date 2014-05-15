package com.scurab.android.anuitor.hierarchy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 14:29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExportView {
}
