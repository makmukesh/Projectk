package com.vpipl.kalpamrit.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

/**
 * Created by admin on 09-06-2017.
 */

public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;

    final SmsManager sms = SmsManager.getDefault();

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        String action = intent.getAction();

        if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            try {
                if (data != null) {
                    Object[] pdus = (Object[]) data.get("pdus");

                    for (int i = 0; i < 1; i++) {

                        try {
                            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

                            String sender = smsMessage.getDisplayOriginatingAddress();
                            String messageBody = smsMessage.getMessageBody();

                            if (sender.contains("KALPMK")) {
                                String otp = messageBody.substring(0, 6).trim();
                                mListener.messageReceived(otp);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
