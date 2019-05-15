package database;

import Model.PkRequestStore;
import Model.RegisteredCredentialStore;
import Model.UserRecord;
import com.mongodb.Block;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import static com.mongodb.client.model.Filters.eq;

import java.util.*;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class UserRecordConnector {

    static MongoCollection<UserRecord> collection;

    static {
        MongoClient mongoClient = MongoClients.create();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoDatabase database = mongoClient.getDatabase("userDirectory").withCodecRegistry(pojoCodecRegistry);

        collection = database.getCollection("userRecords", UserRecord.class);
    }

    public static void addRecord(ByteArray userHandle, String uniqueName, String displayName, ByteArray keyId,
                                 ByteArray publicKeyCose){

        UserRecord existingRecord = collection.find(eq("userHandle", userHandle)).first();
        if (existingRecord != null){
            Map<String, RegisteredCredentialStore> credentials = existingRecord.getCredentials();

            RegisteredCredentialStore newCredential =
                    new RegisteredCredentialStore(keyId.getBase64(), publicKeyCose.getBase64(),
                            userHandle.getBase64(), 0);

            credentials.put(newCredential.getCredentialId(), newCredential);

            existingRecord.setCredentials(credentials);

            collection.replaceOne(eq("userHandle", userHandle), existingRecord);
        }
        else {
            Map<String, RegisteredCredentialStore> credentials = new HashMap<>();
            RegisteredCredentialStore newCredential =
                    new RegisteredCredentialStore(keyId.getBase64(), publicKeyCose.getBase64(),
                            userHandle.getBase64(), 0);


            credentials.put(newCredential.getCredentialId(), newCredential);

            collection.insertOne(new UserRecord(userHandle.getBase64(), uniqueName, displayName, credentials));
        }
    }

//    public static List<UserRecord> findAllUserNames(String uniqueName){
//        List<UserRecord> output = new ArrayList<>();
//
//        collection.find(eq("uniqueName", uniqueName)).forEach((Block<UserRecord>) output::add);
//
//        return output;
//    }

    public static UserRecord findByUserName(String uniqueName){
        return collection.find(eq("uniqueName", uniqueName)).first();
    }

    public static UserRecord findByUserHandle(ByteArray userHandle){
        return collection.find(eq("userHandle", userHandle.getBase64())).first();
    }

    public static RegisteredCredential findCredential(ByteArray credentialId, ByteArray userHandle){
        System.out.println();
        String databaseQueryUser = userHandle.getBase64();
        String databaseQueryCredential = credentialId.getBase64();

        System.out.println("Database query credentials: " + databaseQueryCredential);
        System.out.println("Database query userHandle : " + databaseQueryUser);


        UserRecord record = collection.find(eq("userHandle", databaseQueryUser)).first();



        RegisteredCredentialStore storedCredential = record.getCredentials().get(databaseQueryCredential);

        return RegisteredCredential.builder().credentialId(credentialId)
                .userHandle(ByteArray.fromBase64(storedCredential.getUserHandle()))
                .publicKeyCose(ByteArray.fromBase64(storedCredential.getPublicKeyCose()))
                .build();
    }

    public static ByteArray generatUserHandle(){
        byte[] randBytes = new byte[10];
        Random rd = new Random();
        rd.nextBytes(randBytes);
        return new ByteArray(randBytes);
    }
}
