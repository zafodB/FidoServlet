package Model;

public class PkRequestStore {

    String id;
    String pkRequestAsJson;

    public PkRequestStore(){

    }

    public PkRequestStore(String id, String pkRequestAsJson) {
        this.id = id;
        this.pkRequestAsJson = pkRequestAsJson;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getPkRequestAsJson() {
        return pkRequestAsJson;
    }

    public void setPkRequestAsJson(final String pkRequestAsJson) {
        this.pkRequestAsJson = pkRequestAsJson;
    }
}
