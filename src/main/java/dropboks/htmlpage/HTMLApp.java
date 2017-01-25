package dropboks.htmlpage;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import dropboks.App;
import dropboks.exceptions.AlreadyExistsException;
import dropboks.exceptions.NoRecordForundInDatabaseException;
import dropboks.exceptions.PermissionException;
import org.jooq.exception.DataAccessException;
import spark.ModelAndView;
import spark.Redirect;
import spark.template.mustache.MustacheTemplateEngine;

import javax.naming.AuthenticationException;

import static spark.Spark.*;
import static spark.Spark.get;

/**
 * Mustache template engine for Dropboks project
 */
public class HTMLApp {

    public static void main(String[] args) {

        Map map= new HashMap();

        staticFileLocation("/templates");

        Map<Class, Integer> exceptionMap = initializeExceptionMap();
        Map<Class, String> redirectionMap = initializeRedirectMap();


        // start old json-based services
        App jsonServices = new App();
        jsonServices.initialize(4567, true);

        // index.mustache file is in resources/templates directory
        get("/hello", (rq, rs) -> new ModelAndView(map, "home.mustache"), new MustacheTemplateEngine());
        get("/about", (rq, rs) -> new ModelAndView(map, "about.mustache"), new MustacheTemplateEngine());
        get("/files", (rq, rs) -> new ModelAndView(map, "files.mustache"), new MustacheTemplateEngine());
        get("/login", (rq, rs) -> new ModelAndView(map, "login.mustache"), new MustacheTemplateEngine());

        // redirect to documentation
        get("/documentation", (request, response) -> {
            response.redirect("/docs/index.html");
            return 302;
        });

        // always redirect to main page
        redirect.any("/", "/hello", Redirect.Status.MOVED_PERMANENTLY);


        exception(Exception.class, (ex, request, response) -> {
            ex.printStackTrace();
            response.status(exceptionMap.get(ex.getClass()));
            response.redirect(redirectionMap.get(ex.getClass()));

        });
    }

    public static Map<Class, Integer> initializeExceptionMap(){
        Map<Class, Integer> exceptionMap = new HashMap<>();
        exceptionMap.put(InvalidParameterException.class, INVALID_PARAMETER);
        exceptionMap.put(AuthenticationException.class, UNSUCCESSFUL_LOGIN);
        exceptionMap.put(NoRecordForundInDatabaseException.class, INVALID_PARAMETER);
        exceptionMap.put(DataAccessException.class, NOT_FOUND);
        exceptionMap.put(AlreadyExistsException.class, ALREADY_EXISTS);
        exceptionMap.put(PermissionException.class, AUTHENTICATION_REQUIRED);
        exceptionMap.put(null, NOT_FOUND);

        return exceptionMap;
    }

    public static Map<Class, String> initializeRedirectMap(){
        Map<Class, String> map = new HashMap<>();
        map.put(PermissionException.class, "/login");
        map.put(AuthenticationException.class, "/login");

        return map;
    }


    private static final int ALREADY_EXISTS = 400;
    private static final int AUTHENTICATION_REQUIRED = 401;
    private static final int UNSUCCESSFUL_LOGIN = 403;
    private static final int INVALID_PARAMETER = 405;
    private static final int NOT_FOUND = 404;


}
