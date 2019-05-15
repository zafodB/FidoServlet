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

public class Test {
    public static void main(String args[]) {

        String response = "{\"id\":\"j1hEqK4aMIpkmVb9Itv7ayqXPhyV9Tm7ed9JnNluwPSGt_EZEpGCd28DWw2DWtRaGp2Tc4CUhoFJirmsZaiJ-Q\",\"type\":\"public-key\",\"clientExtensionResults\":{},\"response\":{\"clientDataJSON\":\"eyJjaGFsbGVuZ2UiOiJTbEJpZVZBMFVteE5NbUZCY21Sb2JISmZVR3B2Ulc5dWNUSkdVVmhQVERGbFFqZEtTM1ZCU0ZKUE1BIiwibmV3X2tleXNfbWF5X2JlX2FkZGVkX2hlcmUiOiJkbyBub3QgY29tcGFyZSBjbGllbnREYXRhSlNPTiBhZ2FpbnN0IGEgdGVtcGxhdGUuIFNlZSBodHRwczovL2dvby5nbC95YWJQZXgiLCJvcmlnaW4iOiJodHRwczovL2ZpZG9zZXJ2ZXIubWwiLCJ0eXBlIjoid2ViYXV0aG4uZ2V0In0=\",\"authenticatorData\":\"Fc99lLbDqu9PZCRRSPTfIU0UcDHaXHYDzfmWOJNL0DwBAAAAKQ==\",\"signature\":\"MEQCIHVksYm75jB7tnUNEdUPmAQgHlsIdp0LWUEdcfomW83aAiBdmzFD3N/hUPCCcu/aiS3HoJM8CZvqOweylARXx0B+5w==\"}}";

        String responseData = recodeSignature(response);
        responseData = recodeCredentialId(responseData);

//        TODO work here what happens when they sign.

        try {

            PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> pkc =
                    PublicKeyCredential.parseAssertionResponseJson(responseData);

            String req = "{\"publicKeyCredentialRequestOptions\":{\"challenge\":\"JPbyP4RlM2aArdhlr_PjoEonq2FQXOL1eB7JKuAHRO0\",\"rpId\":\"fidoserver.ml\",\"allowCredentials\":[{\"type\":\"public-key\",\"id\":\"ajFoRXFLNGFNSXBrbVZiOUl0djdheXFYUGh5VjlUbTdlZDlKbk5sdXdQU0d0X0VaRXBHQ2QyOERXdzJEV3RSYUdwMlRjNENVaG9GSmlybXNaYWlKLVE\"}],\"userVerification\":\"preferred\",\"extensions\":{}},\"username\":\"adam@adamovo.com\"}";

            ObjectMapper jsonMapper = new ObjectMapper()
                    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                    .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
                    .registerModule(new Jdk8Module());

            String recodedRequest = recodeChallenge(req);



            AssertionRequest request = jsonMapper.readValue(recodedRequest, AssertionRequest.class);

            AssertionResult result = RpInstance.rp.finishAssertion(FinishAssertionOptions.builder()
                    .request(request)
                    .response(pkc)
                    .build());

        } catch (Exception e){
            e.printStackTrace();
        }

        System.out.println();

    }


    private static String recodeChallenge(String input){
        int cutAtPosition = input.lastIndexOf("\":{\"challenge\":\"") + 16;
        int cutUntil = input.lastIndexOf("\",\"rpId\"");

        return RpInstance.recode(cutAtPosition, cutUntil, input);
    }

    private static String recodeSignature(String input){
        int cutAtPosition = input.lastIndexOf("\"signature\":\"") + 13;
        int cutUntil = input.length() - 3;

        return RpInstance.recode(cutAtPosition, cutUntil, input);
    }

    private static String recodeCredentialId(String input){
        int cutAtPosition = input.lastIndexOf("{\"id\":\"") + 7;
        int cutUntil = input.lastIndexOf("\",\"type\"");

        return RpInstance.recode(cutAtPosition, cutUntil, input);
    }
}
