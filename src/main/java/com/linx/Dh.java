package com.linx;

import com.linx.PasswordStorer.PasswordDataClass;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Dh {
    private static final MongoClient mongoClient = new MongoClient("localhost", 27017);
    private static final MongoDatabase mongoDatabase = mongoClient.getDatabase("password_manager");
    private static final MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("saved");
    private static final MongoCollection<Document> masterMongoCollection = mongoDatabase.getCollection("master");
    private static final Encryption encryption = new Encryption();

    /**
     * Writes an entry to the database collection by generating a document of the passwordDataClass
     * _id is autogenerated by mongodb
     * @param passwordDataClass Representation of the mongodb data
     */
    public void addEntry(PasswordDataClass passwordDataClass){
        Document document = new Document();
        document.append("Description", passwordDataClass.getDescription());
        document.append("Username", passwordDataClass.getUsername());

        try {
            document.append(
                    "Password",
                    encryption.encrypt_AES256(passwordDataClass.getPassword())
            );
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }

        mongoCollection.insertOne(document);
    }

    /**
     * Delete an existing entry in the database collection (saved).
     * Identification done using the _id generated by mongodb
     * Ideally the scenario where the data does not exist should not occur
     * @param passwordDataClass Representation of the mongodb data
     */
    public void deleteEntry(PasswordDataClass passwordDataClass){
        mongoCollection.deleteOne(
                Filters.eq("_id", passwordDataClass.getId())
        );
    }

    /**
     * Modifies values for an already existing data in the collection (saved).
     * Identification done using the _id generated by mongodb
     * @param passwordDataClass Representation of the mongodb data
     */
    public void updateEntry(PasswordDataClass passwordDataClass){
        try {
            mongoCollection.updateOne(
                    Filters.eq("_id", passwordDataClass.getId()),
                    Updates.combine(
                            Updates.set("Description", passwordDataClass.getDescription()),
                            Updates.set("Username", passwordDataClass.getUsername()),
                            Updates.set(
                                    "Password",
                                    encryption.encrypt_AES256(passwordDataClass.getPassword())
                            )
                    )
            );
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }


    public ArrayList<PasswordDataClass> fetchAll(){
        ArrayList<PasswordDataClass> savedPasswords = new ArrayList<>();
        FindIterable<Document> iterable = mongoCollection.find();

        for (Document entry: iterable){
            PasswordDataClass passwordEntry;
            try {
                passwordEntry = new PasswordDataClass(
                        (ObjectId) entry.get("_id"),
                        (String) entry.get("Description"),
                        (String) entry.get("Username"),
                        encryption.decrypt_AES256(
                                (String) entry.get("Password")
                        )

                );
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                     InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException(e);
            }
            savedPasswords.add(passwordEntry);
        }
        return savedPasswords;
    }

    /**
     * Creates an entry in the collection (master) for the master password used for the application
     * @param password  The password entered by the user for creation
     * @throws NoSuchAlgorithmException  y
     */
    public void createMasterPassword(String password) throws NoSuchAlgorithmException {
        String pass_enc = encryption.encrypt_SHA256(password);

        Document document = new Document("password", pass_enc);
        masterMongoCollection.insertOne(document);
    }

    /**
     * Checks if the password provided in the parameter matches the password stored in the database collection (master collection)
     * @param password  Password entered by the user for access to password manager
     * @return  True if the param password matches the database password
     * @throws NoSuchAlgorithmException In case the SHA256 algorithm does not exist
     */
    public boolean checkMasterPassword(String password) throws NoSuchAlgorithmException {
        String pass_enc = encryption.encrypt_SHA256(password);

        FindIterable<Document> iterable = masterMongoCollection.find();
        String key = "";

        for (Document entry: iterable){
            key = (String) entry.get("password");
        }

        return pass_enc.equals(key);
    }

    /**
     *  Checks if master password has been created
     * @return returns boolean true if the masterMongoCollection holds the master password
     */
    public boolean masterPasswordExists(){
        return masterMongoCollection.countDocuments() == 1;
    }
}