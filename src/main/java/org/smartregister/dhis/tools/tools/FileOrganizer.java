package org.smartregister.dhis.tools.tools;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.smartregister.dhis.tools.model.Export;
import org.smartregister.dhis.tools.model.OrganizationUnit;
import org.smartregister.dhis.tools.model.TreeNode;
import org.smartregister.dhis.tools.penknife.IOUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileOrganizer {

    private static void orderContentByLevel(String content, String rootLocation , String destinationFilePrefix , String sourceFile) {
        Export export = new Gson().fromJson(content, Export.class);

        Map<String, List<OrganizationUnit>> organizationUnitMap = new HashMap<>();

        for (OrganizationUnit unit : export.getOrganisationUnits()) {
            String parentID = unit.getParent() != null ? unit.getParent().getId() : "root";
            List<OrganizationUnit> children = organizationUnitMap.get(parentID);
            if (children == null) children = new ArrayList<>();

            children.add(unit);

            organizationUnitMap.put(parentID, children);
        }

        OrganizationUnit root = organizationUnitMap.get("root").get(0);
        TreeNode treeNode = new TreeNode();
        loadTree(organizationUnitMap, treeNode, root);

        //directExport(treeNode, export);

        // split trials
        List<TreeNode> treeNodes = splitToFiles(treeNode, 5);

        int pos = 0;
        for (TreeNode node : treeNodes) {
            String exportName = rootLocation + destinationFilePrefix + pos + sourceFile;
            saveToDisk(node, export, exportName);
            pos++;
        }
    }
    private static List<TreeNode> splitToFiles(TreeNode treeNode, int size) {

        List<TreeNode> nodes = new ArrayList<>();
        int create_nodes = treeNode.getChildren().size() / size;
        while (create_nodes > -1) {
            TreeNode node = new TreeNode();
            node.setOrganizationUnit(treeNode.getOrganizationUnit());
            nodes.add(node);
            create_nodes--;
        }

        int x = 0;
        for (Map.Entry<String, TreeNode> entry : treeNode.getChildren().entrySet()) {
            int position = x / size;

            nodes.get(position).getChildren().put(entry.getKey(), entry.getValue());
            x++;
        }

        return nodes;
    }

    private static void saveToDisk(TreeNode treeNode, Export export, String exportName) {
        // start ordering the content
        List<OrganizationUnit> results = getOrderedList(treeNode);

        Export newExport = new Export();
        newExport.setSystem(export.getSystem());
        newExport.setOrganisationUnits(results);

        String jsonInString = new Gson().toJson(newExport);
        try {
            Files.write(Paths.get(exportName), jsonInString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(export.getSystem().getDate());
    }

    private static List<OrganizationUnit> getOrderedList(TreeNode treeNode) {
        List<OrganizationUnit> result = new ArrayList<>();
        result.add(treeNode.getOrganizationUnit());
        extractLevels(result, treeNode);
        return result;
    }

    private static void extractLevels(List<OrganizationUnit> organizationUnits, TreeNode treeNode) {
        if (treeNode.getChildren() != null && treeNode.getChildren().size() > 0) {
            List<TreeNode> treeNodes = new ArrayList<>();

            // add all the parents
            for (Map.Entry<String, TreeNode> o : treeNode.getChildren().entrySet()) {
                organizationUnits.add(o.getValue().getOrganizationUnit());
                treeNodes.add(o.getValue());
            }

            // loop and add all the children
            for (TreeNode node : treeNodes) {
                extractLevels(organizationUnits, node);
            }
        }
    }

    private static void loadTree(Map<String, List<OrganizationUnit>> organizationUnitMap, TreeNode treeNode, OrganizationUnit root) {
        treeNode.setOrganizationUnit(root);
        List<OrganizationUnit> children = organizationUnitMap.get(root.getId());
        for (OrganizationUnit unit : children) {
            TreeNode childNode = new TreeNode();
            childNode.setOrganizationUnit(unit);

            treeNode.getChildren().put(unit.getId(), childNode);

            List<OrganizationUnit> childUnits = organizationUnitMap.get(childNode.getOrganizationUnit().getId());

            if (childUnits != null && childUnits.size() > 0) {
                loadTree(organizationUnitMap, childNode, unit);
            }
        }
    }

    private List<JsonObject> getOrderedList(List<JsonObject> childrenLocations, Map<String,List<JsonObject>> organizationParents, Map<String,JsonObject> organisationUnitsMap){

        Map<String,JsonObject> uniqueLocations = new HashMap<>();

        for (JsonObject ob: childrenLocations){
            uniqueLocations.put(ob.get("id").getAsString(), ob);

            // read dll kids
        }

        return new ArrayList();
    }
}
