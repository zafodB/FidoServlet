/* Made by Filip Adamik on 17/05/2019 */

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

/**
 * PRovides methods to manipulate Sign-in requests in the database.
 */
public class SigninRequestConnector {
    private static final MongoCollection<SigninRequestStore> collection;


    // Bootstrap connection to database
    static {
        MongoClient mongoClient = MongoClients.create();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoDatabase database = mongoClient.getDatabase(DatabaseReference.DATABASE_NAME).withCodecRegistry(pojoCodecRegistry);

        collection = database.getCollection(DatabaseReference.SIGNIN_REQUESTS_COLLECTION_NAME, SigninRequestStore.class);
    }

    /**
     * Add Sign-in request to database.
     *
     * @param requestId ID of the new request.
     * @param json Request as JSON.
     */
    public static void addRecord(String requestId, String json){
        collection.insertOne(new SigninRequestStore(requestId, json));
    }

    /**
     * Retrieve existing request by its ID.
     *
     * @param requestId ID of the request to be retrieved.
     * @return Sign-in request as {@link SigninRequestStore SigninRequestStore} object.
     * @throws DatabaseException Throws an exception if the record was not found in the database.
     */
    public static SigninRequestStore getRecord(String requestId) throws DatabaseException{
        SigninRequestStore request = collection.find(eq(DatabaseReference.SIGNIN_REQUEST_PARAMETER_ID, requestId)).first();

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
        collection.deleteOne(eq(DatabaseReference.SIGNIN_REQUEST_PARAMETER_ID, requestId));
    }

    /**
     * Delete entire collection
     */
    public static void dropCollection(){
        collection.drop();
    }
}


