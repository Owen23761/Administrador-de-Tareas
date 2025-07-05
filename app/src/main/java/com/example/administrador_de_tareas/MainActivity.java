package com.example.administrador_de_tareas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private DBHelper db;
    private List<Task> tareas;
    private List<Task> tareasFiltradas;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private SearchView buscador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHelper(this);

        listView = findViewById(R.id.listView);
        buscador = findViewById(R.id.buscador);
        Button agregarBtn = findViewById(R.id.agregarBtn);

        // Evento para botón agregar nueva tarea
        agregarBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivity(intent);
        });

        // Cargar todas las tareas al iniciar
        cargarLista("");

        // Listener para búsqueda en tiempo real
        buscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                cargarLista(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cargarLista(newText);
                return true;
            }
        });

        // Listener al hacer clic en un item de la lista
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Task tareaSeleccionada = tareasFiltradas.get(position);
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            intent.putExtra("id", tareaSeleccionada.getId());
            intent.putExtra("titulo", tareaSeleccionada.getTitulo());
            intent.putExtra("categoria", tareaSeleccionada.getCategoria());
            intent.putExtra("estado", tareaSeleccionada.getEstado());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarLista(buscador.getQuery().toString());
    }

    private void cargarLista(String filtro) {
        tareas = db.obtenerTareas();
        if (tareas == null) tareas = new ArrayList<>();

        if (filtro.isEmpty()) {
            tareasFiltradas = new ArrayList<>(tareas);
        } else {
            tareasFiltradas = tareas.stream()
                    .filter(t -> t.getTitulo().toLowerCase().contains(filtro.toLowerCase()))
                    .collect(Collectors.toList());
        }

        ArrayList<String> titulos = new ArrayList<>();
        for (Task t : tareasFiltradas) {
            titulos.add(t.getTitulo() + " - " + t.getEstado());
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titulos);
        listView.setAdapter(adapter);
    }
}
