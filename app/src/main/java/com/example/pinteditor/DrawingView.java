package com.example.pinteditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DrawingView extends View {
    private Bitmap imagenDeFondo, imagenOriginal;
    private Paint paintImagenFondo;

    private Paint pincel;//para decir cómopintar
    private   static Path trazoActual;//guarda lo que el usuario dibuja
    private String formaSeleccionada= "libre";
    private float inicioX, inicioY, finX, finY;
    private int opacidadActual=255;//valor inicial, completamente opaco
    private  final ArrayList<Trazo> trazos=new ArrayList<>();
    private  final ArrayList<Trazo>trazosDeshechos= new ArrayList<>();






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
        paintImagenFondo = new Paint();
        paintImagenFondo.setAlpha(opacidadActual);


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (imagenDeFondo != null) {
            canvas.drawBitmap(imagenDeFondo, 0, 0, paintImagenFondo);

        }

        for (Trazo trazo : trazos) {
            canvas.drawPath(trazo.path, trazo.paint);
        }

        if (!formaSeleccionada.equals("libre")) {
            Path formaTemporal = new Path();
            switch (formaSeleccionada) {
                case "linea":
                    formaTemporal.moveTo(inicioX, inicioY);
                    formaTemporal.lineTo(finX, finY);
                    break;
                case "circulo":
                    float centroX = (inicioX + finX) / 2;
                    float centroY = (inicioY + finY) / 2;
                    float radio = Math.max(Math.abs(finX - inicioX), Math.abs(finY - inicioY)) / 2;
                    formaTemporal.addCircle(centroX, centroY, radio, Path.Direction.CW);

                    break;
                case "cuadrado":
                    formaTemporal.addRect(inicioX, inicioY, finX, finY, Path.Direction.CW);
                    break;
            }
            canvas.drawPath(formaTemporal, pincel);
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
                    trazoActual=new Path();
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
                if(formaSeleccionada.equals("libre")){
                    //guarda el trazo libre con su color
                    Path copia=new Path(trazoActual);
                    Paint nuevoPincel=new Paint(pincel);
                    trazos.add(new Trazo(copia,nuevoPincel));
                    trazoActual.reset();
                }else {
                    Path forma= new Path();

                        switch (formaSeleccionada) {
                            case "linea":
                                forma.moveTo(inicioX, inicioY);
                                forma.lineTo(finX, finY);
                                break;
                            case "circulo":

                                float centroX = (inicioX + finX) / 2;
                                float centroY = (inicioY + finY) / 2;
                                float radio = Math.max(Math.abs(finX - inicioX), Math.abs(finY - inicioY)) / 2;
                                forma.addCircle(centroX, centroY, radio, Path.Direction.CW);


                                break;
                            case "cuadrado":

                                 radio = Math.abs(finX - inicioX);
                                forma.addRect(inicioX, inicioY, finX, finY, Path.Direction.CW);
                                break;
                        }
                        Paint nuevoPincel= new Paint(pincel);
                        trazos.add(new Trazo(forma, nuevoPincel));
                    }
                trazosDeshechos.clear();//se pierde el historial futuro
                invalidate();//redibuja el lienzo
                break;
        }


        return  true;//Indica que el ebento fue manejado

    }


    public void rotarImagen90(){
        if(imagenDeFondo != null){
            Matrix matriz= new Matrix();
            matriz.postRotate(90);
            imagenDeFondo=Bitmap.createBitmap(imagenDeFondo, 0, 0,
                                              imagenDeFondo.getWidth(), imagenDeFondo.getHeight(),
                                                matriz, true);

            invalidate();
        }
    }


    public void limpiar(){
        trazoActual.reset();      // Borra el trazo libre
        trazos.clear();           // Borra todos los trazos guardados
        trazosDeshechos.clear();  // Borra también los rehacibles

        imagenOriginal=null;//borra imagen original
        imagenDeFondo=null;// borra imagen de fondo

        // También resetea las coordenadas de forma
        inicioX = finX = 0;
        inicioY = finY = 0;

        invalidate();             // Redibuja
    }

    //cambia el color
    public void cambiarColor(int nuevoColor){
        pincel.setColor(nuevoColor);
        invalidate();
    }

    public  void  setImagenDeFondo(Bitmap bitmap)
    {
        imagenOriginal=bitmap;//guarda copia original
        imagenDeFondo=bitmap;
        invalidate();//redibuja el lienzo
    }


    public  void setFormaSeleccionada(String forma){
        this.formaSeleccionada=forma;
    }

    public Bitmap getBitmap(){
        Bitmap bitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        draw(canvas);
        return bitmap;
    }

    public void desHacer() {
        if (!trazos.isEmpty()) {
            Trazo ultimo = trazos.remove(trazos.size() - 1);
            trazosDeshechos.add(ultimo);
            invalidate();
        }
    }

    public void reHacer() {
        if (!trazosDeshechos.isEmpty()) {
            Trazo recuperado = trazosDeshechos.remove(trazosDeshechos.size() - 1);
            trazos.add(recuperado);
            invalidate();
        }
    }

    public boolean estaVacio(){
        return  trazos.isEmpty() && imagenDeFondo==null;

    }




    public void escalarImagen(){
        if(imagenOriginal != null){
            int ancho=imagenOriginal.getWidth();
            int alto=imagenOriginal.getHeight();

            //escalamos a un 70% su tamaño original
              int nuevoAncho=(int)(ancho*0.7);
              int nuevoAlto= (int)(alto*0.7);



              imagenDeFondo=Bitmap.createScaledBitmap(imagenOriginal,nuevoAncho,nuevoAlto,true);
              invalidate();//redibuja
        }
    }

    public void escalarAOriginal(){
        if(imagenOriginal != null){
            int ancho=imagenOriginal.getWidth();
            int alto=imagenOriginal.getHeight();

            //escalamos a un 100% su tamaño original
            int nuevoAncho=(int)(ancho*1);
            int nuevoAlto= (int)(alto*1);



            imagenDeFondo=Bitmap.createScaledBitmap(imagenOriginal,nuevoAncho,nuevoAlto,true);
            invalidate();//redibuja
        }
    }

    public void escalarImagen25(){
        if(imagenOriginal != null){
            int ancho=imagenOriginal.getWidth();
            int alto=imagenOriginal.getHeight();

            //escalamos a un 25% su tamaño original
            int nuevoAncho=(int)(ancho*0.25);
            int nuevoAlto= (int)(alto*0.25);



            imagenDeFondo=Bitmap.createScaledBitmap(imagenOriginal,nuevoAncho,nuevoAlto,true);
            invalidate();//redibuja
        }
    }

    public void escalarImagen90(){
        if(imagenOriginal != null){
            int ancho=imagenOriginal.getWidth();
            int alto=imagenOriginal.getHeight();

            //escalamos a un 9% su tamaño original
            int nuevoAncho=(int)(ancho*0.9);
            int nuevoAlto= (int)(alto*0.9);



            imagenDeFondo=Bitmap.createScaledBitmap(imagenOriginal,nuevoAncho,nuevoAlto,true);
            invalidate();//redibuja
        }
    }

    public void escalarImagen50(){
        if(imagenOriginal != null){
            int ancho=imagenOriginal.getWidth();
            int alto=imagenOriginal.getHeight();

            //escalamos a un 5% su tamaño original
            int nuevoAncho=(int)(ancho*0.5);
            int nuevoAlto= (int)(alto*0.5);



            imagenDeFondo=Bitmap.createScaledBitmap(imagenOriginal,nuevoAncho,nuevoAlto,true);
            invalidate();//redibuja
        }



    }

    public void ajustarOpacidad(int nuevoValor){
        opacidadActual = nuevoValor;
        paintImagenFondo.setAlpha(opacidadActual);
        invalidate();
    }



    public Bitmap cambiarOpacidad(Bitmap original, int alpha){
        Bitmap mutable=original.copy(Bitmap.Config.ARGB_8888,true);
        Canvas canvas= new Canvas(mutable);
        Paint paint= new Paint();
        paint.setAlpha(alpha);
        canvas.drawBitmap(mutable,0,0,paint);
        return mutable;
    }


    public int getOpacidadActual() {
        return  opacidadActual;
    }

    public  void setTamañoPincel(float nuevoTamaño){
        pincel.setStrokeWidth(nuevoTamaño);
        invalidate();
    }
    //toma el bitmap actul y lo escala al 50% de alto y ancho originales

}



