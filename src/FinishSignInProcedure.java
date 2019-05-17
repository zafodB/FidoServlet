import Model.SigninRequestStore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.exception.AssertionFailedException;
import database.SigninRequestConnector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FinishSignInProcedure extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        System.out.println("The entire response is: " + req.getParameter("data"));

        String responseData = recodeSignature(req.getParameter("data"));
        responseData = recodeCredentialId(responseData);

//        TODO work here what happens when they sign.

        System.out.println("Response data is: " + responseData);


//        TODO proper error handling

        try {
            PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> pkc =
                    PublicKeyCredential.parseAssertionResponseJson(responseData);

            System.out.println("Successuflly parsed the JSON");


            System.out.println("Challenge from PKC request is: " + new String(pkc.getResponse().getClientData().getChallenge().getBytes()));

            String requestId = new String(pkc.getResponse().getClientData().getChallenge().getBytes());
            SigninRequestStore requestStore = SigninRequestConnector.getRecord(requestId);
            String requestAsJson = requestStore.getSigninRequestAsJson();
            System.out.println("Request is: " + requestAsJson);

            String recodedRequest = recodeChallenge(requestAsJson);


            ObjectMapper jsonMapper = new ObjectMapper()
                    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                    .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
                    .registerModule(new Jdk8Module());

            AssertionRequest request = jsonMapper.readValue(recodedRequest, AssertionRequest.class);

            AssertionResult result = RpInstance.rp.finishAssertion(FinishAssertionOptions.builder()
                    .request(request)
                    .response(pkc)
                    .build());

            if (result.isSuccess()) {
                System.out.println("SUCCESS!");
                resp.setContentType("application/json");
                resp.getWriter().println("{\"result\":\"Success!\"}");
//                return result.getUsername();
            } else {
                resp.getWriter().println("{\"result\":\"Not successful.\"}");
            }
        } catch (Exception e) {

            System.out.println("WTF!");
            resp.getWriter().println("{\"result\":\"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    private String recodeSignature(String input) {

        String output = input.replace("+", "-");
        output = output.replace("/", "_");

        return output;

//        int cutAtPosition = input.lastIndexOf("\"signature\":\"") + 13;
//        int cutUntil = input.length() - 3;
//
//        return RpInstance.recode(cutAtPosition, cutUntil, input);
    }

    private String recodeCredentialId(String input) {
        return input;
//        int cutAtPosition = input.lastIndexOf("{\"id\":\"") + 7;
//        int cutUntil = input.lastIndexOf("\",\"type\"");
//
//        return RpInstance.recode(cutAtPosition, cutUntil, input);
    }

    private String recodeChallenge(String input) {
        int cutAtPosition = input.lastIndexOf("\":{\"challenge\":\"") + 16;
        int cutUntil = input.lastIndexOf("\",\"rpId\"");

        return RpInstance.recode(cutAtPosition, cutUntil, input);
    }

}
