import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.data.*;
import database.PkRequestConnector;
import database.UserRecordConnector;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FinalizeCredentials extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        try {
            String data = req.getParameter("data");

            data = data.replace('/','_');
            data = data.replace('+','-');

            System.out.println("Data is: " + data);
            PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc =
                    PublicKeyCredential.parseRegistrationResponseJson(data);

            ObjectMapper jsonMapper = new ObjectMapper()
                    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                    .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
                    .registerModule(new Jdk8Module());


            ByteArray challengeEncoded = pkc.getResponse().getClientData().getChallenge();

            String pkRecord = recodeChallenge(PkRequestConnector.getRecord(new String(challengeEncoded.getBytes())));


            PublicKeyCredentialCreationOptions pkcRequest = jsonMapper.readValue(pkRecord, PublicKeyCredentialCreationOptions.class);

            RegistrationResult result = RpInstance.rp.finishRegistration(FinishRegistrationOptions.builder()
                    .request(pkcRequest)
                    .response(pkc)
                    .build());

            UserRecordConnector.addRecord("alice@example.com", "Alice Wonder", result.getPublicKeyCose(), result.getKeyId());

            resp.setContentType("application/json");
            resp.getWriter().println("result:\"Success!\"");


        } catch (Exception e){
            System.out.println("The error is: " + e.getMessage());
            e.printStackTrace();
            resp.setContentType("application/json");
            resp.getWriter().println(e.getMessage());
        }
    }

    private String recodeChallenge(String input){
        int cutAtPosition = input.lastIndexOf("\"challenge\":\"") + 13;
        int cutUntil = input.lastIndexOf("\",\"pubKeyCredParams\"");

        System.out.println("JSON input is: " + input);
        String firstPart = input.substring(0, cutAtPosition);
        String challenge = input.substring(cutAtPosition, cutUntil);
        String lastPart = input.substring(cutUntil);

//        System.out.println("First part: " + firstPart);
//        System.out.println("Challenge: " + challenge);
//        System.out.println("Last part: " + lastPart);

        ByteArray challengeByteArray = new ByteArray(challenge.getBytes());
        String challenge64 = challengeByteArray.getBase64Url();

        System.out.println("JSON output is: " + firstPart + challenge64 + lastPart);

        return firstPart + challenge64 + lastPart;
    }


}
