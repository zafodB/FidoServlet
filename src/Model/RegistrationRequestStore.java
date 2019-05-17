/* Made by Filip Adamik on 17/05/2019 */

package Model;

/**
 * Model to store Registration requests.
 */
public class RegistrationRequestStore {

    private String requestId;
    private String requestAsJson;

    public RegistrationRequestStore() {

    }

    /**
     * @param requestId     Registration request ID.
     * @param requestAsJson Registration request body in JSON String.
     */
    public RegistrationRequestStore(String requestId, String requestAsJson) {
        this.requestId = requestId;
        this.requestAsJson = requestAsJson;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public String getRequestAsJson() {
        return requestAsJson;
    }

    public void setRequestAsJson(final String requestAsJson) {
        this.requestAsJson = requestAsJson;
    }

}
