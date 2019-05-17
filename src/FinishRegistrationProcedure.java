/* Made by Filip Adamik on 17/05/2019 */

import Model.RegistrationRequestStore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.data.*;
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String keyData = req.getParameter("keyData");
        String userData = req.getParameter("userData");

        keyData = keyData.replace('/', '_');
        keyData = keyData.replace('+', '-');

        PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkcResponse =
                PublicKeyCredential.parseRegistrationResponseJson(keyData);

        ObjectMapper jsonMapper = new ObjectMapper()
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
                .registerModule(new Jdk8Module());

        ByteArray challengeEncoded = pkcResponse.getResponse().getClientData().getChallenge();

            try {
                RegistrationRequestStore registrationRequestRecord = RegistrationRequestConnector.getRecord(new String(challengeEncoded.getBytes()));
                String pkRecordChallenge = recodeChallenge(registrationRequestRecord.getRequestAsJson());

                PublicKeyCredentialCreationOptions pkcRequest = jsonMapper
                        .readValue(pkRecordChallenge, PublicKeyCredentialCreationOptions.class);

                RegistrationResult result = RpInstance.rp.finishRegistration(FinishRegistrationOptions.builder()
                        .request(pkcRequest)
                        .response(pkcResponse)
                        .build());


                UserRecordConnector.addRecord(generatUserHandle(), userData, "Alice Wonder", result.getKeyId().getId(), result.getPublicKeyCose());

                resp.getWriter().println("{\"result\":\"Success!\"}");
            }
            catch (DatabaseException databaseE){
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "The Registration Request Record was not found in the database.");
            }
            catch (Exception e){}

//            System.out.println("The error is: " + e.getMessage());
//            e.printStackTrace();
//
//            resp.getWriter().println(e.getMessage());
    }

    private String recodeChallenge(String input) {
        int cutAtPosition = input.lastIndexOf("\"challenge\":\"") + 13;
        int cutUntil = input.lastIndexOf("\",\"pubKeyCredParams\"");

        return RpInstance.recode(cutAtPosition, cutUntil, input);
    }

}
