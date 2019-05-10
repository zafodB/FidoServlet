package database;

import Model.PkRequestStore;
import Model.UserRecord;
import com.mongodb.Block;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

    public static void addRecord(String uniqueName, String displayName, ByteArray publicKey, PublicKeyCredentialDescriptor publicKeyDescriptor){
        collection.insertOne(new UserRecord(uniqueName, displayName, publicKey, publicKeyDescriptor));
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
}
