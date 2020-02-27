package org.smartregister.dhis.tools.tools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.smartregister.dhis.tools.model.Pair;
import org.smartregister.dhis.tools.model.Tree;
import org.smartregister.dhis.tools.model.Triple;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonOrganizer {


    private static void extractByLevelsDataLevels(String content, String rootLocation) {
        JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
        JsonObject jsonSystem = jsonObject.getAsJsonObject("system");
        String[] vales = {"organisationUnitGroupSets", "organisationUnitGroups", "organisationUnits", "organisationUnitLevels"};

        Map<String,JsonObject> organisationUnitGroupsMap = convertIdentifiableObjectsToMap(jsonObject.getAsJsonArray("organisationUnitGroups"));
        Map<String,JsonObject> organisationUnitsMap = convertIdentifiableObjectsToMap(jsonObject.getAsJsonArray("organisationUnits"));
        Map<String, List<JsonObject>> organizationParents = convertIdentifiableObjectsToParentMap(jsonObject.getAsJsonArray("organisationUnits"));

        JsonArray parent = jsonObject.getAsJsonArray("organisationUnitGroupSets");

        int x = 0;
        int size = parent.size();

        while (x < size){
            JsonObject object = parent.get(x).getAsJsonObject();
            Pair<JsonObject,List<JsonObject>> pair = getChildren(object, "organisationUnitGroups", organisationUnitGroupsMap);



            // create and export object
            JsonObject jsonExport = new JsonObject();
            jsonExport.add("system", jsonSystem);

            JsonArray set = new JsonArray();
            set.add(object);
            jsonExport.add("organisationUnitGroupSets", set);


            JsonArray organisationUnitGroups = new JsonArray();
            List<JsonObject> childrenLocations = new ArrayList<>();

            for (JsonObject jo: pair.second){
                organisationUnitGroups.add(jo);
                Pair<JsonObject,List<JsonObject>> unitGroups = getChildren(jo, "organisationUnits", organisationUnitsMap);
                childrenLocations.addAll(unitGroups.second);
            }
            jsonExport.add("organisationUnitGroups", organisationUnitGroups);

            // order all the kids then import

            jsonExport.add("organisationUnits", organisationUnitGroups);


            try {
                Files.write(Paths.get(rootLocation + "organisationUnitGroups" + x + ".json"), jsonExport.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            x++;
        }

    }

    private static void extractByLevelsData(String content) {
        JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
        JsonObject jsonExport = jsonObject.getAsJsonObject("system");
        String[] vales = {"organisationUnitGroupSets", "organisationUnitGroups", "organisationUnits", "organisationUnitLevels"};



        Map<String,JsonObject> organisationUnitGroupsMap = convertIdentifiableObjectsToMap(jsonObject.getAsJsonArray("organisationUnitGroups"));
        Map<String,JsonObject> organisationUnitsMap = convertIdentifiableObjectsToMap(jsonObject.getAsJsonArray("organisationUnits"));
        Map<String,List<JsonObject>> organizationParents = convertIdentifiableObjectsToParentMap(jsonObject.getAsJsonArray("organisationUnits"));

        JsonArray parent = jsonObject.getAsJsonArray("organisationUnitGroupSets");
        List<Pair<JsonObject,List<JsonObject>>> organisationUnitGroupSetsPair = new ArrayList<>();

        int x = 0;
        int size = parent.size();

        while (x < size){
            JsonObject object = parent.get(x).getAsJsonObject();
            organisationUnitGroupSetsPair.add(getChildren(object, "organisationUnitGroups", organisationUnitGroupsMap));
            x++;
        }

        List<Pair<JsonObject,List<Pair<JsonObject,List<JsonObject>>>>> organisationUnitGroupPair = new ArrayList<>();

        for(Pair<JsonObject,List<JsonObject>> unitSet: organisationUnitGroupSetsPair){

            List<Pair<JsonObject,List<JsonObject>>> organisationUnitPair = new ArrayList<>();
            for(JsonObject group: unitSet.second){
                Pair<JsonObject,List<JsonObject>> units = getChildren(group, "organisationUnits", organisationUnitsMap);

                Map<String, JsonObject> unitsWithParents = extractParents(units.second, organisationUnitsMap);
                Tree<JsonObject> ordered = orderMapOfUnits(unitsWithParents);

                organisationUnitPair.add(units);
            }

            Pair<JsonObject,List<Pair<JsonObject,List<JsonObject>>>> result = Pair.of(unitSet.first, organisationUnitPair);
            organisationUnitGroupPair.add(result);
        }

        int threshold = 3;


        for (String pos : vales) {
            //extractLevels(jsonObject, jsonExport, pos);
        }
    }

    private static Map<String, JsonObject> extractParents(List<JsonObject> objects, Map<String,JsonObject> parentsMap){
        Map<String,JsonObject> parentMap = new HashMap<>();
        for (JsonObject child: objects){
            parentMap.putAll(extractParents(child, parentsMap));
        }
        return parentMap;
    }

    private static Map<String, JsonObject> extractParents(JsonObject child, Map<String,JsonObject> parentsMap){
        Map<String,JsonObject> parentMap = new HashMap<>();
        if(child.has("parent")){
            JsonElement parentNode = child.get("parent");
            if(parentNode.isJsonObject()){
                JsonObject parentNodeObject = parentNode.getAsJsonObject();

                if(parentNodeObject.has("id")){
                    String id = parentNodeObject.get("id").getAsString();
                    JsonObject parentObject = parentsMap.get(id);

                    if(parentObject != null) {
                        parentMap.put(id, parentObject);

                        parentMap.putAll(extractParents(parentObject, parentsMap));
                    }
                }

            }
        }
        return parentMap;
    }

    private static Tree<JsonObject> orderMapOfUnits(Map<String, JsonObject> jsonObjectMap){
        Tree<JsonObject> tree = new Tree<>();
        // get the root node
        for (Map.Entry<String,JsonObject> entry: jsonObjectMap.entrySet()){
            if(!entry.getValue().has("parent")){
                // the node has been found, exit loop
                Tree.Node<JsonObject> node = new Tree.Node<>();
                node.setData(entry.getValue());
                tree.setRoot(node);
                break;
            }
        }
        return tree;
    }

    private static void traverseAndPopulate(Tree.Node<JsonObject> node){
        // get all the children
    }

    private static Pair<JsonObject,List<JsonObject>> getChildren(JsonObject parent, String nodeName, Map<String,JsonObject> childrenMap){

        List<JsonObject> children = new ArrayList<>();

        if(parent.has(nodeName)){
            JsonElement element = parent.get(nodeName);
            if(element.isJsonArray()){
                JsonArray jsonArray = element.getAsJsonArray();

                int position = 0;
                int size = jsonArray.size();
                while (position < size){
                    JsonObject childNode = jsonArray.get(position).getAsJsonObject();

                    if(childNode.has("id")){
                        JsonObject object = childrenMap.get(childNode.get("id").getAsString());
                        children.add(object);
                    }

                    position++;
                }
            }
        }

        return Pair.of(parent, children);
    }

    /**
     * Returns a triple object with the list of partitioned arrays
     * Second object is the list of organization unit groups
     * 3rd object is list of organization unit sets
     * @return
     */
    private static Triple<List<JsonArray>, List<JsonObject>, List<JsonObject>> splitObject(){

        return null;
    }

    /**
     * breaks the code to unit groups
     * @param jsonArray
     * @param partitionCount
     * @return
     */
    private static List<JsonArray> partition(JsonArray jsonArray, int partitionCount){

        int partitionsSize = jsonArray.size() / partitionCount;
        List<JsonArray> partitions = new ArrayList<>();

        int arraySize = jsonArray.size();
        int pointer = 0;

        JsonArray array = new JsonArray();
        while (pointer < arraySize){

            // reload the holder
            if(array.size() == partitionsSize){
                partitions.add(array);
                array = new JsonArray();
            }

            array.add(jsonArray.get(pointer));
            pointer++;
        }

        return partitions;
    }

    private static Map<String,JsonObject> convertIdentifiableObjectsToMap(JsonArray array){
        Map<String,JsonObject> objectMap = new HashMap<>();
        int size = array.size();
        int x = 0;

        while (x < size){
            JsonObject jsonObject = array.get(x).getAsJsonObject();
            objectMap.put(jsonObject.get("id").getAsString(), jsonObject);
            x++;
        }

        return objectMap;
    }

    private static Map<String,List<JsonObject>> convertIdentifiableObjectsToParentMap(JsonArray array){
        Map<String,List<JsonObject>> objectMap = new HashMap<>();
        int size = array.size();
        int x = 0;

        while (x < size){
            JsonObject jsonObject = array.get(x).getAsJsonObject();
            String id = "root";
            if(jsonObject.has("parent")){
                id = jsonObject.get("parent").getAsJsonObject().get("id").getAsString();
            }

            List<JsonObject> kids = objectMap.get(id);
            if(kids ==null)
                kids = new ArrayList<>();

            kids.add(jsonObject);
            objectMap.put(id, kids);
            x++;
        }

        return objectMap;
    }
}
