/* Made by Filip Adamik on 17/05/2019 */

package Model;

/**
 * Model to temporarily store Sign-in requests
 */
public class SigninRequestStore {

    private String requestId;
    private String signinRequestAsJson;

    public SigninRequestStore() {

    }

    /**
     * @param requestId           Sign-in request ID.
     * @param signinRequestAsJson Sign-in request body in JSON String.
     */
    public SigninRequestStore(String requestId, String signinRequestAsJson) {
        this.requestId = requestId;
        this.signinRequestAsJson = signinRequestAsJson;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public String getSigninRequestAsJson() {
        return signinRequestAsJson;
    }

    public void setSigninRequestAsJson(String signinRequestAsJson) {
        this.signinRequestAsJson = signinRequestAsJson;
    }

}
