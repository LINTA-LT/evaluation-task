package org.tmdn.cti.repository.jpa.domain.common;

public enum Role {
	
    NONE("None"),
    APPLICANT("Applicant"),
    REPRESENTATIVE("Representative"),
    DESIGNER("Designer"),
    CLAIMANT("Claimant"),
    CLAIMANT_REPRESENTATIVE("Claimant_representative"),
    PREVIOUS_APPLICANT("Previous_applicant"),
    REPRESENTATIVE_SPECIAL("special_representative");
    
    private final String description;
    
    Role(String description) {
        this.description = description;
    }
 
    public String getDescription() {
        return description;
    }
    
    public static Role fromDescription(String description) {
        for (Role role: Role.values()) {
            if (role.description.equals(description)) {
                return role;
            }
        }
        throw new IllegalArgumentException(description);
    }
}
