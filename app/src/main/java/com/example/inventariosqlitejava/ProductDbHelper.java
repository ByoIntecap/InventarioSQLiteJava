package com.example.inventariosqlitejava;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ProductDbHelper extends SQLiteOpenHelper {

    //Informacion de la base de datos
    // Nombre del archivo de la base de datos SQLite
    private static final String DATABASE_NAME = "productos.db";
    // Versión de la base de datos (se usa para migraciones)
    private static final int DATABASE_VERSION = 1;

    //tablas y columnas
    // Nombre de la tabla en la base de datos
    public static final String TABLE_PRODUCT = "productos";
    // Nombre de la columna para el identificador único
    public static final String COLUMN_ID = "_id";
    // Nombre de la columna para el nombre del producto
    public static final String COLUMN_NOMBRE = "nombre";
    // Nombre de la columna para la descripción del producto
    public static final String COLUMN_DESCRIPCION = "descripcion";
    // Nombre de la columna para el precio del producto
    public static final String COLUMN_PRECIO = "precio";
    // Nombre de la columna para la cantidad disponible del producto
    public static final String COLUMN_CANTIDAD = "cantidad";

    //sentencias sql para crear la tabla
    // Comando SQL para crear la tabla 'productos' con sus columnas y tipos de datos
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_PRODUCT + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOMBRE + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPCION + " TEXT, " +
                    COLUMN_PRECIO + " REAL NOT NULL DEFAULT 0, " +
                    COLUMN_CANTIDAD + " INTEGER NOT NULL DEFAULT 0 " +
                    ");";

    // Comando SQL para eliminar la tabla existente (útil durante desarrollo o actualizaciones)
    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_PRODUCT;

// Constructor de la clase que llama al constructor de la clase padre (SQLiteOpenHelper)
public ProductDbHelper(Context context) {
    // Llama al constructor de SQLiteOpenHelper con el contexto, nombre de la base de datos y versión
    // 'factory: null' indica que no se usa un custom cursor factory
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
}


@Override
public void onCreate(SQLiteDatabase db) {
    // Ejecuta el comando SQL para crear la tabla 'productos'
    db.execSQL(SQL_CREATE_TABLE);
}


@Override
public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    // Primero elimina la tabla existente (pérdida de datos, útil para desarrollo)
    db.execSQL(SQL_DELETE_TABLE);
    // Luego recrea la tabla desde cero
    onCreate(db);
}


