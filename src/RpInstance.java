import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.ByteArray;
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

    public static String recode(int cutAtPosition, int cutUntilPosition, String input){
        String firstPart = input.substring(0, cutAtPosition);
        String middle = input.substring(cutAtPosition, cutUntilPosition);
        String lastPart = input.substring(cutUntilPosition);

        ByteArray middleByteArray = new ByteArray(middle.getBytes());
        String middle64 = middleByteArray.getBase64Url();

        return firstPart + middle64 + lastPart;
    }
}

