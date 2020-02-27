package org.smartregister.dhis.tools.tools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.smartregister.dhis.tools.model.Tree;
import org.smartregister.dhis.tools.penknife.IOUtils;
import org.smartregister.dhis.tools.penknife.JsonUtil;
import org.smartregister.dhis.tools.penknife.TreeUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileSplitter {


    public void splitDHIS2File(String rootLocation, String sourceFile, String destinationFilePrefix, int chunks) {
        try {

            String content = IOUtils.getFileContent(rootLocation + sourceFile);
            JsonObject contentObject = JsonUtil.getStringAsJson(content);

            // get all the nodes and convert them into a tree
            Map<String, Map<String, JsonObject>> rootIDNode = getIdJsonArrayMap(contentObject);
            Map<String, Map<String, List<JsonObject>>> rootParentNode = getParentIdJsonArrayMap(contentObject);


            // remove all un needed info
            removeDHIS2UselessInfo(contentObject, "organisationUnitGroupSets");
            removeDHIS2UselessInfo(contentObject, "organisationUnitGroups");
            removeDHIS2UselessInfo(contentObject, "organisationUnits");
            removeDHIS2UselessInfo(contentObject, "organisationUnitLevels");

            // extract individual sets into files
            Map<String, JsonObject> setMap = rootIDNode.get("organisationUnitGroupSets");
            JsonObject system = contentObject.getAsJsonObject("system");
            JsonArray organisationUnitLevels = contentObject.getAsJsonArray("organisationUnitLevels");

            for (Map.Entry<String, JsonObject> entry : setMap.entrySet()) {

                JsonObject exportObject = new JsonObject();
                exportObject.add("system", system);
                exportObject.add("organisationUnitLevels", organisationUnitLevels);


                JsonObject groupSet = entry.getValue();

                // add group set
                exportObject.add("organisationUnitGroupSets", JsonUtil.asJsonObjectsArray(groupSet));

                // get set groups
                List<JsonObject> setGroupsReference = JsonUtil.getElementsFromReference(groupSet.getAsJsonArray("organisationUnitGroups"), rootIDNode.get("organisationUnitGroups"));
                exportObject.add("organisationUnitGroups", JsonUtil.asJsonObjectsArray(setGroupsReference));

                // get all the units
                // organisationUnits
                List<JsonObject> directChildrenUnits = new ArrayList<>();
                for (JsonObject jo : setGroupsReference) {
                    directChildrenUnits.addAll(JsonUtil.getElementsFromReference(jo.getAsJsonArray("organisationUnits"), rootIDNode.get("organisationUnits")));
                }

                List<JsonObject> allChildren = JsonUtil.getAllSeniorRelatives(directChildrenUnits, rootIDNode.get("organisationUnits"));
                JsonObject root = rootParentNode.get("organisationUnits").get("root").get(0);
                Tree<JsonObject> organizationUnits = TreeUtil.plantTree(allChildren, rootParentNode.get("organisationUnits"), root, rootIDNode.get("organisationUnits"));
                JsonArray unitsArray = TreeUtil.getOrderedTreeArray(organizationUnits);


                // get set groups
                exportObject.add("organisationUnits", unitsArray);

                // save to the disk
                IOUtils.saveToDisk(rootLocation, entry.getKey() + ".json", exportObject.toString().getBytes());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeDHIS2UselessInfo(JsonObject contentObject, String memberName) {

        JsonArray array = contentObject.getAsJsonArray(memberName);

        List<JsonObject> obs = new ArrayList<>();

        int pos = 0;
        int size = array.size();

        while (pos < size) {
            JsonObject jsonObject = array.get(pos).getAsJsonObject();

            if (jsonObject.has("userGroupAccesses")) {
                jsonObject.remove("userGroupAccesses");
                jsonObject.add("userGroupAccesses", new JsonArray());
            }

            if (jsonObject.has("userAccesses")) {
                jsonObject.remove("userAccesses");
                jsonObject.add("userAccesses", new JsonArray());
            }

            obs.add(jsonObject);
            pos++;
        }

        JsonArray jsonArray = new JsonArray();
        for (JsonObject jo : obs) {
            jsonArray.add(jo);
        }

        contentObject.remove(memberName);
        contentObject.add(memberName, jsonArray);
    }

    /**
     * returns a nested map fo an object refrerence by its ID
     *
     * @param contentObject
     * @return
     */
    private Map<String, Map<String, JsonObject>> getIdJsonArrayMap(JsonObject contentObject) {
        Map<String, Map<String, JsonObject>> rootNode = new HashMap<>();

        for (Map.Entry<String, JsonElement> entrySet : contentObject.entrySet()) {
            JsonElement element = entrySet.getValue();
            if (element.isJsonArray()) {
                rootNode.put(entrySet.getKey(), JsonUtil.getIDMap(element.getAsJsonArray()));
            }
        }
        return rootNode;
    }

    /**
     * returns a nested list of all the data grouped by parent
     *
     * @param contentObject
     * @return
     */
    private Map<String, Map<String, List<JsonObject>>> getParentIdJsonArrayMap(JsonObject contentObject) {
        Map<String, Map<String, List<JsonObject>>> rootNode = new HashMap<>();

        for (Map.Entry<String, JsonElement> entrySet : contentObject.entrySet()) {
            JsonElement element = entrySet.getValue();
            if (element.isJsonArray()) {
                rootNode.put(entrySet.getKey(), JsonUtil.getParentIDMap(element.getAsJsonArray()));
            }
        }

        return rootNode;
    }
}
