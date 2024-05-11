package josebailon.ensayos.cliente.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import josebailon.ensayos.cliente.data.database.entity.CancionEntity;
import josebailon.ensayos.cliente.data.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.databinding.CancionItemBinding;
import josebailon.ensayos.cliente.databinding.UsuarioItemBinding;
import josebailon.ensayos.cliente.fragmentos.VergrupodetalleFragment;


public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.ViewHolder> {

    List<UsuarioEntity> list;
    VergrupodetalleFragment fragment;

    public UsuariosAdapter(List<UsuarioEntity> list, VergrupodetalleFragment fragment) {
        this.list = list;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UsuarioItemBinding binding =UsuarioItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.nombre.setText(list.get(position).getEmail());
        holder.itemView.setOnLongClickListener(v -> fragment.mostrarMenuUsuario(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<UsuarioEntity> usuarios) {
        this.list=usuarios;
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        UsuarioItemBinding binding;
        public ViewHolder(@NonNull UsuarioItemBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

    }
}
