package org.smartregister.dhis.tools.model;

import java.util.HashMap;
import java.util.Map;

public class TreeNode {
    private OrganizationUnit organizationUnit;
    private Map<String, TreeNode> children = new HashMap<>();

    public OrganizationUnit getOrganizationUnit() {
        return organizationUnit;
    }

    public void setOrganizationUnit(OrganizationUnit organizationUnit) {
        this.organizationUnit = organizationUnit;
    }

    public Map<String, TreeNode> getChildren() {
        return children;
    }
}
