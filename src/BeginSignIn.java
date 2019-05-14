import Model.SigninRequestStore;
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

public class BeginSignIn extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String userEmail = req.getParameter("userData");

        System.out.println("User email was extracted and is: " + userEmail);

//        TODO break the process if email is unknown.
        AssertionRequest request = RpInstance.rp.startAssertion(StartAssertionOptions.builder()
                .username(Optional.of(userEmail))
                .build());

        ObjectMapper jsonMapper = new ObjectMapper()
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
                .registerModule(new Jdk8Module());

        String json = jsonMapper.writeValueAsString(request);

//        String userHandle = UserRecordConnector.findByUserName(request.getUsername().get()).getUserHandle();

        SigninRequestConnector.addRecord(request.getPublicKeyCredentialRequestOptions().getChallenge().getBase64Url(), json);

        resp.getWriter().println(json);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
