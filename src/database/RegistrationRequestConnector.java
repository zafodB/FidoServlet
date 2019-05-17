/* Made by Filip Adamik on 17/05/2019 */

package database;

import Model.RegisteredCredentialStore;
import Model.RegistrationRequestStore;
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

/**
 * Provides methods to manipulate Registration Request entries in the database.
 */
public class RegistrationRequestConnector {

    private static MongoCollection<RegistrationRequestStore> collection;

    // Bootstrap connection to the database.
    static {
        MongoClient mongoClient = MongoClients.create();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoDatabase database = mongoClient.getDatabase(DatabaseReference.DATABASE_NAME).withCodecRegistry(pojoCodecRegistry);

        collection = database.getCollection(DatabaseReference.REGISTRATION_REQUESTS_COLLECTION_NAME, RegistrationRequestStore.class);
    }

    /**
     * Add new RegistrationRequest
     * @param requestId ID of the new request.
     * @param json Request in the JSON form.
     */
    public static void addRecord(String requestId, String json){
        collection.insertOne(new RegistrationRequestStore(requestId, json));
    }

    /**
     * Retrieve an existing Registration Request.
     * @param requestId requestId ID of the request to be retrieved.
     * @return Registration request as {@link RegistrationRequestStore RegistrationRequestStore} object.
     * @throws DatabaseException Throws an exception if the record was not found in the database.
     */
    public static RegistrationRequestStore getRecord(String requestId) throws DatabaseException{
        RegistrationRequestStore request = collection.find(eq(DatabaseReference.REGISTRATION_REQUEST_PARAMETER_ID, requestId)).first();

        if (request == null){
            throw new DatabaseException("The request was not found in the database.");
        }

        return  request;
    }

    /**
     * Remove a single record from the database based on the request ID
     * @param requestId ID of the request to be removed.
     */
    public static void removeRecord(String requestId){
        collection.deleteOne(eq(DatabaseReference.REGISTRATION_REQUEST_PARAMETER_ID, requestId));
    }

    /**
     * Delete entire collection.
     */
    public static void dropCollection(){
        collection.drop();
    }
}
