package org.smartregister.dhis.tools.model;

import java.util.List;

public class OrganizationUnit {
    private String created;
    private String lastUpdated;
    private String name;
    private String id;
    private String shortName;
    private String path;
    private String featureType;
    private String openingDate;
    private ReferencedObject parent;
    private ReferencedObject user;
    private List<OrganizationUnit> attributeValues;
    private List<OrganizationUnit> translations;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFeatureType() {
        return featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(String openingDate) {
        this.openingDate = openingDate;
    }

    public ReferencedObject getParent() {
        return parent;
    }

    public void setParent(ReferencedObject parent) {
        this.parent = parent;
    }

    public ReferencedObject getUser() {
        return user;
    }

    public void setUser(ReferencedObject user) {
        this.user = user;
    }

    public List<OrganizationUnit> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(List<OrganizationUnit> attributeValues) {
        this.attributeValues = attributeValues;
    }

    public List<OrganizationUnit> getTranslations() {
        return translations;
    }

    public void setTranslations(List<OrganizationUnit> translations) {
        this.translations = translations;
    }
}
