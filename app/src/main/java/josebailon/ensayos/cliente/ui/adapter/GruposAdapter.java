package josebailon.ensayos.cliente.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import josebailon.ensayos.cliente.R;
import josebailon.ensayos.cliente.data.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.databinding.GrupoItemBinding;
import josebailon.ensayos.cliente.fragmentos.VergruposFragment;


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
