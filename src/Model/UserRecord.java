package Model;

import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;

import java.util.Map;
import java.util.Set;

public class UserRecord {

    private String userHandle;
    private String uniqueName;
    private String displayName;

    private Map<String, RegisteredCredentialStore> credentials;

    public UserRecord() {
    }

    public UserRecord(String userHandle, String uniqueName, String displayName, Map<String, RegisteredCredentialStore> credentials) {

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

