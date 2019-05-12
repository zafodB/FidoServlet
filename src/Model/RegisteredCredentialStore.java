package Model;

public class RegisteredCredentialStore {

    String credentialId;
    String publicKeyCose;
    String userHandle;
    Integer signatureCount;

    public RegisteredCredentialStore() {
    }

    public RegisteredCredentialStore(String credentialId, String publicKeyCose, String userHandle, Integer signatureCount) {
        this.credentialId = credentialId;
        this.publicKeyCose = publicKeyCose;
        this.userHandle = userHandle;
        this.signatureCount = signatureCount;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getPublicKeyCose() {
        return publicKeyCose;
    }

    public void setPublicKeyCose(String publicKeyCose) {
        this.publicKeyCose = publicKeyCose;
    }

    public String getUserHandle() {
        return userHandle;
    }

    public void setUserHandle(String userHandle) {
        this.userHandle = userHandle;
    }

    public Integer getSignatureCount() {
        return signatureCount;
    }

    public void setSignatureCount(Integer signatureCount) {
        this.signatureCount = signatureCount;
    }
}
