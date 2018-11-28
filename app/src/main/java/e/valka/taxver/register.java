package e.valka.taxver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import e.valka.taxver.Models.Persona;
import e.valka.taxver.Models.Usuarios;
import e.valka.taxver.Utils.DownloadAsyncTask;
import e.valka.taxver.Utils.URLS;

public class register extends AppCompatActivity {
    TextView nombre,apellidos,telefono,correo,pass1,pass2,dia,mes,anio;
    Button registrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nombre = findViewById(R.id.editText);
        apellidos = findViewById(R.id.editText2);
        telefono = findViewById(R.id.editText3);
        correo = findViewById(R.id.editText5);
        pass1 = findViewById(R.id.editText6);
        pass2 = findViewById(R.id.editText7);
        dia = findViewById(R.id.dia);
        mes = findViewById(R.id.mes);
        anio = findViewById(R.id.anio);
        registrar = findViewById(R.id.registrarse);

        registrar.setOnClickListener((v)->{
            if(!pass1.getText().toString().isEmpty() && !pass2.getText().toString().isEmpty()){
                if(pass1.getText().toString().equals(pass2.getText().toString())){
                    int diai, mesi, anioi;
                    diai = Integer.parseInt(dia.getText().toString());
                    mesi = Integer.parseInt(mes.getText().toString());
                    anioi = Integer.parseInt(anio.getText().toString());
                    String apellidoss[] = apellidos.getText().toString().split(" ");
                    Persona per = new Persona();
                    per.Nombre = nombre.getText().toString();
                    per.ApellidoPaterno = apellidoss[0];
                    if(apellidoss.length > 1)
                        per.ApellidoMaterno = apellidoss[1];
                    per.Telefono = telefono.getText().toString();
                    per.FechaNacimiento = anioi+"-"+mesi+"-"+diai;
                    per.Nombre = nombre.getText().toString();
                    Usuarios usuario = new Usuarios();
                    usuario.Nombre = correo.getText().toString();
                    usuario.Password = pass1.getText().toString();
                    usuario.IdPersonaNavigation = per;
                    sendPost(usuario);
                }else{
                    Toast.makeText (getBaseContext (), "¡Contraseñas diferentes!", Toast.LENGTH_LONG).show ();
                }
            }else{
                Toast.makeText (getBaseContext (), "¡Escribre tu contraseña!", Toast.LENGTH_LONG).show ();
            }
        });


    }
    public void sendPost(Usuarios usuario) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(URLS.CreateCliente);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    JSONObject jsonParam = new JSONObject(new Gson().toJson(usuario));

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                    new DownloadAsyncTask(s ->{
                        Usuarios usuario = parseJSON(s);
                        if(usuario != null){
                            Intent iniciarsesion = new Intent(getBaseContext(),navigationActivity.class);
                            iniciarsesion.putExtra("usuario",usuario);
                            startActivity(iniciarsesion);
                        }}).execute (URLS.LoginApp+"?Correo="+usuario.Nombre+"&password="+usuario.Password);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
    private Usuarios parseJSON (String json) {
        Usuarios usuario = new Gson().fromJson (json, Usuarios.class);
        if (usuario == null) return null;
        return usuario;
    }
}
