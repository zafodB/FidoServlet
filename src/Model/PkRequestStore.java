package Model;

public class PkRequestStore {

    String requestId;
    String pkRequestAsJson;

    public PkRequestStore(){

    }

    public PkRequestStore(String requestId, String pkRequestAsJson) {
        this.requestId = requestId;
        this.pkRequestAsJson = pkRequestAsJson;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public String getPkRequestAsJson() {
        return pkRequestAsJson;
    }

    public void setPkRequestAsJson(final String pkRequestAsJson) {
        this.pkRequestAsJson = pkRequestAsJson;
    }

}
