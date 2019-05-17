/* Made by Filip Adamik on 17/05/2019 */

import Model.RegisteredCredentialStore;
import Model.UserRecordStore;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import database.DatabaseException;
import database.UserRecordConnector;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

class CredentialRepositoryConnector implements CredentialRepository {

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String userName) {

        try {
            UserRecordStore userRecordStore = UserRecordConnector.findByUserName(userName);

            Set<PublicKeyCredentialDescriptor> output = new HashSet<>();

            if (userRecordStore != null) {

                Set<String> keys = userRecordStore.getCredentials().keySet();

                if (!keys.isEmpty()) {
                    for (String keyId : userRecordStore.getCredentials().keySet()) {

                        RegisteredCredentialStore savedCredential = userRecordStore.getCredentials().get(keyId);

                        RegisteredCredential credential = RegisteredCredential.builder()
                                .credentialId(ByteArray.fromBase64(savedCredential.getCredentialId()))
                                .userHandle(ByteArray.fromBase64(savedCredential.getUserHandle()))
                                .publicKeyCose(ByteArray.fromBase64(savedCredential.getPublicKeyCose()))
                                .build();

                        PublicKeyCredentialDescriptor key =
                                PublicKeyCredentialDescriptor.builder().id(credential.getCredentialId()).build();

                        output.add(key);
                    }
                }
            }
            return output;
        } catch (DatabaseException exception) {
            return new HashSet<>();
        }
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        try {
            UserRecordStore record = UserRecordConnector.findByUserName(username);
            return Optional.of(ByteArray.fromBase64(record.getUserHandle()));
        } catch (DatabaseException exception) {
//            TODO Log stuff here
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        try {
            UserRecordStore record = UserRecordConnector.findByUserHandle(userHandle);
            return Optional.of(record.getUniqueName());
        } catch (DatabaseException exception) {
//            TODO log stuff here
            return Optional.empty();
        }
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        try {
            RegisteredCredential credential = UserRecordConnector.findCredential(credentialId, userHandle);
            return Optional.of(credential);
        } catch (DatabaseException databaseException) {
//            TODO logs stuff here.
            return Optional.empty();
        }
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray byteArray) {
//        TODO actually return list of credentials
        Set<RegisteredCredential> out = new HashSet<>();

        return out;
    }
}