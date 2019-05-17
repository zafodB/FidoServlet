/* Made by Filip Adamik on 17/05/2019 */

package Model;

/**
 * Model to store user's credentials and associated details
 */
public class RegisteredCredentialStore {

    private String credentialId;
    private String publicKeyCose;
    private String userHandle;
    private Integer signatureCount;

    public RegisteredCredentialStore() {
    }

    /**
     * @param credentialId   ID number of the credential registered with the user account.
     * @param publicKeyCose  COSE representation of the Public key value of the registered physical key.
     * @param userHandle     Unique identifier of the user. Respective to
     *                       <a href="https://www.w3.org/TR/webauthn/#user-handle">user handle defined by Webauthn</a>.
     * @param signatureCount used to prevent replay attacks. Not implemented in this version.
     */
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
