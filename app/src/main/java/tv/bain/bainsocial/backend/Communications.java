package tv.bain.bainsocial.backend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import tv.bain.bainsocial.utils.Utils;

import static tv.bain.bainsocial.utils.Utils.getPublicIPAddress;


public class Communications extends BroadcastReceiver {
    private int port = 6666;
    public void setPort(int port){ this.port = port; }
    public int getPort(){ return port; }

    private Context context;
    public Communications(Context c) {
        context = c;
    }

    private ServerSocket serverSocket;

    public String yourIP(){
        //TODO: We will create a function to send back the connected users public IP address to them
        //To help replace the getIP address dependency above
        return "";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
            if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
                //do stuff
                /*
                Check the IP against the one currently listed in the directory for you, Do this for ipv4 and ipv6
                if there was a change update the Directory and send out a packet of data to each user you know
                 */
                AddressUpdate();
            } else {
                // wifi connection was lost
            }
        }
    }
    public void AddressUpdate(){
        String IP2Use = "";
        String AddressIPv4 = Utils.getIPAddress(true);
        String AddressIPv6 = Utils.getIPAddress(false);

        if(!AddressIPv6.equals("")) IP2Use = AddressIPv4;
        else IP2Use = AddressIPv6;

        IP2Use = getPublicIPAddress();
        //using IP2Use

        BAINServer.getInstance().getDb().directory_Insert(
                BAINServer.getInstance().getUser().getuID(),
                IP2Use, Integer.toString(port));
    }
    public boolean isConnectedViaWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    public Communications(){}

    private static final int[] CommonlyBlockedPorts = {
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
}

