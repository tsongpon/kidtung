package com.kidtung;

import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import static spark.Spark.*;

/**
 *
 *
 */
public class App 
{
    public static void main( String[] args ) {
        //config static file location
        staticFileLocation("/public");

        get("/kidtung", (request, response) -> {
            // The hello.html file is located in directory:
            // src/resources/spark/template/freemarker
            return new ModelAndView(null, "kidtung.html");
        }, new FreeMarkerEngine());
    }
}
