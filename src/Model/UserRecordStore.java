/* Made by Filip Adamik on 17/05/2019 */

package Model;

import java.util.Map;

/**
 * Model to store user records.
 */
public class UserRecordStore {

    private String userHandle;
    private String uniqueName;
    private String displayName;

    private Map<String, RegisteredCredentialStore> credentials;

    public UserRecordStore() {
    }

    /**
     * @param userHandle  A unique opaque user identifier.
     * @param uniqueName  A unique name that may be displayed to the user.
     * @param displayName A user-friendly name to be displayed to the user.
     * @param credentials List of credentials registered with the user account. Implemented as a Map (Credential ID -> Credential details).
     */
    public UserRecordStore(String userHandle, String uniqueName, String displayName, Map<String, RegisteredCredentialStore> credentials) {

        this.userHandle = userHandle;
        this.uniqueName = uniqueName;
        this.displayName = displayName;
        this.credentials = credentials;
    }

    public String getUserHandle() {
        return userHandle;
    }

    public void setUserHandle(String userHandle) {
        this.userHandle = userHandle;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<String, RegisteredCredentialStore> getCredentials() {
        return credentials;
    }

    public void setCredentials(Map<String, RegisteredCredentialStore> credentials) {
        this.credentials = credentials;
    }
}

