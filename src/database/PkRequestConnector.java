package database;

import Model.PkRequestStore;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.Arrays;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class PkRequestConnector {

    static MongoCollection<PkRequestStore> collection;

    static {
        MongoClient mongoClient = MongoClients.create();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoDatabase database = mongoClient.getDatabase("userDirectory").withCodecRegistry(pojoCodecRegistry);

        collection = database.getCollection("pkRequests", PkRequestStore.class);

    }

    public static void addRecord(String requestId, String json){
        collection.insertOne(new PkRequestStore(requestId, json));
    }

//    public static void compareTwo(String challenge){
//        PkRequestStore fist = collection.find().first();
//
//        System.out.println(Arrays.toString(fist.getRequestId().getBytes()));
//
//        System.out.println(Arrays.toString(challenge.getBytes()));
//
//        System.out.println("Are they the same? " + challenge.equals(fist.getRequestId()));
//    }

    public static PkRequestStore getRecord(String requestId){
        PkRequestStore record = collection.find(eq("requestId", requestId)).first();

        return record;
    }

    public static void dropCollection(){
        collection.drop();
    }
}
