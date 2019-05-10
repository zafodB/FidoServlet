import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;

public class RpInstance {

    static RelyingParty rp;

    static {
        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
                .id("fidoserver.ml")
                .name("Fido App")
                .build();

        rp = RelyingParty.builder()
                .identity(rpIdentity)
                .credentialRepository(new MyCredentialRepository())
                .build();
    }

}
