package com.example.pinteditor;

import static com.example.pinteditor.R.*;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> galeriaLauncher;
    String[] formas={"libre","linea","cuadrado","círculo","rectangulo","Triangulo"};
    private Button botonLimpiar, botonCargar;
    private DrawingView vistaDibujo;
    private Spinner spinnerForma;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // CONECTAR SPINNER Y ASIGNAR FORMA

        //  Conecta el Spinner y asigna las opciones
        spinnerForma=findViewById(R.id.spinnerForma);
        ArrayAdapter<String> adapter =new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                formas
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerForma.setAdapter(adapter);

        //Escucha el cambio de opción y pásaselo al DrawingView
        spinnerForma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String formaSeleccionada= formas[position].toLowerCase(Locale.ROOT);
                vistaDibujo.setFormaSeleccionada(formaSeleccionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        galeriaLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK && result.getData()!= null){
                        Uri uriImagen= result.getData().getData();
                        Bitmap bitmap= null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriImagen);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        vistaDibujo.setImagenDeFondo(bitmap);
                    }
                }
        );
        vistaDibujo=findViewById(id.vistaDibujo);
        botonLimpiar=findViewById(R.id.btnLimpiar);
        botonLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vistaDibujo.limpiar();
            }
        });
        //BOTON PARA CAMBIAR DE COLOR
        Button botonColor=findViewById(R.id.btnColor);
        botonColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarSelectorDeColor();
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //BOTON PARA CARGAR IMAGENES
        botonCargar=findViewById(R.id.btnCargar);
        //para abrir galeria
        botonCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirGaleria();
            }
        });
    }
    //METOD PARA ABRIR DIALOGO CON OPCIONES DE COLORES
    private  void mostrarSelectorDeColor(){
        final String[]  nombresColores={"blanco","negro","rojo","azul","verde","naranja","gris","amarillo"};
        final  int[] valoresColores={
                Color.WHITE,
                Color.BLACK,
                Color.RED,
                Color.BLUE,
                Color.GREEN,
                Color.GRAY,
                Color.YELLOW,
        };

        new AlertDialog.Builder(this)
                .setTitle("Elige un color")
                .setItems(nombresColores,(dialog,wich)->{
                    int colorSelecionado=valoresColores[wich];
                    vistaDibujo.cambiarColor(colorSelecionado);
                })
                .show();
    }

    //MÉTODO PARA ABRIR LA GALERIA
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galeriaLauncher.launch(intent);
    }
}