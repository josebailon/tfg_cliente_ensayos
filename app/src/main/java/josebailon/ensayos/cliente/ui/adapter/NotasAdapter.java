package josebailon.ensayos.cliente.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import josebailon.ensayos.cliente.data.database.entity.CancionEntity;
import josebailon.ensayos.cliente.databinding.CancionItemBinding;
import josebailon.ensayos.cliente.fragmentos.VercanciondetalleFragment;
import josebailon.ensayos.cliente.fragmentos.VergrupodetalleFragment;


public class NotasAdapter extends RecyclerView.Adapter<NotasAdapter.ViewHolder> {

    List<CancionEntity> list;
    VercanciondetalleFragment fragment;

    public NotasAdapter(List<CancionEntity> list, VercanciondetalleFragment fragment) {
        this.list = list;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CancionItemBinding binding =CancionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.texto.setText(list.get(position).getNombre());
        holder.itemView.setOnClickListener(v -> fragment.verCancion(list.get(position).getId()));
        holder.itemView.setOnLongClickListener(v -> fragment.mostrarMenuCancion(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<CancionEntity> canciones) {
        this.list=canciones;
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CancionItemBinding binding;
        public ViewHolder(@NonNull CancionItemBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

    }
}
