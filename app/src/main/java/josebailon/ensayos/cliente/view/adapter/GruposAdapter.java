package josebailon.ensayos.cliente.view.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.databinding.GrupoItemBinding;
import josebailon.ensayos.cliente.view.fragment.VergruposFragment;


public class GruposAdapter extends RecyclerView.Adapter<GruposAdapter.ViewHolder> {

    List<GrupoEntity> list;
    VergruposFragment fragment;

    public GruposAdapter(List<GrupoEntity> list, VergruposFragment fragment) {
        this.list = list;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GrupoItemBinding binding =GrupoItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy HH:mm:ss", Locale.getDefault());
        if (list.get(position).getFecha()!=null) {
            String fechaFormateada = dateFormat.format(list.get(position).getFecha());
            Log.i("JJBO", "fecha de " + position + ":" + fechaFormateada);
        }
        holder.binding.nombre.setText(list.get(position).getNombre());
        holder.itemView.setOnClickListener(v -> fragment.verGrupo(list.get(position).getId()));
        holder.itemView.setOnLongClickListener(v -> fragment.mostrarMenu(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<GrupoEntity> grupos) {
        this.list=grupos;
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        GrupoItemBinding binding;
        public ViewHolder(@NonNull GrupoItemBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

    }
}
