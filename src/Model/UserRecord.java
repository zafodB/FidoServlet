package Model;

public final class UserRecord {

    public UserRecord(final String uniqueName, final String displayName, final String greeting){
        this.uniqueName = uniqueName;
        this.displayName = displayName;
        this.greeting = greeting;
    }

    private String uniqueName;

    private String displayName;

    private String greeting;

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(final String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(final String greeting) {
        this.greeting = greeting;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserRecord person = (UserRecord) o;


        if (getUniqueName() != null ? !getUniqueName().equals(person.getUniqueName()) : person.getUniqueName() != null) {
            return false;
        }
        if (getDisplayName() != null ? !getDisplayName().equals(person.getDisplayName()) : person.getDisplayName() != null) {
            return false;
        }
        if (getGreeting() != null ? !getGreeting().equals(person.getGreeting()) : person.getGreeting() != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = getUniqueName() != null ? getUniqueName().hashCode() : 0;
        result = 31 * result + (getDisplayName() != null ? getDisplayName().hashCode() : 0);
        result = 31 * result + (getGreeting() != null ? getGreeting().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Person{"
                + ", uniqueName='" + uniqueName + "'"
                + ", displayName=" + displayName
                + ", greeting=" + greeting
                + "}";
    }
}

