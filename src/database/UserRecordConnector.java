/* Made by Filip Adamik on 17/05/2019 */

package database;

import Model.RegisteredCredentialStore;
import Model.UserRecordStore;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Provides methods to manipulate user records in the database.
 */
public class UserRecordConnector {

    private static MongoCollection<UserRecordStore> collection;

    //Bootstrap connection to the database
    static {
        MongoClient mongoClient = MongoClients.create();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoDatabase database = mongoClient.getDatabase(DatabaseReference.DATABASE_NAME).withCodecRegistry(pojoCodecRegistry);

        collection = database.getCollection(DatabaseReference.USER_RECORDS_COLLECTION_NAME, UserRecordStore.class);
    }

    /**
     * Add a new user record entry to the database.
     *
     * @param userHandle    Unique identifier of the user. Respective to
     *                      <a href="https://www.w3.org/TR/webauthn/#user-handle">user handle defined by Webauthn</a>.
     * @param uniqueName    Unique name, intended to be displayed to the user
     * @param displayName   Display name, intended to be displayed to the user. Doesn't need to be unique.
     * @param keyId         ID number of the physical key registered with the user account.
     * @param publicKeyCose COSE representation of the Public key value of the registered physical key.
     */
    public static void addRecord(ByteArray userHandle, String uniqueName, String displayName, ByteArray keyId,
                                 ByteArray publicKeyCose) {

        UserRecordStore existingRecord = collection.find(eq("userHandle", userHandle)).first();

        RegisteredCredentialStore newCredential =
                new RegisteredCredentialStore(keyId.getBase64(), publicKeyCose.getBase64(),
                        userHandle.getBase64(), 0);

        if (existingRecord != null) {
            Map<String, RegisteredCredentialStore> credentials = existingRecord.getCredentials();

            credentials.put(newCredential.getCredentialId(), newCredential);

            existingRecord.setCredentials(credentials);

            collection.replaceOne(eq("userHandle", userHandle), existingRecord);
        } else {
            Map<String, RegisteredCredentialStore> credentials = new HashMap<>();

            credentials.put(newCredential.getCredentialId(), newCredential);

            collection.insertOne(new UserRecordStore(userHandle.getBase64(), uniqueName, displayName, credentials));
        }
    }

    /**
     * Retrieve user record from the database by the unique name.
     *
     * @param uniqueName Unique name, user-readable.
     * @return User record as a {@link UserRecordStore} object.
     * @throws DatabaseException Throws an exception if the user record was not found.
     */
    public static UserRecordStore findByUserName(String uniqueName) throws DatabaseException{
        UserRecordStore record = collection.find(eq(DatabaseReference.USER_RECORD_PARAMETER_UNIQUENAME, uniqueName)).first();

        if (record == null){
            throw new DatabaseException("The user record was not found in the database for unique name: " + uniqueName);
        }

        return record;
    }

    /**
     * Retireve user record from the database by the user handle.
     *
     * @param userHandle unique user handle, opaque to the user.
     * @return User record as a {@link UserRecordStore} object.
     * @throws DatabaseException Throws an exception if the user record was not found.
     */
    public static UserRecordStore findByUserHandle(ByteArray userHandle) throws DatabaseException{
        UserRecordStore record = collection.find(eq(DatabaseReference.USER_RECORD_PARAMETER_USERHANDLE, userHandle.getBase64())).first();

        if (record == null){
            throw new DatabaseException("The user records was not found in the database for user handle: " + userHandle.getBase64());
        }

        return record;
    }

    /**
     * Retrieve a single credential belonging to a user based on user handle and the credential ID.
     *
     * @param credentialId ID of the requested credential.
     * @param userHandle   user handle of the owner of the credential.
     * @return The existing, previously registered credentials as a {@link RegisteredCredential} object.
     */
    public static RegisteredCredential findCredential(ByteArray credentialId, ByteArray userHandle) throws DatabaseException {
        String databaseQueryUser = userHandle.getBase64();
        String databaseQueryCredential = credentialId.getBase64();

        UserRecordStore record = collection.find(eq(DatabaseReference.USER_RECORD_PARAMETER_USERHANDLE, databaseQueryUser)).first();

        if (record == null) {
            throw new DatabaseException("The user record was not found in the database.");
        } else if (record.getCredentials().isEmpty()) {
            throw new DatabaseException("The user record has no associated credentials.");
        }

        RegisteredCredentialStore storedCredential = record.getCredentials().get(databaseQueryCredential);

        if (storedCredential == null) {
            throw new DatabaseException("The requested record was not found in user's credentials.");
        }

        return RegisteredCredential.builder().credentialId(credentialId)
                .userHandle(ByteArray.fromBase64(storedCredential.getUserHandle()))
                .publicKeyCose(ByteArray.fromBase64(storedCredential.getPublicKeyCose()))
                .build();
    }

    /**
     * Generate new random user handle
     *
     * @return ByteArray with random bytes representing the user handle.
     */
    public static ByteArray generatUserHandle() {
        byte[] randBytes = new byte[20];
        Random rd = new Random();
        rd.nextBytes(randBytes);
        return new ByteArray(randBytes);
    }
}
