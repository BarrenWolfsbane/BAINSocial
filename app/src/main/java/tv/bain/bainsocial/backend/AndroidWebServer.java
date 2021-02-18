package tv.bain.bainsocial.backend;

/*
This can be used for creation of http based Documentation sites As well as serve up files

 */


import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

import static tv.bain.bainsocial.utils.Utils.getIPAddress;
import static tv.bain.bainsocial.utils.Utils.getPublicIPAddress;

public class AndroidWebServer extends NanoHTTPD {

    public AndroidWebServer(int port) {
        super(port);
    }

    public AndroidWebServer(String hostname, int port) {
        super(hostname, port);
    }
    @Override
    public Response serve(IHTTPSession session) {
        String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null) {
            msg += "Current Pub IP: "+getPublicIPAddress()+"\n";
            msg += "IPv4: "+getIPAddress(true)+"\n";
            msg += "IPv6: "+getIPAddress(false)+"\n";

            msg += "<form action='?' method='get'>\n";
            msg += "<p>Your name: <input type='text' name='username'></p>\n";
            msg += "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }
        return newFixedLengthResponse( msg + "</body></html>\n" );
    }
}
