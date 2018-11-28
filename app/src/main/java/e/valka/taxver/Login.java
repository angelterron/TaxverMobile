package e.valka.taxver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button registrar = findViewById(R.id.registrarse);
        Button login = findViewById(R.id.iniciarsesion);
        TextView email = findViewById(R.id.editText);
        TextView pass = findViewById(R.id.editText2);
        registrar.setOnClickListener((v)->{

            Intent registrarse = new Intent(this,register.class);
            startActivity(registrarse);
        });
        login.setOnClickListener((v)->{
            new DownloadAsyncTask (s ->{
                Usuarios usuario = parseJSON(s);
                if(usuario != null){
                    Intent iniciarsesion = new Intent(getBaseContext(),navigationActivity.class);
                    iniciarsesion.putExtra("usuario",usuario);
                    startActivity(iniciarsesion);
                }else{
                    new DownloadAsyncTask (j ->{
                        Conductor con = parseJSONConductor(j);
                        if(con != null){
                            Intent conductorpass = new Intent(getBaseContext(),conductor_password.class);
                            conductorpass.putExtra("conductor",con);
                            startActivity(conductorpass);
                        }else{
                            Toast.makeText (getBaseContext (), "¡Error al iniciar sesión!", Toast.LENGTH_LONG).show ();
                        }
                    }).execute (URLS.Conductor+"?correo="+email.getText());
                }
            }).execute (URLS.LoginApp+"?Correo="+email.getText()+"&password="+pass.getText());
        });
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
