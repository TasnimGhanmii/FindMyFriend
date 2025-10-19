package tasnimghanmi.grp1.findmyfriend;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import tasnimghanmi.grp1.findmyfriend.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    Button btn_sms,btn_ecrire;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_sms=findViewById(R.id.btn_sms);
        btn_ecrire=findViewById(R.id.btn_sdcard);

        btn_sms.setOnClickListener(v->{
            if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED)
            {
                sensSMS("27028755","bonjour");
            }
            else
            {   //demander permission
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.SEND_SMS},1);
            }
        });

    }

    public void sensSMS(String numero,String msg)
    {
        //envoie msg depuis carte sim1
        SmsManager manager=SmsManager.getDefault();
        manager.sendTextMessage(numero,null,msg,null,null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
        if(requestCode==1)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "permission accordée", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "permission refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }
}