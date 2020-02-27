package org.smartregister.dhis.tools.model;

import java.util.List;

public class Export {

    private System system;
    private List<OrganizationUnit> organisationUnits;

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    public List<OrganizationUnit> getOrganisationUnits() {
        return organisationUnits;
    }

    public void setOrganisationUnits(List<OrganizationUnit> organisationUnits) {
        this.organisationUnits = organisationUnits;
    }
}
