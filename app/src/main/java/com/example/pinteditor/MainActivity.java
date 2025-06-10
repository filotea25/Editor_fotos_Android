package com.example.pinteditor;



import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> galeriaLauncher;
    String[] formas={"libre","linea","circulo","cuadrado"};

    private Button botonCargar;
   private AppCompatButton botonGuardar,botonLimpiar,botonEditar ;

    private ImageButton btnRehacer,btnDeshacer;
    private DrawingView vistaDibujo;
    private Spinner spinnerForma;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //boton para editar
        botonEditar=findViewById(R.id.btnEditar);
        botonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menud= new PopupMenu(MainActivity.this, view);
                menud.getMenuInflater().inflate(R.menu.menu_editar,menud.getMenu());

                menud.setOnMenuItemClickListener(menuItem -> {
                    int id= menuItem.getItemId();

                    if(id == R.id.opcion_rotar){
                        vistaDibujo.rotarImagen90();
                    }else if (id == R.id.opcion_opacidad) {
                     dialogoOpacidad();
                        return true;
                    }else if (id == R.id.opcion_Trazo) {
                        mostarrDialogoTrazo();
                        return true;

                    }else if (id == R.id.opcion_redimensionar) {

                        Toast.makeText(MainActivity.this, "Elige el tamaño de redimensión", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (id==R.id.opcion_redimensionar_70) {
                            vistaDibujo.escalarImagen();
                            return true;
                    } else if (id==R.id.opcion_redimensionar_50) {
                            vistaDibujo.escalarImagen50();
                            return  true;
                    } else if (id==R.id.opcion_redimensionar_25) {
                            vistaDibujo.escalarImagen25();
                            return true;
                    } else if (id==R.id.original) {
                    vistaDibujo.escalarAOriginal();
                    return true;
                }

                    return false;
                });
                    menud.show();

            }
        });

        //para mostrar el diálogo deopacidad


        //BOTON REHACER
        btnRehacer=findViewById(R.id.btnRedo);
        btnRehacer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vistaDibujo.reHacer();
                Toast.makeText(MainActivity.this, "Deshacer listo!!", Toast.LENGTH_LONG).show();
            }
        });

        //BOTON DESHACER
        btnDeshacer=findViewById(R.id.btnUndo);
        btnDeshacer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vistaDibujo.desHacer();
                Toast.makeText(MainActivity.this, "Deshacer listo!!", Toast.LENGTH_LONG);
            }
        });

        //BOTON PARA GUARDAR LA IMAGEN EDITADA

        botonGuardar=findViewById(R.id.btnGuardar);
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vistaDibujo.estaVacio()){
                    Toast.makeText(MainActivity.this, "No hay nada que guardar", Toast.LENGTH_SHORT).show();
                }else{
                    guardarFoto();
                    Toast.makeText(MainActivity.this, "foto guardada con éxito", Toast.LENGTH_SHORT).show();
                }

            }
        });
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
        //para coger una imagen de la galería
        galeriaLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK && result.getData()!= null){
                        Uri uriImagen= result.getData().getData();
                        Bitmap bitmap= null;
                        try {
                            InputStream is= getContentResolver().openInputStream(uriImagen);
                            bitmap= BitmapFactory.decodeStream(is);
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                        }

                        if(bitmap != null){
                            vistaDibujo.setImagenDeFondo(bitmap);
                        }else{
                            Toast.makeText(this, "La imagen no se pudo cargar", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        vistaDibujo=findViewById(R.id.vistaDibujo);
        botonLimpiar=findViewById(R.id.btnLimpiar);
        botonLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vistaDibujo.estaVacio()){
                    Toast.makeText(MainActivity.this, "No hay nada que borrar", Toast.LENGTH_SHORT).show();

                }else{
                    vistaDibujo.limpiar();
                    Toast.makeText(MainActivity.this, "Borrado con éxito", Toast.LENGTH_SHORT).show();
                }


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
        final String[]  nombresColores={"blanco","negro","rojo","azul","verde","amarillo"};
        final  int[] valoresColores={
                Color.WHITE,
                Color.BLACK,
                Color.RED,
                Color.BLUE,
                Color.GREEN,
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


    private void dialogoOpacidad() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajustar Opacidad");

        // Crea el SeekBar dinámicamente
        final SeekBar seekBar = new SeekBar(this);
        seekBar.setMax(255);
        seekBar.setProgress(vistaDibujo.getOpacidadActual());

        // Escucha los cambios
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vistaDibujo.ajustarOpacidad(progress);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        builder.setView(seekBar);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void guardarFoto(){
        //obtener el bitmap del DrawingView
        Bitmap bitmap= vistaDibujo.getBitmap();
        //crear el nombre del archivo
        String nombre_Archivo = "dibujo_"+System.currentTimeMillis()+".png";

        //ruta donde guardar la imagen (la carpeta publica de imagenes)
        File directorio = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PintEditor");
        if (!directorio.exists()) {

            directorio.mkdirs();
        }
        try {
            File archivoImagen= new File(directorio, nombre_Archivo);
            FileOutputStream out= new FileOutputStream(archivoImagen);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
            out.flush();
            out.close();

            //notoficar a la galería
            Intent intent= new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(archivoImagen));
            sendBroadcast(intent);

            Toast.makeText(this, "Foto guardada  en "+archivoImagen.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this, "Error al guardar la foto"+e.getMessage(), Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }

    }
    private void mostarrDialogoTrazo(){
        View vista= getLayoutInflater().inflate(R.layout.trazo,null);
        SeekBar seekBar= vista.findViewById(R.id.seekBarTrazo);
        TextView valor = vista.findViewById(R.id.valorTrazo);


        //valor inicial de trazo
        seekBar.setProgress(10);
        valor.setText("Tamaño: 10");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int tamaño= Math.max(progress, 1);
                valor.setText("tamaño: " + tamaño);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                    int tamaño= Math.max(seekBar.getProgress(),1);
                    vistaDibujo.setTamañoPincel(tamaño);
            }
        });

        new AlertDialog.Builder(this)
                .setTitle("Ajustar tamaño")
                .setView(vista)
                .setPositiveButton("OK", null)
                .show();
    }
}