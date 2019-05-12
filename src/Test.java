import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.UserIdentity;
import database.UserRecordConnector;

import java.util.Arrays;

public class Test {
    public static void main(String args[]){

        ByteArray userHandle = UserRecordConnector.generatUserHandle();

        System.out.println("ahoj");
//        byte[] originByteArray;
//        byte[] AliceUserBytes = null;
//        {
//            try {
//                originByteArray = "This Is alice in wonderland and I hope this text is long enough".getBytes("UTF-8");
//                AliceUserBytes = Arrays.copyOfRange(originByteArray, 0, 12);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        ByteArray aliceIdBytes = new ByteArray(AliceUserBytes);
//
//        PublicKeyCredentialCreationOptions pkrequest = RpInstance.rp.startRegistration(StartRegistrationOptions.builder()
//                .user(UserIdentity.builder()
//                        .name("alice@example.com")
//                        .displayName("Alice Hypothetical")
//                        .id(aliceIdBytes)
//                        .build())
//                .build());
//
//        ObjectMapper jsonMapper = new ObjectMapper()
//                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
//                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
//                .registerModule(new Jdk8Module());
//        System.out.println("hmmmm");
    }


}
