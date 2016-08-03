package buyhatke.assignmentsmsapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;

/**
 * Created by Chandrakant on 02-08-2016.
 */
public class ReceiveSMS extends BroadcastReceiver {
    public static final String SMS_BUNDLE = "pdus";
    Intent broadcastIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();

                smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += smsBody + "\n";
            }

            Intent notiIntent = new Intent(context,MainActivity.class);
            notiIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notiIntent,
                    PendingIntent.FLAG_ONE_SHOT);
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(icon)
                    .setContentText(smsMessageStr)
                    .setContentTitle("New SMS")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(smsMessageStr))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, notificationBuilder.build());
            MainActivity a = MainActivity.getInstance();
            if(a!=null) {
                a.refreshList();
                a=null;
            }

        }
    }
}
