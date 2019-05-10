package Model;

import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;

public class UserRecord {

    public UserRecord(){}

    public UserRecord(String uniqueName, String displayName, ByteArray publicKeyCose, PublicKeyCredentialDescriptor publicKeyDescriptor){
        this.uniqueName = uniqueName;
        this.displayName = displayName;
        this.publicKeyCose = publicKeyCose;
        this.publicKeyDescriptor = publicKeyDescriptor;
    }

    private String uniqueName;

    private String displayName;

    private ByteArray publicKeyCose;

    private PublicKeyCredentialDescriptor publicKeyDescriptor;

    public ByteArray getPublicKeyCose() {
        return publicKeyCose;
    }

    public void setPublicKeyCose(ByteArray publicKeyCose) {
        this.publicKeyCose = publicKeyCose;
    }

    public PublicKeyCredentialDescriptor getPublicKeyDescriptor() {
        return publicKeyDescriptor;
    }

    public void setPublicKeyDescriptor(PublicKeyCredentialDescriptor publicKeyDescriptor) {
        this.publicKeyDescriptor = publicKeyDescriptor;
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

}

