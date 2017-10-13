package com.saddacampus.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.saddacampus.app.activity.SubmitOtpActivity;
import com.saddacampus.app.app.Config.Config;

/**
 * Created by Devesh Anand on 22-05-2017.
 */

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    SubmitOtpActivity submitOtpActivity = null;

    public void setMainActivityHandler(SubmitOtpActivity submitOtpActivity){
        this.submitOtpActivity = submitOtpActivity;
    }



    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "SmsReceiver onReceive");

        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                //Object[] pdusObj = (Object[]) bundle.get("pdus");
                SmsMessage currentMessage[] = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                for (SmsMessage acurrentMessage : currentMessage) {

                    //SmsMessage currentMessage = SmsMessage.createFromPdu((byte[])aPdusObj);
                    String senderAddress = acurrentMessage.getDisplayOriginatingAddress();
                    String message = acurrentMessage.getDisplayMessageBody();

                    Log.e(TAG, "Received SMS: " + message + ", Sender: " + senderAddress);

                    // if the SMS is not from our gateway, ignore the message
                    if (!senderAddress.toLowerCase().contains(Config.SMS_ORIGIN.toLowerCase())) {
                        return;
                    }

                    // verification code from sms
                    String verificationCode = getVerificationCode(message);

                    Log.e(TAG, "OTP received: " + verificationCode);
                    //SignUpOtpActivity otp = new SignUpOtpActivity();
                    submitOtpActivity.receivedSms(verificationCode);
                    submitOtpActivity.verifyOtpButton.performClick();

                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf(Config.OTP_DELIMITER);

        if (index != -1) {
            int start = index + 2;
            int length = 6;
            code = message.substring(start, start + length);
            return code;
        }

        return code;
    }
}

