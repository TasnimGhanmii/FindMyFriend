package tasnimghanmi.grp1.findmyfriend;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MysmsReceiver extends BroadcastReceiver {

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!"android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) return;

        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null) return;

        SmsMessage[] messages = new SmsMessage[pdus.length];
        for (int i = 0; i < pdus.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        }

        if (messages.length == 0) return;

        String messageBody = messages[0].getMessageBody();
        String phoneNumber = messages[0].getDisplayOriginatingAddress();

        // Trigger location request
        if (messageBody.contains("FindMyFriend: envoyer moi votre position!")) {
            Intent i = new Intent(context, MyGeoService.class);
            i.putExtra("sender", phoneNumber);
            context.startService(i);
        }

        // Receive position
        if (messageBody.startsWith("FindMyFriend: ma position est")) {
            String[] parts = messageBody.split("#");
            if (parts.length < 4) return;

            String longitude = parts[1].trim();
            String latitude = parts[2].trim();
            String pseudo = parts[3].trim();
            String numero = phoneNumber;

            sendPositionToServer(pseudo, numero, longitude, latitude);

            // Show notification
            NotificationCompat.Builder mynotif = new NotificationCompat.Builder(context, "FindMyFriend_channel_ID")
                    .setContentTitle("Position Reçue: " + pseudo)
                    .setContentText("Appuyer pour voir")
                    .setSmallIcon(android.R.drawable.ic_dialog_map)
                    .setAutoCancel(true);

            Intent mapIntent = new Intent(context, MapsActivity.class);
            mapIntent.putExtra("longitude", longitude);
            mapIntent.putExtra("latitude", latitude);

            PendingIntent pi = PendingIntent.getActivity(context, 0, mapIntent, PendingIntent.FLAG_MUTABLE);
            mynotif.setContentIntent(pi);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            NotificationChannel canal = new NotificationChannel(
                    "FindMyFriend_channel_ID",
                    "Canal pour FindMyFriend",
                    android.app.NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(canal);
            notificationManager.notify(0, mynotif.build());
        }
    }

    private void sendPositionToServer(String pseudo, String numero, String longitude, String latitude) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL("http://10.0.2.2/servicephp/add_position.php"); // Emulator -> localhost
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

                String postData = "pseudo=" + URLEncoder.encode(pseudo, "UTF-8") +
                        "&numero=" + URLEncoder.encode(numero, "UTF-8") +
                        "&longitude=" + URLEncoder.encode(longitude, "UTF-8") +
                        "&latitude=" + URLEncoder.encode(latitude, "UTF-8");

                Log.d("MysmsReceiver", "POST data: " + postData);

                byte[] postDataBytes = postData.getBytes("UTF-8");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

                OutputStream os = conn.getOutputStream();
                os.write(postDataBytes);
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d("MysmsReceiver", "Server response code: " + responseCode);

                java.io.InputStream is = (responseCode >= 200 && responseCode < 400)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                String response = s.hasNext() ? s.next() : "";
                Log.d("MysmsReceiver", "Server response: " + response);

            } catch (Exception e) {
                Log.e("MysmsReceiver", "Error sending position", e);
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}
