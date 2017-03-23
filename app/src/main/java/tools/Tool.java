package tools;

import android.content.Context;
import android.net.ConnectivityManager;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HuongLX on 3/14/2017.
 */

public class Tool {
    public static boolean isInternetAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("")&&cm.getActiveNetworkInfo() != null;

        } catch (Exception e) {
            return false;
        }
    }
}
