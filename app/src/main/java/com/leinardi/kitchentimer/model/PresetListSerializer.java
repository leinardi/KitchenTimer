/*
 * Kitchen Timer
 * Copyright (C) 2015 Roberto Leinardi
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.leinardi.kitchentimer.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leinardi on 23/04/15.
 */
public class PresetListSerializer implements JsonSerializer<List<Preset>>, JsonDeserializer<List<Preset>> {

    @Override
    public JsonElement serialize(List<Preset> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray result = new JsonArray();

        for (Preset preset : src) {
            JsonObject object = new JsonObject();
            object.addProperty(Preset.ID_FIELD_NAME, preset.getId());
            object.addProperty(Preset.LABEL_FIELD_NAME, preset.getLabel());
            object.addProperty(Preset.DURATION_FIELD_NAME, preset.getDuration());
            result.add(object);
        }

        return result;
    }

    @Override
    public List<Preset> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonArray items = json.getAsJsonArray();
        List<Preset> list = new ArrayList<>();

        try {
            for (JsonElement jsonElement : items) {
                String id = jsonElement.getAsJsonObject().get(Preset.ID_FIELD_NAME).getAsString();
                String label = jsonElement.getAsJsonObject().get(Preset.LABEL_FIELD_NAME).getAsString();
                long duration = jsonElement.getAsJsonObject().get(Preset.DURATION_FIELD_NAME).getAsLong();
                list.add(new Preset(id, label, duration));
            }
        } catch (Exception e) {
            throw new JsonParseException(e);
        }

        return list;
    }
}
