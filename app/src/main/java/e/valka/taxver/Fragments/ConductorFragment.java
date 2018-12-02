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
import e.valka.taxver.R;
import e.valka.taxver.Utils.CircleTransform;
import e.valka.taxver.Utils.DownloadAsyncTask;
import e.valka.taxver.Utils.URLS;
import e.valka.taxver.navigationActivity;

public class ConductorFragment extends DialogFragment {
    ArrayList<Posicionconductor> posicionesFragment = new ArrayList<>();
    RecyclerView recyclerView;
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_base, container, false);
        recyclerView = v.findViewById (R.id.recycler);
        recyclerView.setLayoutManager (new LinearLayoutManager(getActivity ()));
        new DownloadAsyncTask(this::parseJSON).execute (URLS.Conductores);
        return v;
    }

    private void parseJSON (String json) {
        Posiciones posiciones = new Gson().fromJson (json, Posiciones.class);
        if (posiciones == null) return;
        posicionesFragment = posiciones.Posiciones;
        recyclerView.setAdapter(new conductoresAdapter(posicionesFragment));

    }
    class conductoresAdapter extends RecyclerView.Adapter<conductoresAdapter.conductorViewHolder> {

        private ArrayList<Posicionconductor> data;

        conductoresAdapter (ArrayList<Posicionconductor> d) {
            data = d;
        }

        @NonNull
        @Override
        public conductorViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from (parent.getContext ());
            View view = inflater.inflate (R.layout.conductor_item, parent, false);

            conductorViewHolder con = new conductorViewHolder(view);
            view.setOnClickListener((v)->{
                navigationActivity.mainActivity.pedir(con.id);
                getDialog().dismiss();
            });

            return con;
        }

        @Override
        public void onBindViewHolder (@NonNull conductorViewHolder holder, int position) {
            Posicionconductor posicionconductor = data.get (position);
            String Nombre = posicionconductor.IdConductorNavigation.IdPersonaNavigation.Nombre+" "+ posicionconductor.IdConductorNavigation.IdPersonaNavigation.ApellidoPaterno + " "+ posicionconductor.IdConductorNavigation.IdPersonaNavigation.ApellidoMaterno;
            String Placa = posicionconductor.IdConductorNavigation.IdVehiculoNavigation.Placa;
            String Vehiculo = posicionconductor.IdConductorNavigation.IdVehiculoNavigation.Marca +" "+ posicionconductor.IdConductorNavigation.IdVehiculoNavigation.Modelo + " "+posicionconductor.IdConductorNavigation.IdVehiculoNavigation.Numero;
            holder.setData (Nombre,Placa,Vehiculo,posicionconductor.IdConductorNavigation.Foto,posicionconductor.IdConductor);
        }

        @Override
        public int getItemCount () {
            return data.size ();
        }

        class conductorViewHolder extends RecyclerView.ViewHolder {
            int id;
            TextView texto,texto2,texto3;
            ImageView foto;

            conductorViewHolder (View itemView) {
                super (itemView);
                texto = itemView.findViewById(R.id.Nombre);
                texto2 = itemView.findViewById(R.id.Placas);
                texto3 = itemView.findViewById(R.id.Vehiculo);
                foto = itemView.findViewById(R.id.foto);
            }

            void setData (String data1,String data2,String data3,String data4, int id) {
                this.id = id;
                texto.setText(data1);
                texto2.setText(data2);
                texto3.setText(data3);
                Picasso.get().load(URLS.servidor+data4).transform( new CircleTransform()).fit().into(foto);

            }
        }
    }
}
