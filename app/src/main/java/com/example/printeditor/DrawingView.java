package com.example.printeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.Nullable;

public class DrawingView extends View {
        private Paint pincel;//para decir cómopintar
        private   static Path trazoActual;//guarda lo que el usuario dibuja

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
        canvas.drawPath(trazoActual,pincel);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        float x=event.getX();
        float y=event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                trazoActual.moveTo(x,y);//empieza el trazo desde donde se tocó
                break;

            case  MotionEvent.ACTION_MOVE:
                trazoActual.lineTo(x,y);//dibuja una linea hasta la nueva posicion
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        invalidate();//redibuja la vista con el nuevo trazo
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
}
