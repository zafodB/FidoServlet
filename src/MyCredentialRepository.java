import Model.RegisteredCredentialStore;
import Model.UserRecord;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import database.UserRecordConnector;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


public class MyCredentialRepository implements CredentialRepository {

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String userName) {

        UserRecord userRecord = UserRecordConnector.findByUserName(userName);

        Set<PublicKeyCredentialDescriptor> output = new HashSet<>();

        if (userRecord != null) {

            Set<String> keys = userRecord.getCredentials().keySet();

            if (keys != null) {
                for (String keyId : userRecord.getCredentials().keySet()) {

                    RegisteredCredentialStore savedCredential = userRecord.getCredentials().get(keyId);

                    RegisteredCredential credential = RegisteredCredential.builder()
                            .credentialId(new ByteArray(savedCredential.getCredentialId().getBytes()))
                            .userHandle(new ByteArray(savedCredential.getUserHandle().getBytes()))
                            .publicKeyCose(new ByteArray(savedCredential.getPublicKeyCose().getBytes()))
                            .build();

                    PublicKeyCredentialDescriptor key =
                            PublicKeyCredentialDescriptor.builder().id(credential.getCredentialId()).build();

                    output.add(key);
                }
            }
        }
        return output;
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        UserRecord record = UserRecordConnector.findByUserName(username);
        if (record != null) return Optional.of(new ByteArray(record.getUserHandle().getBytes()));
        else return Optional.empty();
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        UserRecord record = UserRecordConnector.findByUserHandle(userHandle);
        if (record != null) return Optional.of(record.getUniqueName());
        else return Optional.empty();
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        return Optional.of(UserRecordConnector.findCredential(credentialId, userHandle));
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray byteArray) {
//        TODO actually return list of credentials
        Set<RegisteredCredential> out = new HashSet<>();

        return out;
    }
}