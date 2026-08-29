package com.example.inventariosqlitejava;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private ProductDbHelper dbHelper;
    private ProductAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new ProductDbHelper(this);

        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.tvEmpty);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicialización con lista vacía o datos iniciales
        adapter = new ProductAdapter(dbHelper.getAllProducts(), this);
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showProductoDialog(null));

        refreshList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    // Metodo para refrescar la lista (FALTABA ESTO ANTES)
    private void refreshList() {
        List<Product> products = dbHelper.getAllProducts();

        // Si tu adaptador tiene un metodo setProductList, úsalo. Si no, recrea el adaptador.
        // Asumiendo que ProductAdapter tiene setProductList como se vio en el código:
        adapter.setProductList(products);

        // Controlar la vista vacía
        if (products.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showProductoDialog(final Product productToEdit) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_product, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombre);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcion);
        EditText etPrecio = dialogView.findViewById(R.id.etPrecio);
        EditText etCantidad = dialogView.findViewById(R.id.etCantidad);

        boolean esEdicion = productToEdit != null;

        if (esEdicion) {
            etNombre.setText(productToEdit.getNombre());
            etDescripcion.setText(productToEdit.getDescripcion());
            etPrecio.setText(String.valueOf(productToEdit.getPrecio()));
            etCantidad.setText(String.valueOf(productToEdit.getCantidad()));
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(esEdicion ? "Editar producto" : "Agregar producto")
                .setView(dialogView)
                .setPositiveButton(esEdicion ? "Actualizar" : "Guardar", null)
                .setNegativeButton("Cancelar", (DialogInterface d, int which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            View btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            btnPositive.setOnClickListener(v -> {
                String nombre = etNombre.getText().toString().trim();
                String descripcion = etDescripcion.getText().toString().trim();
                String precioStr = etPrecio.getText().toString().trim();
                String cantidadStr = etCantidad.getText().toString().trim();

                if (TextUtils.isEmpty(nombre)) {
                    etNombre.setError("El nombre es obligatorio");
                    return;
                }

                double precio;
                try {
                    precio = TextUtils.isEmpty(precioStr) ? 0 : Double.parseDouble(precioStr);
                } catch (NumberFormatException e) {
                    etPrecio.setError("Precio inválido");
                    return;
                }

                int cantidad;
                try {
                    cantidad = TextUtils.isEmpty(cantidadStr) ? 0 : Integer.parseInt(cantidadStr);
                } catch (NumberFormatException e) {
                    etCantidad.setError("Cantidad inválida");
                    return;
                }

                if (esEdicion) {
                    // CORRECCIÓN: Usar 'productToEdit' (la variable correcta) en lugar de 'productoToEdit'
                    productToEdit.setNombre(nombre);
                    productToEdit.setDescripcion(descripcion);
                    productToEdit.setPrecio(precio);
                    productToEdit.setCantidad(cantidad);

                    dbHelper.updateProduct(productToEdit);
                    Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show();
                } else {
                    Product nuevo = new Product(nombre, descripcion, precio, cantidad);
                    dbHelper.insertProduct(nuevo);
                    Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
                refreshList();
            });
        });

        dialog.show();
    }

    // --- IMPLEMENTACIÓN DE LA INTERFAZ (MÉTODOS MOVIDOS FUERA DE OTROS MÉTODOS) ---

    @Override
    public void onClickEdit(Product product) {
        showProductoDialog(product);
    }

    @Override
    public void onClickDelete(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Seguro deseas eliminar?")
                .setPositiveButton("Eliminar", (DialogInterface d, int which) -> {
                    dbHelper.deleteProduct(product.getId());
                    Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show();
                    refreshList();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}