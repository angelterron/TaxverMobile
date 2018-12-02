package e.valka.taxver;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import e.valka.taxver.Fragments.ObjetosFragment;

public class objetos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objetos);
        ObjetosFragment objetos =new ObjetosFragment();

        objetos.idPersona = getIntent().getIntExtra("IDPERSONA",0);
        getSupportFragmentManager()
                .beginTransaction ()
                .replace (R.id.container, objetos)
                .setTransition (FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit ();
    }
}
