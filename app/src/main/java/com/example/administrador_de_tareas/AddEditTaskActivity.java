package com.example.administrador_de_tareas;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditTaskActivity extends AppCompatActivity {

    private EditText edtTitulo, edtCategoria;
    private Spinner spinnerEstado;
    private Button btnGuardar, btnEliminar;

    private DBHelper db;

    private int idTarea = -1; // -1 significa nueva tarea

    private ArrayAdapter<String> adapterEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        edtTitulo = findViewById(R.id.edtTitulo);
        edtCategoria = findViewById(R.id.edtCategoria);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnEliminar = findViewById(R.id.btnEliminar);

        db = new DBHelper(this);

        // Configurar el Spinner con opciones fijas
        String[] opcionesEstado = {"Planeación", "En proceso", "Terminado"};
        adapterEstado = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opcionesEstado);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstado);

        // Ver si vienen datos para editar
        if (getIntent() != null) {
            idTarea = getIntent().getIntExtra("id", -1);
            String titulo = getIntent().getStringExtra("titulo");
            String categoria = getIntent().getStringExtra("categoria");
            String estado = getIntent().getStringExtra("estado");

            if (idTarea != -1) {
                edtTitulo.setText(titulo);
                edtCategoria.setText(categoria);

                // Seleccionar el estado correcto en el spinner
                int posEstado = adapterEstado.getPosition(estado);
                if (posEstado >= 0) {
                    spinnerEstado.setSelection(posEstado);
                }
                btnEliminar.setVisibility(Button.VISIBLE);
            } else {
                btnEliminar.setVisibility(Button.GONE);
            }
        }

        btnGuardar.setOnClickListener(v -> {
            String titulo = edtTitulo.getText().toString().trim();
            String categoria = edtCategoria.getText().toString().trim();
            String estado = spinnerEstado.getSelectedItem().toString();

            if (titulo.isEmpty() || categoria.isEmpty() || estado.isEmpty()) {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (idTarea == -1) {
                // Crear nueva tarea
                Task nuevaTarea = new Task();
                nuevaTarea.setTitulo(titulo);
                nuevaTarea.setCategoria(categoria);
                nuevaTarea.setEstado(estado);

                long resultado = db.agregarTarea(nuevaTarea);
                if (resultado != -1) {
                    Toast.makeText(this, "Tarea agregada", Toast.LENGTH_SHORT).show();
                    finish();  // Cerrar para volver a MainActivity
                } else {
                    Toast.makeText(this, "Error al agregar tarea", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Actualizar tarea existente
                Task tareaEditada = new Task();
                tareaEditada.setId(idTarea);
                tareaEditada.setTitulo(titulo);
                tareaEditada.setCategoria(categoria);
                tareaEditada.setEstado(estado);

                int resultado = db.actualizarTarea(tareaEditada);
                if (resultado > 0) {
                    Toast.makeText(this, "Tarea actualizada", Toast.LENGTH_SHORT).show();
                    finish(); // Cerrar para volver a MainActivity
                } else {
                    Toast.makeText(this, "Error al actualizar tarea", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnEliminar.setOnClickListener(v -> {
            if (idTarea != -1) {
                int resultado = db.eliminarTarea(idTarea);
                if (resultado > 0) {
                    Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error al eliminar tarea", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