@Override
public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Llama a onUpgrade para manejar la situación (Android por defecto lanza excepción en downgrade)
    onUpgrade(db, oldVersion, newVersion);
}
  // Metodos CRUD de productos

    // Insertar
    public long insertProduct(Product product) {
        // Obtiene una instancia de la base de datos en modo escritura
        SQLiteDatabase db = this.getWritableDatabase();

        // Crea un objeto ContentValues para almacenar los datos del producto en formato clave-valor
        ContentValues values = new ContentValues();
        // Inserta el nombre del producto en el ContentValues
        values.put(COLUMN_NOMBRE, product.getNombre());
        // Inserta la descripción del producto
        values.put(COLUMN_DESCRIPCION, product.getDescripcion());
        // Inserta el precio del producto
        values.put(COLUMN_PRECIO, product.getPrecio());
        // Inserta la cantidad del producto
        values.put(COLUMN_CANTIDAD, product.getCantidad());

        // Ejecuta la inserción en la tabla y devuelve el ID de la fila insertada (o -1 si falla)
        long id = db.insert(TABLE_PRODUCT, null, values);
        // Cierra la conexión a la base de datos
        db.close();
        // Devuelve el ID generado para el nuevo producto
        return id;
    }

    // Obtener
    public Product getProduct(long id) {
        // Obtiene una instancia de la base de datos en modo lectura
        SQLiteDatabase db = this.getReadableDatabase();

        // Ejecuta una consulta para obtener los datos del producto con el ID especificado
        Cursor cursor = db.query(
                TABLE_PRODUCT,              // Nombre de la tabla
                null,                       // Columnas a devolver (null = todas)
                COLUMN_ID + "=?",           // Condición de filtro (WHERE id = ?)
                new String[]{String.valueOf(id)}, // Parámetro para la condición
                null, null, null
        );

        // Inicializa la variable del producto como null
        Product product = null;
        // Verifica que el cursor no sea nulo y que pueda moverse a la primera fila
        if (cursor != null && cursor.moveToFirst()) {
            // Convierte los datos del cursor a un objeto Product
            product = cursorToProduct(cursor);
            // Cierra el cursor después de usarlo
            cursor.close();
        }
        // Cierra la conexión a la base de datos
        db.close();
        // Devuelve el objeto Product encontrado (o null si no existe)
        return product;
    }
    // Convertir cursor en un objeto producto
    private Product cursorToProduct(Cursor cursor) {
        Product product = new Product();
        product.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        product.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)));
        product.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION)));
        product.setPrecio(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRECIO)));
        product.setCantidad(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CANTIDAD)));
        return product;
    }

    // Buscar producto por nombre
    public List<Product> searchProductsByName(String query) {
        // Crea una lista vacía para almacenar los resultados
        List<Product> productList = new ArrayList<>();

        // Obtiene una instancia de la base de datos en modo lectura
        SQLiteDatabase db = this.getReadableDatabase();

        // Ejecuta una consulta para buscar productos cuyo nombre contenga el texto buscado
        Cursor cursor = db.query(
                TABLE_PRODUCT,                // Tabla a consultar
                null,                         // Columnas (null = todas)
                COLUMN_NOMBRE + " LIKE ?",   // Condición: nombre LIKE '%consulta%'
                new String[]{"%" + query + "%"}, // Parámetro para la búsqueda (comodines incluidos)
                null,                         // GROUP BY (no se usa)
                null,                         // HAVING (no se usa)
                COLUMN_NOMBRE + " ASC"        // Ordenar resultados por nombre de forma ascendente
        );

        // Verifica si el cursor contiene resultados
        if (cursor != null && cursor.moveToFirst()) {
            // Bucle mientras haya más registros en el cursor
            do {
                // Convierte cada fila del cursor a un objeto Product y lo agrega a la lista
                productList.add(cursorToProduct(cursor));
            } while (cursor.moveToNext()); // Avanza al siguiente registro
            cursor.close(); // Cierra el cursor después de terminar
        }
        db.close(); // Cierra la conexión a la base de datos
        return productList; // Devuelve la lista de productos encontrados
    }

    // Actualizar producto
    public int updateProduct(Product product) {
        // Obtiene una instancia de la base de datos en modo escritura
        SQLiteDatabase db = this.getWritableDatabase();

        // Crea un objeto ContentValues para almacenar los nuevos datos
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, product.getNombre());
        values.put(COLUMN_DESCRIPCION, product.getDescripcion());
        values.put(COLUMN_PRECIO, product.getPrecio());
        values.put(COLUMN_CANTIDAD, product.getCantidad());

        // Ejecuta la actualización y devuelve el número de filas modificadas
        int rowsAffected = db.update(
                TABLE_PRODUCT,            // Nombre de la tabla
                values,                   // Datos a actualizar
                COLUMN_ID + "=?",        // Condición WHERE: WHERE _id = ?
                new String[]{String.valueOf(product.getId())} // Parámetro para la condición
        );

        db.close(); // Cierra la conexión a la base de datos
        return rowsAffected; // Devuelve cuántas filas fueron actualizadas (1 si tuvo éxito, 0 si no)
    }

    // Eliminar producto por ID
    public void deleteProduct(long id) {
        // Obtiene una instancia de la base de datos en modo escritura
        SQLiteDatabase db = this.getWritableDatabase();

        // Ejecuta la eliminación del producto con el ID especificado
        db.delete(
                TABLE_PRODUCT,            // Nombre de la tabla a eliminar
                COLUMN_ID + "=?",        // Condición WHERE: WHERE _id = ?
                new String[]{String.valueOf(id)} // Parámetro para la condición (el ID del producto)
        );

        db.close(); // Cierra la conexión a la base de datos
    }

    // Eliminar todos los productos de la tabla
    public void deleteAllProducts() {
        // Obtiene una instancia de la base de datos en modo escritura
        SQLiteDatabase db = this.getWritableDatabase();

        // Ejecuta la eliminación de TODOS los productos (sin condición WHERE)
        db.delete(TABLE_PRODUCT, null, null);

        db.close(); // Cierra la conexión a la base de datos
    }

    // Metodo para obtener el total de productos registrados en la base de datos
    public int getProductCount() {
        // Obtiene una instancia de la base de datos en modo lectura
        SQLiteDatabase db = this.getReadableDatabase();

        // Ejecuta una consulta SQL directa para contar el número total de filas en la tabla
        // SELECT COUNT(*) devuelve un solo valor numérico
        Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_PRODUCT, null);

        // Variable para almacenar el resultado del conteo
        int count = 0;

        // Verifica si el cursor tiene resultados y se mueve a la primera fila
        if (cursor.moveToFirst()) {
            // Obtiene el valor entero de la primera (y única) columna del resultado
            // El índice 0 corresponde a la columna devuelta por COUNT(*)
            count = cursor.getInt(0);
        }

        // Cierra el cursor para liberar recursos
        cursor.close();
        // Cierra la conexión a la base de datos
        db.close();

        // Devuelve el número total de productos encontrados
        return count;
    }
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCT, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                productList.add(cursorToProduct(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return productList;
    }
}