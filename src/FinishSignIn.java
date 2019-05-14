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

public class FinishSignIn extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("The entire request is: " + req.getParameter("data"));

        String responseData = recodeSignature(req.getParameter("data"));
        responseData = recodeCredentialId(responseData);

//        TODO work here what happens when they sign.

        System.out.println("Response data is: " + responseData);

        PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> pkc =
                PublicKeyCredential.parseAssertionResponseJson(responseData);

//        TODO proper error handling

        try{

            System.out.println("Challenge from PKC request is: " + new String(pkc.getResponse().getClientData().getChallenge().getBytes()));

            String requestId = new String(pkc.getResponse().getClientData().getChallenge().getBytes());
            SigninRequestStore requestStore = SigninRequestConnector.getRecord(requestId);
            String requestAsJson = requestStore.getSigninRequestAsJson();
            System.out.println("Request is: " + requestAsJson);



            ObjectMapper jsonMapper = new ObjectMapper()
                    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                    .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
                    .registerModule(new Jdk8Module());

            AssertionRequest request = jsonMapper.readValue(requestAsJson, AssertionRequest.class);

            AssertionResult result = RpInstance.rp.finishAssertion(FinishAssertionOptions.builder()
                    .request(request)
                    .response(pkc)
                    .build());

            if (result.isSuccess()) {
                System.out.println("SUCCESS!");
                resp.getWriter().println("{\"result\":\"Success!\"}");
//                return result.getUsername();
            }

            resp.getWriter().println("{\"result\":\"Not successful.\"}");
        } catch (Exception e) {
            resp.getWriter().println("{\"result\":\""+ e.getMessage() +"\"}");
            e.printStackTrace();
        }
    }

    private String recodeSignature(String input){
        int cutAtPosition = input.lastIndexOf("\"signature\":\"") + 13;
        int cutUntil = input.length() - 3;

        return RpInstance.recode(cutAtPosition, cutUntil, input);
    }

    private String recodeCredentialId(String input){
        int cutAtPosition = input.lastIndexOf("{\"id\":\"") + 8;
        int cutUntil = input.lastIndexOf("\",\"type\"");

        return RpInstance.recode(cutAtPosition, cutUntil, input);
    }

}
