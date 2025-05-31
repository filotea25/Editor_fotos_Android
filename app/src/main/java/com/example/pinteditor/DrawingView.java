package com.example.pinteditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class DrawingView extends View {

    private Bitmap imagenDeFondo;
    private Paint pincel;//para decir cómopintar
    private   static Path trazoActual;//guarda lo que el usuario dibuja
    private String formaSeleccionada= "libre";
    private float inicioX, inicioY, finX, finY;




    @SuppressLint("ResourceAsColor")
    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);//conecta esta clase con el sistema de android

        pincel = new Paint();
        pincel.setColor(Color.BLACK);
        pincel.setStrokeWidth(8);
        pincel.setStyle(Paint.Style.STROKE);
        pincel.setAntiAlias(true);
        //inicializo el path de la ruta
        trazoActual= new Path();



    }


    protected  void  onDraw(Canvas canvas){
        super.onDraw(canvas);//llama al metodo original por si hay algo mas que pintar
        //Dibuja la imagen de fondo si hay
        if (imagenDeFondo != null) {
            canvas.drawBitmap(imagenDeFondo, 0, 0, null);
        }

        // Dibuja el trazo encima
        //canvas.drawPath(trazoActual, pincel);

        //DIBUJA SEGÚN FORMA SELECCIONADA
        switch (formaSeleccionada){
            case "libre":
                canvas.drawPath(trazoActual,pincel);
                break;
            case "linea":
                canvas.drawLine(inicioX,inicioY,finX,finY,pincel);
                break;
            case "circulo":
                canvas.drawOval(inicioX,inicioY,finX,finY,pincel);
                break;
            case "cuadrado":
                canvas.drawCircle(inicioX,inicioY,finX,pincel);
                break;

            //añadir otras formas

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        float x=event.getX();
        float y=event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                inicioX=x;
                inicioY=y;
                if(formaSeleccionada.equals("libre")){
                    trazoActual.moveTo(x,y);
                }
                break;

            case  MotionEvent.ACTION_MOVE:
                if(formaSeleccionada.equals("libre")) {
                    trazoActual.lineTo(x, y);
                    invalidate(); // redibuja mientras se mueve
                } else {
                    finX = x;
                    finY = y;
                    invalidate(); // redibuja mientras se mueve para formas fijas
                }
                break;
            case MotionEvent.ACTION_UP:
                finX=x;
                finY=y;
                invalidate();//redibuja el lienzo
                break;
        }


        return  true;//Indica que el ebento fue manejado

    }



    public void limpiar(){
        trazoActual.reset();//borra el path
        invalidate();//redibuja el lienzo

    }

    //cambia el color
    public void cambiarColor(int nuevoColor){
        pincel.setColor(nuevoColor);
        invalidate();
    }

    public  void  setImagenDeFondo(Bitmap bitmap)
    {
        imagenDeFondo=bitmap;
        invalidate();//redibuja el lienzo
    }


    public  void setFormaSeleccionada(String forma){
        this.formaSeleccionada=forma;
    }
}
