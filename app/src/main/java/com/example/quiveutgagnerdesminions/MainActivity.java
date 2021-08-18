package com.example.quiveutgagnerdesminions;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import model.User;

public class MainActivity extends AppCompatActivity {
    private TextView mUserNameText; // variables qui seront reliées au texte de bienvenue défini dans activity_main.xml
    private EditText mNameInput;    // pareil, pour le champ de texte 'nom'
    private Button mPlayButton;     // pareil, pour le bouton
    private User mUser; // utilisateur qui joue

    private static final int GAME_ACTIVITY_REQUEST_CODE = 11;

    private SharedPreferences mPreferences; // instance de SharedPreferences
    public static final String PREF_KEY_SCORE = "PREF_KEY_SCORE";
    public static final String PREF_KEY_FIRSTNAME = "PREF_KEY_FIRSTNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("MainActivity :: onCreate()");
        setContentView(R.layout.activity_main); // lien avec activity_main.xml

        mUserNameText = (TextView) findViewById(R.id.activity_main_user_name); // Ici on relie la variable plus haut au champ correspondant défini dans activity_main.xml
        mNameInput = (EditText) findViewById(R.id.activity_main_name_input);
        mPlayButton = (Button) findViewById(R.id.activity_main_play_btn);

        mUser = new User(); // on crée l'user pour pouvoir l'utiliser

        mPreferences = getPreferences(MODE_PRIVATE); // accès à l'instance de SharedPreferences (MODE_PRIVATE pour empecher l'accès aux autres appli)

        mPlayButton.setEnabled(false); // on cache le boutton par défaut

        greetUser();

        mNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {} // sert à rien mais doit être implémentée

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPlayButton.setEnabled(s.toString().length() != 0); // on affiche le bouton seulement quand au moins une lettre est tapée
            }

            @Override
            public void afterTextChanged(Editable s) {} // sert à rien mais doit être implémentée
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // s'active au clique sur le bouton
                mUser.setFirstName(mNameInput.getText().toString()); // on stock dans mUser (modele) le nom tapé (pseudo du joueur)
                mPreferences.edit().putString(PREF_KEY_FIRSTNAME, mUser.getFirstName()).apply(); // on récupère le nom (du modele, mUser), & le stock dans fichier avec clé : "pref_key_firstname"
                Intent gameActivity = new Intent(MainActivity.this, GameActivity.class);
                startActivityForResult(gameActivity, GAME_ACTIVITY_REQUEST_CODE); // ces 2 lignes servent à diriger vers l'activité gameActivity (grâce au clic), d'où l'on attend un résultat
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // pour récupérer le résultat envoyé par une activité
        super.onActivityResult(requestCode, resultCode, data);
        if (GAME_ACTIVITY_REQUEST_CODE == requestCode && RESULT_OK == resultCode) { // on vérifie que le résultat vient de la bonne act (ici il n'y en a qu'une) && que l'act s'est bien terminée
            int score = data.getIntExtra(GameActivity.BUNDLE_EXTRA_SCORE, 0); // on récupère le score grâce à getIntExtra
            mPreferences.edit().putInt(PREF_KEY_SCORE, score).apply(); // on récupère le score, & le stock dans fichier avec clé : "PREF_KEY_SCORE"
            greetUser();
        }
    }

    private void greetUser() { // vérifie si le joueur a déjà joué, et affiche son score précédent en conséquence
        String firstname = mPreferences.getString(PREF_KEY_FIRSTNAME, null);

        if (null != firstname) {  // vérifie si une partie a déjà eu lieu
            int score = mPreferences.getInt(PREF_KEY_SCORE, 0);

            String fulltext = "Bon retour par minions, " + firstname
                    + " !\nVous avez déjà gagné " + score
                    + " minions, en gagnerez-vous plus cette fois ? BANANA'llons-y !!";
            mUserNameText.setText(fulltext); // maj texte accueil
            mNameInput.setText(firstname); // rajoute automatiquement le pseudo du joueur pour gagner du temps
            mNameInput.setSelection(firstname.length()); // positionne le curseur à la fin du nom
            mPlayButton.setEnabled(true); // affiche le bouton direct, (nom déjà rempli, mais se fait normalement slmt quand du texte est tapé)
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("MainActivity :: onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("MainActivity :: onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("MainActivity :: onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("MainActivity :: onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("MainActivity :: onDestroy()");
    }

}