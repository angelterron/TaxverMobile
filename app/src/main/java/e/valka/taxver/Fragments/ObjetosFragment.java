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
import e.valka.taxver.Models.ObjetosList;
import e.valka.taxver.Models.ObjetosPerdidos;
import e.valka.taxver.Models.Posicionconductor;
import e.valka.taxver.Models.Posiciones;
import e.valka.taxver.Models.Viaje;
import e.valka.taxver.Models.Viajes;
import e.valka.taxver.R;
import e.valka.taxver.Utils.CircleTransform;
import e.valka.taxver.Utils.DownloadAsyncTask;
import e.valka.taxver.Utils.URLS;
import e.valka.taxver.navigationActivity;

public class ObjetosFragment extends android.support.v4.app.Fragment {
    ArrayList<ObjetosPerdidos> objetosFragment = new ArrayList<>();
    RecyclerView recyclerView;
    public int idPersona;
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_base, container, false);
        recyclerView = v.findViewById (R.id.recycler);
        recyclerView.setLayoutManager (new LinearLayoutManager(getActivity ()));
        new DownloadAsyncTask(this::parseJSON).execute (URLS.Objetos+"?idPersona="+idPersona);
        return v;
    }

    private void parseJSON (String json) {
        ObjetosList objetos = new Gson().fromJson (json, ObjetosList.class);
        if (objetos == null) return;
        objetosFragment = objetos.ObjetosPerdidos;
        recyclerView.setAdapter(new objetosAdapter(objetosFragment));

    }
    class objetosAdapter extends RecyclerView.Adapter<objetosAdapter.objetosViewHolder> {

        private ArrayList<ObjetosPerdidos> data;

        objetosAdapter (ArrayList<ObjetosPerdidos> d) {
            data = d;
        }

        @NonNull
        @Override
        public objetosViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from (parent.getContext ());
            View view = inflater.inflate (R.layout.objetos_items, parent, false);
            return new objetosViewHolder(view);
        }

        @Override
        public void onBindViewHolder (@NonNull objetosViewHolder holder, int position) {
            ObjetosPerdidos objetos = data.get(position);
            String status = null;
            if(objetos.Status == 1)
                status = "No entregado.";
            else
                status = "Entregado";
            holder.setData (objetos.Detalles,status);
        }

        @Override
        public int getItemCount () {
            return data.size ();
        }

        class objetosViewHolder extends RecyclerView.ViewHolder {
            int id;
            TextView texto,texto2;

            objetosViewHolder (View itemView) {
                super (itemView);
                texto = itemView.findViewById(R.id.objeto);
                texto2 = itemView.findViewById(R.id.status);
            }

            void setData (String data1,String data2) {
                texto.setText(data1);
                texto2.setText(data2);
            }
        }
    }
}
