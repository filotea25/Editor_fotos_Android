package com.example.printeditor;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button botonLimpiar, botonCargar;
    private DrawingView vistaDibujo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        vistaDibujo=findViewById(R.id.vitaDibujo);
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
        botonCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
    private  void  abrirGaleria(){
        Intent intent = new Intent().setAction(Intent.ACTION_VIEW); //Define Intent.
        intent.setType("image/*"); //Define mime type para abrir imagenes
        startActivity(intent); //Abre galería.
    }


}