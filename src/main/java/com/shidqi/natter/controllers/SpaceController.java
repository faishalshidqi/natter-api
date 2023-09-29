package com.shidqi.natter.controllers;
import org.dalesbred.Database;
import org.json.*;
import spark.*;

import java.sql.SQLException;

public class SpaceController {
    private final Database database;
    public SpaceController(Database database) {
        this.database = database;
    }

    public JSONObject createSpace(Request request, Response response) throws SQLException {
        var json = new JSONObject(request.body());
        var spaceName = json.getString("name");
        var owner = json.getString("owner");

        return database.withTransaction(transact -> {
            var spaceId = database.findUniqueLong("select next value for space_id_seq;");
            database.updateUnique("insert into spaces(space_id, name, owner) " + "values(" + spaceId + ", '"+ spaceName + "', '"+ owner+ "');");
            response.status(201);
            response.header("Location", "/spaces/" + spaceId);

            return new JSONObject()
                    .put("name", spaceName)
                    .put("uri", "/spaces/" + spaceId);
        });
    }
}
