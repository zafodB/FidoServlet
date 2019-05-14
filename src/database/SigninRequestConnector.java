package database;

import Model.SigninRequestStore;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class SigninRequestConnector {
    static MongoCollection<SigninRequestStore> collection;

    static {
        MongoClient mongoClient = MongoClients.create();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoDatabase database = mongoClient.getDatabase("userDirectory").withCodecRegistry(pojoCodecRegistry);

        collection = database.getCollection("signinRequests", SigninRequestStore.class);
    }

    public static void addRecord(String requestId, String json){
        collection.insertOne(new SigninRequestStore(requestId, json));
    }

    public static SigninRequestStore getRecord(String requestId){
        return collection.find(eq("requestId", requestId)).first();
    }

    public static void dropCollection(){
        collection.drop();
    }
}


