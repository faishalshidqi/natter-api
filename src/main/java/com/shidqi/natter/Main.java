package com.shidqi.natter;
import com.shidqi.natter.controllers.*;
import java.nio.file.*;
import java.util.Objects;

import org.dalesbred.Database;
import org.h2.jdbcx.JdbcConnectionPool;
import org.json.*;
import static spark.Spark.*;

public class Main {
    public static void main(String... args) throws Exception {
        var datasource = JdbcConnectionPool.create("jdbc:h2:mem:natter", "developer", "supersecretpassword");
        var database = Database.forDataSource(datasource);
        createTables(database);

        var spaceController = new SpaceController(database);
        post("/spaces", spaceController::createSpace);
        after((request, response) -> {
            response.type("application/json");
        });
        internalServerError(new JSONObject().put("error", "Internal Server Error").toString());
        notFound(new JSONObject().put("error", "Not Found").toString());
    }

    private static void createTables(Database database) throws Exception {
        var path = Paths.get(Objects.requireNonNull(Main.class.getResource("/schema.sql")).toURI());
        database.update(Files.readString(path));
    }
}
