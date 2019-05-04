import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


public class MyCredentialRepository implements CredentialRepository {

     byte[] originByteArray;
     byte[] AliceUserBytes;
    {
        try {
            originByteArray = "This Is alice in wonderland and I hope this text is long enough".getBytes("UTF-8");
            AliceUserBytes = Arrays.copyOfRange(originByteArray, 0, 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    ByteArray aliceAsByteAeeay = new ByteArray(AliceUserBytes);

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String s) {

        Set mySet = new HashSet<PublicKeyCredentialDescriptor>();

        PublicKeyCredentialDescriptor myPk = PublicKeyCredentialDescriptor.builder()
                .id(aliceAsByteAeeay)
                .build();

        mySet.add(myPk);

        return mySet;
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String s) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray byteArray) {
        return Optional.empty();
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray byteArray, ByteArray byteArray1) {
        return Optional.empty();
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray byteArray) {
        return null;
    }
}