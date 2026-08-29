package com.example.inventariosqlitejava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onClickEdit(Product product);
        void onClickDelete(Product product);
    }

    private List<Product> productList;
    private final OnProductClickListener listener;

    public ProductAdapter(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvNombre.setText(product.getNombre());
        holder.tvDescripcion.setText(product.getDescripcion());
        holder.tvPrecio.setText(String.format(Locale.getDefault(), "Precio: %.2f", product.getPrecio()));
        holder.tvCantidad.setText(String.format(Locale.getDefault(), "Cantidad: %d", product.getCantidad()));

        // Aseguramos que los nombres coincidan con la interfaz
        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) listener.onClickEdit(product);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) listener.onClickDelete(product);
        });
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvPrecio, tvCantidad;
        View btnEditar, btnEliminar;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}





