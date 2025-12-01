package tasnimghanmi.grp1.findmyfriend;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.telephony.SmsManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Random;

public class MyGeoService extends Service {

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String sender = intent.getStringExtra("sender");
        String pseudo = generateRandomPseudo();

        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();

                    SmsManager manager = SmsManager.getDefault();
                    String message = "FindMyFriend: ma position est#" + longitude + "#" + latitude + "#" + pseudo;
                    manager.sendTextMessage(sender, null, message, null, null);
                }
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    private String generateRandomPseudo() {
        String[] names = {"Alpha","Bravo","Charlie","Delta","Echo","Foxtrot","Golf","Hotel","India","Juliet"};
        Random random = new Random();
        int idx = random.nextInt(names.length);
        return names[idx] + random.nextInt(1000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
