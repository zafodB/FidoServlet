/* Made by Filip Adamik on 17/05/2019 */

import Model.RegistrationRequestStore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.data.*;
import com.yubico.webauthn.exception.RegistrationFailedException;
import database.DatabaseException;
import database.RegistrationRequestConnector;
import database.UserRecordConnector;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static database.UserRecordConnector.generatUserHandle;

public class FinishRegistrationProcedure extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }

    /**
     * Finish the Registration procedure by verifying that the signed response received from the authenticator
     * matches the data stored in the database.
     *
     * @param req  Request carrying the signed response and user identifier.
     * @param resp Text informing about success or failure
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

//        Retrieve and prepare the date from the response
        String keyData = req.getParameter("keyData");
        String userUniqueName = req.getParameter("userUniqueName");
        String userDisplayName = req.getParameter("userDisplayName");
        keyData = keyData.replace('/', '_');
        keyData = keyData.replace('+', '-');
        PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> registrationRequestResponse =
                PublicKeyCredential.parseRegistrationResponseJson(keyData);

//        Prepare the JSON parser
        ObjectMapper jsonMapper = new ObjectMapper()
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
                .registerModule(new Jdk8Module());
//        Retrieve the challenge as String to use in database query.
        String challengeEncoded = new String(registrationRequestResponse.getResponse().getClientData().getChallenge()
                .getBytes());

        try {
//            Find the stored request in the database by the challenge from the response,
            RegistrationRequestStore registrationRequestRecord = RegistrationRequestConnector.getRecord(challengeEncoded);
//            Recode challenge to match encoding required by the Yubico parser.
            String registrationRequestStored = recodeChallenge(registrationRequestRecord.getRequestAsJson());

//            Turn JSON string to Java representation of the request.
            PublicKeyCredentialCreationOptions registrationRequest = jsonMapper
                    .readValue(registrationRequestStored, PublicKeyCredentialCreationOptions.class);

//            Compare and verify the request and the received result. If the result is not successful, a registraiton exception is thrown.
            RegistrationResult result = RpInstance.rp.finishRegistration(FinishRegistrationOptions.builder()
                    .request(registrationRequest)
                    .response(registrationRequestResponse)
                    .build());

//            Add user record to the database.
            UserRecordConnector.addRecord(generatUserHandle(), userUniqueName, userDisplayName, result.getKeyId().getId(), result.getPublicKeyCose());
//            Remove registration request from the database.
            RegistrationRequestConnector.removeRecord(challengeEncoded);

            resp.getWriter().println("{\"result\":\"Success!\"}");
        } catch (DatabaseException | RegistrationFailedException registrationE) {
            resp.getWriter().println("{\"Error\":\"The Registration Request Record was not found in the database.\"}");
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "The Registration Request Record was not found in the database or the response doesn't match.");
        }
    }

    /**
     * Recode challenge to Base64 encoding.
     * @param input request as String
     * @return Request as String with the challenge bit recorded to Base64
     */
    private String recodeChallenge(String input) {
        int cutAtPosition = input.lastIndexOf("\"challenge\":\"") + 13;
        int cutUntil = input.lastIndexOf("\",\"pubKeyCredParams\"");

        return RpInstance.recode(cutAtPosition, cutUntil, input);
    }

}
