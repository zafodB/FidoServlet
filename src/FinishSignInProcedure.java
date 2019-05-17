import Model.SigninRequestStore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.exception.AssertionFailedException;
import database.DatabaseException;
import database.SigninRequestConnector;
import database.UserRecordConnector;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FinishSignInProcedure extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }

//    TODO logging in entire process.

    /**
     * Finalize the sign-in process by comparing the stored assertion request with assertion response supplied by the client.
     *
     * @param req  This method request contains an assertion response generated at the client side after the user has used their authenticator.
     * @param resp Success message and user display name if successful, HTTP 403 FORBIDDEN otherwise.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

//        Recode the response to other variant of Base64 encoding.
        String responseString = recodeResponse(req.getParameter("data"));

//        Create an assertion response object out of response string.
        PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> assertionResponse =
                PublicKeyCredential.parseAssertionResponseJson(responseString);

//        Extract assertion ID for use in the database.
        String requestId = new String(assertionResponse.getResponse().getClientData().getChallenge().getBytes());

        try {
//            Get assertion request from the database by the assertion ID
            SigninRequestStore requestStore = SigninRequestConnector.getRecord(requestId);

//            Prepare and parse the assertion request
            String requestAsJson = requestStore.getSigninRequestAsJson();
            String recodedRequest = recodeChallenge(requestAsJson);

//            Initialize JSON parser and parse the request into an AssertionRequest object
            ObjectMapper jsonMapper = new ObjectMapper()
                    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                    .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
                    .registerModule(new Jdk8Module());
            AssertionRequest assertionRequest = jsonMapper.readValue(recodedRequest, AssertionRequest.class);

//            Compare and verify assertion request and assertion result. If they don't match, an AssertionFailedException is thrown.
            AssertionResult result = RpInstance.rp.finishAssertion(FinishAssertionOptions.builder()
                    .request(assertionRequest)
                    .response(assertionResponse)
                    .build());

//            If the assertion was successfully verified, proceed to sign the user in.
            if (result.isSuccess()) {
                String name;

                try {
                    name = UserRecordConnector.findByUserName(result.getUsername()).getDisplayName();
                } catch (DatabaseException databaseE) {
                    name = "unknown";
                }

                resp.getWriter().println("{\"result\":\"Successfully signed in the user with display name: " + name + "\"}");
                SigninRequestConnector.removeRecord(requestId);

            } else {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        } catch (AssertionFailedException | DatabaseException exception) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    /**
     * Recode the JSON response from Base64Url to regular (plain) Base64 encoding.
     *
     * @param input Original JSON response
     * @return Recoded JSON response in Base64 encoding.
     */
    private String recodeResponse(String input) {
        String output = input.replace("+", "-");
        output = output.replace("/", "_");
        return output;

    }

    /**
     * Recode challenge from regular string into Base64 encoded string.
     *
     * @param input Response as JSON
     * @return Response as JSON with the challenge bit recoded.
     */
    private String recodeChallenge(String input) {
        int cutAtPosition = input.lastIndexOf("\":{\"challenge\":\"") + 16;
        int cutUntil = input.lastIndexOf("\",\"rpId\"");

        return RpInstance.recode(cutAtPosition, cutUntil, input);
    }

}
