/* Made by Filip Adamik on 17/05/2019 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.StartAssertionOptions;
import database.SigninRequestConnector;
import database.UserRecordConnector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class BeginSignInProcedure extends HttpServlet {

    /**
     * Begin sign in procedure when the client requests a challenge from the server.
     *
     * @param req  Request contains client's unique name.
     * @param resp Response contains sign-in challenge.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String userEmail = req.getParameter("userData");

        if (UserRecordConnector.userNameExists(userEmail)) {

//            Build a sign-in request for the client.
            AssertionRequest request = RpInstance.rp.startAssertion(StartAssertionOptions.builder()
                    .username(Optional.of(userEmail))
                    .build());

//            Initialize JSON parser
            ObjectMapper jsonMapper = new ObjectMapper()
                    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                    .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
                    .registerModule(new Jdk8Module());

            String json = jsonMapper.writeValueAsString(request);

//            Save the sign-in request to database under challenge ID.
            SigninRequestConnector.addRecord(request.getPublicKeyCredentialRequestOptions().getChallenge().getBase64Url(), json);

            resp.getWriter().println(json);
        } else {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
