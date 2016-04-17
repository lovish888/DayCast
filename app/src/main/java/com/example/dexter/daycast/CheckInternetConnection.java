package com.example.dexter.daycast;

/**
 * Created by dexter on 4/17/16.
 */
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

/**
 * CheckInternetConnection Class is used to check Internet connection
 *
 */
public class CheckInternetConnection {

    private Context context;

    public CheckInternetConnection(Context mContext) {
        context = mContext;
    }

    /**
     * @return isConnectedToInternet?(), is used to check Internet Connectivity by using Connection Manager.
     * it will return true if it is connected and false if not connected
     */
    public boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void showDialog() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        AlertDialog alertDialog;
        alertDialogBuilder.setTitle("No Internet Connectivity Found");
        alertDialogBuilder
                .setMessage("Move to internet connection settings?")
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        context.startActivity(intent);
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}