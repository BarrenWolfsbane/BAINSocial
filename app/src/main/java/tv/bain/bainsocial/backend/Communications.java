package tv.bain.bainsocial.backend;

import android.content.Context;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;



public class Communications {
    private Context context;
    public Communications(Context c) {
        context = c;
    }
    private ServerSocket serverSocket;

    private static int[] CommonlyBlockedPorts = {
            0,      // is a reserved port, which means it should not be used by applications. Network abuse has prompted the need to block this port.	IPv4/IPv6
            25,     //TCP	SMTP	Both	Port 25 is unsecured, and Botnet spammers can use it to send spam. This does not affect Xfinity Connect usage. We recommend learning more about configuring your email settings to Comcast email to use port 587.	IPv4/IPv6
            67,     //UDP	BOOTP, DHCP	Downstream	UDP Port 67, which is used to obtain dynamic Internet Protocol (IP) address information from our dynamic host configuration protocol (DHCP) server, is vulnerable to malicious hacks.	IPv4
            135,
            136,
            137,
            138,
            139,    //TCP/UDP	NetBios	Both	NetBios services allow file sharing over networks. When improperly configured, ports 135-139 can expose critical system files or give full file system access (run, delete, copy) to any malicious intruder connected to the network.	IPv4/IPv6
            161,    //UDP	SNMP	Both	SNMP is vulnerable to reflected amplification distributed denial of service (DDoS) attacks.	IPv4/IPv6
            445,    //TCP	MS-DS, SMB	Both	Port 445 is vulnerable to attacks, exploits and malware such as the Sasser and Nimda worms.	IPv4/IPv6
            520,    //UDP	RIP	Both	Port 520 is vulnerable to malicious route updates, which provides several attack possibilities.	IPv4
            547,    //UDP	DHCPv6	Downstream	UDP Port 547, which is used to obtain dynamic Internet Protocol (IP) address information from our dynamic host configuration protocol (DHCP) server, is vulnerable to malicious hacks.	IPv6
            1080,   //TCP	SOCKS	Downstream	Port 1080 is vulnerable to, among others, viruses, worms and DoS attacks.	IPv4/IPv6
            1900    //UDP	SSDP	Both	Port 1900 is vulnerable to DoS attacks.	IPv4/IPv6};
    };
    public static boolean portChecker(final String ip, final int port, final int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            socket.close();
            return true;
        }
        catch(ConnectException ce){
            ce.printStackTrace();
            return false;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    /*
    public static JSONObject obj2JSONObj(Object obj){

        JSONArray arr = new JSONObject(result).getJSONArray("objects");
    }
    public static Object JSONObj2Obj(Object obj){

        JSONArray arr = new JSONObject(result).getJSONArray("objects");
    }*/
}
