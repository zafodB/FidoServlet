import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import com.yubico.webauthn.data.UserIdentity;
import org.pac4j.core.config.Config;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;
import javax.servlet.http.*;

public class FidoTestSecond extends HttpServlet {
    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {

    }

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws java.io.IOException {
        // Set the response message's MIME type
        response.setContentType("application/json");

        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
                .id("example.com")
                .name("Example Application")
                .build();

        RelyingParty rp = RelyingParty.builder()
                .identity(rpIdentity)
                .credentialRepository(new MyCredentialRepository())
                .build();


        byte[] originByteArray;
        byte[] AliceUserBytes = null;
        {
            try {
                originByteArray = "This Is alice in wonderland and I hope this text is long enough".getBytes("UTF-8");
                AliceUserBytes = Arrays.copyOfRange(originByteArray, 0, 64);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        rp.getCredentialRepository().getCredentialIdsForUsername().

        PublicKeyCredentialCreationOptions pkrequest = rp.startRegistration(StartRegistrationOptions.builder()
                .user(UserIdentity.builder()
                        .name("alice")
                        .displayName("Alice Hypothetical")
                        .id(new ByteArray(AliceUserBytes))
                        .build())
                .build());

        ObjectMapper jsonMapper = new ObjectMapper()
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
                .registerModule(new Jdk8Module());

        String json = jsonMapper.writeValueAsString(pkrequest);

        response.getWriter().println(json);

//
//        // Allocate a output writer to write the response message into the network socket
//        PrintWriter out = response.getWriter();
//
////        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
////                .id("example.com")
////                .name("Example Application")
////                .build();
////
////        RelyingParty rp = RelyingParty.builder()
////                .identity(rpIdentity)
////                .credentialRepository(new MyCredentialRepository())
////                .build();
////
////        ObjectMapper jsonMapper = new ObjectMapper()
////                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
////                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
////                .registerModule(new Jdk8Module());
////
////        String json = jsonMapper.writeValueAsString(request);
//
//
//        // Write the response message, in an HTML page
//        try {
//            out.println("<!DOCTYPE html>");
//            out.println("<html><head>");
//            out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
//            out.println("<title>Hello, World</title></head>");
//            out.println("<body>");
//            out.println("<h1>Hello, world!</h1>");  // says Hello
//            // Echo client's request information
//            out.println("<p>Request URI: " + request.getRequestURI() + "</p>");
//            out.println("<p>Protocol: " + request.getProtocol() + "</p>");
//            out.println("<p>PathInfo: " + request.getPathInfo() + "</p>");
//            out.println("<p>Remote Address: " + request.getRemoteAddr() + "</p>");
//            // Generate a random number upon each request
//            out.println("<p>A Random Number: <strong> Ahoooooooooooj" + Math.random() + "</strong></p>");
//            out.println("</body>");
//            out.println("</html>");
//        } finally {
//            out.close();  // Always close the output writer
//        }
//
////        return json;
    }
}
