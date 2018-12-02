package e.valka.taxver;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import e.valka.taxver.Fragments.ViajesFragment;

public class Viajes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viajes);
        ViajesFragment viajes = new ViajesFragment();

        viajes.idPersona = getIntent().getIntExtra("IDPERSONA",0);
        getSupportFragmentManager()
                .beginTransaction ()
                .replace (R.id.container, viajes)
                .setTransition (FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit ();
    }
}
