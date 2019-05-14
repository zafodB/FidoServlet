package Model;

public class SigninRequestStore {

    String requestId;
    String signinRequestAsJson;

    public SigninRequestStore(){

    }

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
