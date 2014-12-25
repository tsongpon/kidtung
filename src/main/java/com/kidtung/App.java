package com.kidtung;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import static spark.Spark.*;

/**
 *
 *
 */
public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) {
        //config static file location
        staticFileLocation("/public");

        get("/kidtung", (request, response) -> {
            log.info("Redering kidtung.html");
            // The hello.html file is located in directory:
            // src/resources/spark/template/freemarker
            return new ModelAndView(null, "kidtung.html");
        }, new FreeMarkerEngine());
    }
}
