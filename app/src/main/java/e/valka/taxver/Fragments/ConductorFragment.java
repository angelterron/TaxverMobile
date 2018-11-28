package e.valka.taxver.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;

import e.valka.taxver.Models.Conductor;
import e.valka.taxver.Models.ConductorHeader;
import e.valka.taxver.R;
import e.valka.taxver.Utils.DownloadAsyncTask;
import e.valka.taxver.Utils.URLS;

public class ConductorFragment extends Fragment {
    ArrayList<Conductor> conductorList = new ArrayList<>();
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
        Conductor[] conductores = new Gson().fromJson (json, Conductor[].class);
        if (conductores == null) return;
        for(int i = 0; i < conductores.length ; i++){
            conductorList.add(conductores[i]);
        }
        recyclerView.setAdapter(new conductoresAdapter(conductorList));

    }
    class conductoresAdapter extends RecyclerView.Adapter<conductoresAdapter.conductorViewHolder> {

        private ArrayList<Conductor> data;

        conductoresAdapter (ArrayList<Conductor> d) {
            data = d;
        }

        @NonNull
        @Override
        public conductorViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from (parent.getContext ());
            View view = inflater.inflate (R.layout.conductor_item, parent, false);

            return new conductorViewHolder (view);
        }

        @Override
        public void onBindViewHolder (@NonNull conductorViewHolder holder, int position) {
            Conductor conductor = data.get (position);
            String Nombre = conductor.IdPersonaNavigation.Nombre+" "+ conductor.IdPersonaNavigation.ApellidoPaterno + " "+ conductor.IdPersonaNavigation.ApellidoMaterno;
            String Placa = conductor.IdVehiculoNavigation.Placa;
            String Vehiculo = conductor.IdVehiculoNavigation.Marca +" "+ conductor.IdVehiculoNavigation.Modelo + " "+conductor.IdVehiculoNavigation.Numero;
            holder.setData (Nombre,Placa,Vehiculo);
        }

        @Override
        public int getItemCount () {
            return data.size ();
        }

        class conductorViewHolder extends RecyclerView.ViewHolder {
            TextView texto,texto2,texto3;

            conductorViewHolder (View itemView) {
                super (itemView);
                texto = itemView.findViewById(R.id.Nombre);
                texto2 = itemView.findViewById(R.id.Placas);
                texto3 = itemView.findViewById(R.id.Vehiculo);
            }

            void setData (String data1,String data2,String data3) {
                texto.setText(data1);
                texto2.setText(data2);
                texto3.setText(data3);
            }
        }
    }
}
