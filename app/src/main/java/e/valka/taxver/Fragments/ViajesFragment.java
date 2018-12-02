package e.valka.taxver.Fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

import e.valka.taxver.Models.Conductor;
import e.valka.taxver.Models.ConductorHeader;
import e.valka.taxver.Models.Posicionconductor;
import e.valka.taxver.Models.Posiciones;
import e.valka.taxver.Models.Viaje;
import e.valka.taxver.Models.Viajes;
import e.valka.taxver.R;
import e.valka.taxver.Utils.CircleTransform;
import e.valka.taxver.Utils.DownloadAsyncTask;
import e.valka.taxver.Utils.URLS;
import e.valka.taxver.navigationActivity;

public class ViajesFragment extends android.support.v4.app.Fragment {
    ArrayList<Viaje> viajesFragment = new ArrayList<>();
    RecyclerView recyclerView;
    public int idPersona;
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_base, container, false);
        recyclerView = v.findViewById (R.id.recycler);
        recyclerView.setLayoutManager (new LinearLayoutManager(getActivity ()));
        new DownloadAsyncTask(this::parseJSON).execute (URLS.Viajes+"?idPersona="+idPersona);
        return v;
    }

    private void parseJSON (String json) {
        Viajes viajes = new Gson().fromJson (json, Viajes.class);
        if (viajes == null) return;
        viajesFragment = viajes.Viajes;
        recyclerView.setAdapter(new viajesAdapter(viajesFragment));

    }
    class viajesAdapter extends RecyclerView.Adapter<viajesAdapter.viajeViewHolder> {

        private ArrayList<Viaje> data;

        viajesAdapter (ArrayList<Viaje> d) {
            data = d;
        }

        @NonNull
        @Override
        public viajeViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from (parent.getContext ());
            View view = inflater.inflate (R.layout.viajes_item, parent, false);
            return new viajeViewHolder(view);
        }

        @Override
        public void onBindViewHolder (@NonNull viajeViewHolder holder, int position) {
            Viaje viaje = data.get(position);
            String conductor = viaje.IdConductorNavigation.IdPersonaNavigation.Nombre + viaje.IdConductorNavigation.IdPersonaNavigation.ApellidoPaterno + viaje.IdConductorNavigation.IdPersonaNavigation.ApellidoMaterno;
            String distancia = viaje.Kilometros+"Kms";
            String tarifa = "$"+viaje.Tarifa;
            holder.setData (conductor,distancia,tarifa);
        }

        @Override
        public int getItemCount () {
            return data.size ();
        }

        class viajeViewHolder extends RecyclerView.ViewHolder {
            int id;
            TextView texto,texto2,texto3;

            viajeViewHolder (View itemView) {
                super (itemView);
                texto = itemView.findViewById(R.id.conductor);
                texto2 = itemView.findViewById(R.id.distancia);
                texto3 = itemView.findViewById(R.id.tarifa);
            }

            void setData (String data1,String data2,String data3) {
                texto.setText(data1);
                texto2.setText(data2);
                texto3.setText(data3);

            }
        }
    }
}
