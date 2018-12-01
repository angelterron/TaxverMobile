package e.valka.taxver;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import e.valka.taxver.Fragments.ConductorFragment;
import e.valka.taxver.R;

public class Conductores extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conductores);
        ConductorFragment conductorAdapter = new ConductorFragment();

    }
}
