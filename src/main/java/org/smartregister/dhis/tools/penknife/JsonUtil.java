package org.smartregister.dhis.tools.penknife;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    public static JsonObject getStringAsJson(String jsonString) {
        return new JsonParser().parse(jsonString).getAsJsonObject();
    }

    public static Map<String, JsonObject> getIDMap(JsonArray array) {
        Map<String, JsonObject> hashMap = new HashMap<>();

        int size = array.size();
        int pointer = 0;

        while (pointer < size) {
            JsonObject object = array.get(pointer).getAsJsonObject();
            hashMap.put(getObjectsID(object), object);
            pointer++;
        }

        return hashMap;
    }

    public static Map<String, List<JsonObject>> getParentIDMap(JsonArray array) {
        Map<String, List<JsonObject>> hashMap = new HashMap<>();

        int size = array.size();
        int pointer = 0;

        while (pointer < size) {
            JsonObject object = array.get(pointer).getAsJsonObject();
            String parent = getObjectsParentID(object);
            if (parent == null)
                parent = "root";

            List<JsonObject> node = hashMap.get(parent);

            if (node == null)
                node = new ArrayList<>();

            node.add(object);

            hashMap.put(parent, node);

            pointer++;
        }

        return hashMap;
    }

    public static String getObjectsID(JsonObject object) {
        if (object.has("id"))
            return object.get("id").getAsString();

        return null;
    }

    public static String getObjectsParentID(JsonObject object) {
        if (object.has("parent")) {
            JsonObject parentObject = object.get("parent").getAsJsonObject();
            return getObjectsID(parentObject);
        }
        return null;
    }

    public static JsonArray asJsonObjectsArray(@NotNull JsonObject object, JsonObject... objects) {
        JsonArray array = new JsonArray();
        array.add(object);

        for (JsonObject o : objects)
            array.add(o);

        return array;
    }

    public static JsonArray asJsonObjectsArray(@NotNull List<JsonObject> objects) {
        JsonArray array = new JsonArray();
        for (JsonObject o : objects)
            array.add(o);

        return array;
    }

    public static List<JsonObject> getElementsFromReference(JsonArray array, Map<String, JsonObject> dictionary) {
        List<JsonObject> results = new ArrayList<>();

        int pointer = 0;
        int size = array.size();

        while (pointer < size) {
            JsonObject identifiable = array.get(pointer).getAsJsonObject();
            JsonObject referenced = dictionary.get(getObjectsID(identifiable));

            results.add(referenced);
            pointer++;
        }

        return results;
    }


    public static List<JsonObject> getAllSeniorRelatives(List<JsonObject> units, Map<String, JsonObject> jsonObjectMap) {
        Map<String, JsonObject> uniqueObjects = new HashMap<>();

        List<JsonObject> objects = new ArrayList<>();
        for (JsonObject unit : units) {
            extractParent(unit, jsonObjectMap, objects);
        }

        for (JsonObject object : objects) {
            uniqueObjects.put(JsonUtil.getObjectsID(object), object);
        }

        return new ArrayList<>(uniqueObjects.values());
    }

    public static void extractParent(JsonObject jsonObject, Map<String, JsonObject> jsonObjectMap, List<JsonObject> objects) {
        String parentID = JsonUtil.getObjectsParentID(jsonObject);
        if (parentID != null) {
            JsonObject parent = jsonObjectMap.get(parentID);
            if (parent != null) {
                objects.add(parent);
                extractParent(parent, jsonObjectMap, objects);
            }
        }
    }

}
