package com.forthgreen.app.views.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.telephony.PhoneNumberUtils;

public class SmsSendObserver extends ContentObserver {
    public static final int NO_TIMEOUT = -1;

    private static final Handler handler = new Handler();
    private static final Uri uri = Uri.parse("content://sms/");

    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_TYPE = "type";
    private static final String[] PROJECTION = { COLUMN_ADDRESS, COLUMN_TYPE };
    private static final int MESSAGE_TYPE_SENT = 2;

    private Context context = null;
    private ContentResolver resolver = null;

    private String phoneNumber = null;
    private long timeout = NO_TIMEOUT;
    private boolean wasSent = false;
    private boolean timedOut = false;
    private SmsSendListener smsSendListener;

    public SmsSendObserver() {
        super(handler);
    }
    /* public SmsSendObserver(Context context, String phoneNumber, long timeout) {
        super(handler);

        if (context instanceof SmsSendListener) {
            this.context = context;
            this.resolver = context.getContentResolver();
            this.phoneNumber = phoneNumber;
            this.timeout = timeout;
        }
        else {
            throw new IllegalArgumentException(
                    "Context must implement SmsSendListener interface");
        }
    }*/

    public SmsSendObserver(Context context, String phoneNumber, long timeout) {
        super(handler);
        this.resolver = context.getContentResolver();
        this.phoneNumber = phoneNumber;
        this.timeout = timeout;
    }

    public void setListener(SmsSendListener smsSendListener) {
        this.smsSendListener = smsSendListener;
    }

    private Runnable runOut = new Runnable() {
        @Override
        public void run() {
            if (!wasSent) {
                timedOut = true;
                callBack();
            }
        }
    };

    public void start() {
        if (resolver != null) {
            resolver.registerContentObserver(uri, true, this);

            if (timeout > NO_TIMEOUT) {
                handler.postDelayed(runOut, timeout);
            }
        }
        else {
            throw new IllegalStateException(
                    "Current SmsSendObserver instance is invalid");
        }
    }

    public void stop() {
        if (resolver != null) {
            resolver.unregisterContentObserver(this);

            resolver = null;
            context = null;
        }
    }

    private void callBack() {
      //  ((SmsSendListener) context).onSmsSendEvent(wasSent);
        smsSendListener.onSmsSendEvent(wasSent);
        stop();
    }

    @Override
    public void onChange(boolean selfChange) {
        if (wasSent || timedOut)
            return;

        Cursor cursor = null;

        try {
            cursor = resolver.query(uri, PROJECTION, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                final String address =
                        cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS));
                final int type =
                        cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE));

                if (PhoneNumberUtils.compare(address, phoneNumber) &&
                        type == MESSAGE_TYPE_SENT) {

                    wasSent = true;
                    callBack();
                }
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public interface SmsSendListener {
        // Passes true if the message was sent
        // Passes false if timed out
        public void onSmsSendEvent(boolean sent);
    }
}
