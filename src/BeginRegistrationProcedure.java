/* Made by Filip Adamik on 17/05/2019 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.UserIdentity;
import database.RegistrationRequestConnector;
import database.UserRecordConnector;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

public class BeginRegistrationProcedure extends HttpServlet {
    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws java.io.IOException {
        doPost(request, response);
    }

    /**
     * Begin the registration process by creating a registration request, sending the request to the client and saving
     * the request in the database. If the email is already registered, returns response with code 409.
     *
     * @param request  Request contains email address (unique name) and user name (display name) chosen by the user.
     * @param response Response contains the registration request as JSON. Alternatively, if the unique name already
     *                 exists in the database, the response carries code 409 and a description of the error.
     */
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws java.io.IOException {

        String uniqueUserName = request.getParameter("uniqueName");
        String displayName = request.getParameter("displayName");

        response.setContentType("application/json");

//        Check if username exists in the database.
        if (UserRecordConnector.userNameExists(uniqueUserName)) {
//            response.getWriter().println("{\"result\":\"Email already registered!\"}");
            response.sendError(HttpServletResponse.SC_CONFLICT, "Email already registered!");
        } else {

//            Create a registration request for the user details
            PublicKeyCredentialCreationOptions registrationRequest = RpInstance.rp.startRegistration(StartRegistrationOptions.builder()
                    .user(UserIdentity.builder()
                            .name(uniqueUserName)
                            .displayName(displayName)
                            .id(UserRecordConnector.generatUserHandle())
                            .build())
                    .build());

//            Initialize a JSON parser.
            ObjectMapper jsonMapper = new ObjectMapper()
                    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                    .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
                    .registerModule(new Jdk8Module());

//            Parse request to JSON
            String registrationRequestJson = jsonMapper.writeValueAsString(registrationRequest);
//            Save Request in the database
            RegistrationRequestConnector.addRecord(registrationRequest.getChallenge().toJsonString(), registrationRequestJson);
//            Send Request to Client
            response.getWriter().println(registrationRequestJson);
        }
    }
}
