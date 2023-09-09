package com.example.prototip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
/**
 * Clasa MainActivity reprezintă activitatea principală a aplicației și gestionează afișarea ecranului de pornire (splash screen).
 */
public class MainActivity extends AppCompatActivity {

    // Durata afisarii ecranului de pornire in milisecunde
    private static int splashScreenTimeout = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Ascunde bara de notificari si bara de actiuni pentru a afisa ecranul de pornire pe tot ecranul
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Seteaza continutul activitatii cu layout-ul definit in activity_main.xml
        setContentView(R.layout.activity_main);

        //Creaza o animatie de fade-out pentru ecranul de pornire
        Animation fadeOut = new AlphaAnimation(1,0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(500);
        fadeOut.setDuration(1800);

        // Dupa expirarea timpului de afisare a ecranului de pornire, navigheaza catre activitatea SignupActivity
        new Handler().postDelayed(()-> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
            finish(); // Inchide activitatea curenta pentru a preveni revenirea la ecranul de pornire
        },splashScreenTimeout);
    }
}
