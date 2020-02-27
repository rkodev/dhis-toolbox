package org.smartregister.dhis.tools.penknife;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.smartregister.dhis.tools.model.Tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeUtil {

    /**
     * Receives a map of all the parents and a list of objects to create a tree
     *
     * @return
     */
    public static Tree<JsonObject> plantTree(List<JsonObject> objects, Map<String, List<JsonObject>> parentIDMap, JsonObject root, Map<String, JsonObject> dictionary) {
        // prepare
        List<String> objectIDs = jsonObjectToIDs(objects);
        Map<String, List<String>> parentIDs = new HashMap<>();
        for (Map.Entry<String, List<JsonObject>> entry : parentIDMap.entrySet()){
            parentIDs.put(entry.getKey(), jsonObjectToIDs(entry.getValue()));
        }

        // get all immediate children to the root
        Tree<JsonObject> tree = new Tree<>();

        Tree.Node<JsonObject> node = new Tree.Node<>();
        node.setData(root);
        growTree(objectIDs, parentIDs, root, dictionary, node);


        tree.setRoot(node);

        return tree;
    }

    private static void growTree(List<String> objectIDs, Map<String, List<String>> parentIDs, JsonObject root , Map<String, JsonObject> dictionary, Tree.Node<JsonObject> rootNode){
        List<String> children = parentIDs.get(JsonUtil.getObjectsID(root));
        List<Tree.Node<JsonObject>> nodes = new ArrayList<>();

        for (String c: children) {
            if(objectIDs.contains(c)){
                Tree.Node<JsonObject> node = new Tree.Node<>();

                JsonObject child = dictionary.get(c);
                node.setData(child);
                growTree(objectIDs, parentIDs, child, dictionary, node);

                nodes.add(node);
            }
        }
        rootNode.setChildren(nodes);
    }

    private static List<String> jsonObjectToIDs(List<JsonObject> objects){
        List<String> res = new ArrayList<>();

        for (JsonObject jo: objects){
            String id = JsonUtil.getObjectsID(jo);
            res.add(id);
        }
        return res;
    }

    public static JsonArray getOrderedTreeArray(Tree<JsonObject> tree){
        JsonArray array = new JsonArray();

        // traverse tree node by node
        List<JsonObject> holder = new ArrayList<>();
        extract(tree.getRoot(), holder);

        for (JsonObject jo: holder){
            array.add(jo);
        }

        return array;
    }

    private static void extract(Tree.Node<JsonObject> node, List<JsonObject> parent){
        if(node != null) {
            parent.add(node.getData());

            if (node.getChildren() != null) {

                for (Tree.Node<JsonObject> baby: node.getChildren())
                    extract(baby, parent);
            }
        }
    }
}
