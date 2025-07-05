package com.example.administrador_de_tareas;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tareasDB.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TAREAS = "tareas";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creación de la tabla
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_TAREAS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "titulo TEXT," +
                "categoria TEXT," +
                "estado TEXT" +
                ")";
        db.execSQL(CREATE_TABLE);
    }

    // Actualizar la base de datos (no usado aquí)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAREAS);
        onCreate(db);
    }

    // Obtener todas las tareas
    public List<Task> obtenerTareas() {
        List<Task> tareas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT id, titulo, categoria, estado FROM " + TABLE_TAREAS;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String titulo = cursor.getString(1);
                String categoria = cursor.getString(2);
                String estado = cursor.getString(3);

                Task tarea = new Task(id, titulo, categoria, estado);
                tareas.add(tarea);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tareas;
    }

    // Agregar tarea
    public long agregarTarea(Task tarea) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("titulo", tarea.getTitulo());
        values.put("categoria", tarea.getCategoria());
        values.put("estado", tarea.getEstado());

        long id = db.insert(TABLE_TAREAS, null, values);
        db.close();
        return id;
    }

    // Actualizar tarea
    public int actualizarTarea(Task tarea) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("titulo", tarea.getTitulo());
        values.put("categoria", tarea.getCategoria());
        values.put("estado", tarea.getEstado());

        // Actualiza la tarea con id específico
        int filasAfectadas = db.update(TABLE_TAREAS, values, "id = ?", new String[]{String.valueOf(tarea.getId())});
        db.close();
        return filasAfectadas;
    }

    // Eliminar tarea por id
    public int eliminarTarea(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filasAfectadas = db.delete(TABLE_TAREAS, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return filasAfectadas;
    }
}
