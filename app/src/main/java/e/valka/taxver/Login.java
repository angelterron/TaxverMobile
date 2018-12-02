package e.valka.taxver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import e.valka.taxver.Fragments.ConductorFragment;
import e.valka.taxver.Interfaces.OnDownloadFinishedListener;
import e.valka.taxver.Models.Conductor;
import e.valka.taxver.Models.Usuarios;
import e.valka.taxver.Utils.DownloadAsyncTask;
import e.valka.taxver.Utils.URLS;

public class Login extends AppCompatActivity {
    public static final int PERMISSIONS = 1001;
    public static final String MY_PREFERENCES = "myPref";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "pass";
    public static final String KEY_DEVICEID = "deviceid";

    SharedPreferences sharedPreferences;
    String deviceID = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button registrar = findViewById(R.id.registrarse);
        Button login = findViewById(R.id.iniciarsesion);
        TextView email = findViewById(R.id.editText);
        TextView pass = findViewById(R.id.editText2);
        String emailS, passwordS,device;

        registrar.setOnClickListener((v)->{

            Intent registrarse = new Intent(this,register.class);
            startActivity(registrarse);
        });
        int a = ContextCompat.checkSelfPermission (this, Manifest.permission.READ_PHONE_STATE);
        int b = ContextCompat.checkSelfPermission (this, Manifest.permission.ACCESS_WIFI_STATE);

        if ( a != PackageManager.PERMISSION_GRANTED || b != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions (this,
                    new String [] {Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_WIFI_STATE},
                    PERMISSIONS);
        }
        sharedPreferences = getSharedPreferences (MY_PREFERENCES, Context.MODE_PRIVATE);
        if (sharedPreferences.contains (KEY_EMAIL)) {
            if(sharedPreferences.contains(KEY_PASSWORD)){
                if(sharedPreferences.contains(KEY_DEVICEID)){
                    emailS = sharedPreferences.getString(KEY_EMAIL,"");
                    passwordS = sharedPreferences.getString(KEY_PASSWORD,"");
                    device = sharedPreferences.getString(KEY_DEVICEID,"");
                    new DownloadAsyncTask (s ->{
                        Usuarios usuario = parseJSON(s);
                        if(usuario != null){
                            Intent iniciarsesion = new Intent(getBaseContext(),navigationActivity.class);
                            iniciarsesion.putExtra("usuario",usuario);
                            startActivity(iniciarsesion);
                            }
                    }).execute (URLS.LoginApp+"?Correo="+emailS+"&password="+passwordS+"&phoneID="+device);
                }
            }
        }
        saveToken();

        login.setOnClickListener((v)->{
            new DownloadAsyncTask (s ->{
                Usuarios usuario = parseJSON(s);
                if(usuario != null){
                    SharedPreferences.Editor editor = sharedPreferences.edit ();
                    editor.putString(KEY_EMAIL,usuario.Nombre);
                    editor.putString(KEY_PASSWORD,pass.getText().toString());
                    editor.putString(KEY_DEVICEID,usuario.PhoneId);
                    editor.apply();
                    Intent iniciarsesion = new Intent(getBaseContext(),navigationActivity.class);
                    iniciarsesion.putExtra("usuario",usuario);
                    startActivity(iniciarsesion);
                }else{
                    new DownloadAsyncTask (j ->{
                        Conductor con = parseJSONConductor(j);
                        if(con != null){
                            Intent conductorpass = new Intent(getBaseContext(),conductor_password.class);
                            conductorpass.putExtra("conductor",con);
                            conductorpass.putExtra("phoneID",deviceID);
                            startActivity(conductorpass);
                        }else{
                            Toast.makeText (getBaseContext (), "¡Error al iniciar sesión!", Toast.LENGTH_LONG).show ();
                        }
                    }).execute (URLS.Conductor+"?correo="+email.getText());
                }
            }).execute (URLS.LoginApp+"?Correo="+email.getText()+"&password="+pass.getText()+"&phoneID="+deviceID);
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS:
                if (grantResults [0] == PackageManager.PERMISSION_GRANTED && grantResults [1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText (this, "Required permissions granted!", Toast.LENGTH_SHORT).show ();
                    saveToken ();
                }
        }
    }
    @SuppressLint("HardwareIds")
    private void saveToken () {

        if (ContextCompat.checkSelfPermission (this, Manifest.permission.READ_PHONE_STATE) == PermissionChecker.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission (this, Manifest.permission.ACCESS_WIFI_STATE) == PermissionChecker.PERMISSION_GRANTED) {

            TelephonyManager telephonyManager = (TelephonyManager) getSystemService (TELEPHONY_SERVICE);
            deviceID = telephonyManager != null ?  telephonyManager.getDeviceId () : null;

            if (deviceID == null) {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService (WIFI_SERVICE);
                deviceID = wifiManager != null ? wifiManager.getConnectionInfo ().getMacAddress () : null;
            }
        }

        if (deviceID == null) {
            Log.i (navigationActivity.TAG, "NULO");
            return;
        }
    }
    private Usuarios parseJSON (String json) {
        Usuarios usuario = new Gson().fromJson (json, Usuarios.class);
        if (usuario == null) return null;
        return usuario;
    }
    private Conductor parseJSONConductor (String json) {
        Conductor conductor = new Gson().fromJson (json, Conductor.class);
        if (conductor == null) return null;
        return conductor;
    }
}
