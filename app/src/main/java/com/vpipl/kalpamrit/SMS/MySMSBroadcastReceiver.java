package com.vpipl.kalpamrit.SMS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.vpipl.kalpamrit.Utils.SmsListener;


/**
 * Created by shree on 1/3/2019.
 */

public class MySMSBroadcastReceiver extends BroadcastReceiver  {

    private static SmsListener mListener;

    final SmsManager sms = SmsManager.getDefault();

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch(status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                 //   String otp = extraSMSCode.get(message);
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server.
                    if (message.contains("Z8rLEgBDjBc")) {
                        String otp = message.substring(4, 10).trim();
                    //    Toast.makeText(context, otp , Toast.LENGTH_SHORT).show();
                        mListener.messageReceived(otp);
                      }

                 //   Toast.makeText(context, message , Toast.LENGTH_SHORT).show();
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
               //     Toast.makeText(context, "TIMEOUT" , Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    }
}