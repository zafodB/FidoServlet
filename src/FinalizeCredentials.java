import com.yubico.webauthn.data.AuthenticatorResponse;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FinalizeCredentials extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        UserDirectoryConnector.addRecord("test1", "test2", "test3");


        resp.getWriter().println("something happened lol");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
//        super.doPost(req, resp);

        String data = req.getParameter("data");
        String session = req.getParameter("session");

        resp.setContentType("application/json");
        resp.getWriter().println(data);
        resp.getWriter().println();
        resp.getWriter().println(session);

        AuthenticatorResponse as;

    }



}
