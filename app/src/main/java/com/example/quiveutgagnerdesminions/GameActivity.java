package com.example.quiveutgagnerdesminions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import model.Question;
import model.QuestionBank;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mQuestion; // champ texte question
    private Button mAns1; // boutons
    private Button mAns2;
    private Button mAns3;
    private Button mAns4;

    private QuestionBank mQuestionBank; // banque de questions
    private Question mCurrentQuestion; // question actuelle
    private int mNumberOfQuestions; // var representant nb de Q
    private int mScore; // score

    public static final String BUNDLE_EXTRA_SCORE = "BUNDLE_EXTRA_SCORE"; // "clé" liée au score du joueur, qui sera utilisée par MainActivity
    private boolean mEnableTouchEvents; // va servir à (des)activer les boutons temporairement
    public static final String BUNDLE_STATE_SCORE = "currentScore";
    public static final String BUNDLE_STATE_QUESTION = "currentQuestion";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("GameActivity :: onCreate()");
        setContentView(R.layout.activity_game); // lien avec le layout

        mQuestionBank = this.generateQuestions(); // création de la banque de question avec les Q de la fonction plus bas

        if (savedInstanceState != null) { // restore le score et le nb de question restantes si l'activité redémarre
            mScore = savedInstanceState.getInt(BUNDLE_STATE_SCORE);
            mNumberOfQuestions = savedInstanceState.getInt(BUNDLE_STATE_QUESTION);
        } else {
            mScore = 0;
            mNumberOfQuestions = 16;
        }

        mQuestion = (TextView) findViewById(R.id.activity_game_question_text); // association des mVar avec le xml
        mAns1 = (Button) findViewById(R.id.activity_game_answer1_btn);
        mAns2 = (Button) findViewById(R.id.activity_game_answer2_btn);
        mAns3 = (Button) findViewById(R.id.activity_game_answer3_btn);
        mAns4 = (Button) findViewById(R.id.activity_game_answer4_btn);

        mAns1.setTag(0); // assigne un id à chaque bouton pour les différencier
        mAns2.setTag(1);
        mAns3.setTag(2);
        mAns4.setTag(3);

        mAns1.setOnClickListener(this); // interception du clic sur le bouton grâce à la classe elle-même listener (this)
        mAns2.setOnClickListener(this);
        mAns3.setOnClickListener(this);
        mAns4.setOnClickListener(this);

        mCurrentQuestion = mQuestionBank.getQuestion(); // association de la Q courante avec une Q de la banque
        this.displayQuestion(mCurrentQuestion); // affichage de la question
        mEnableTouchEvents = true; // de base, quand on appuie sur l'écran ça marche
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {} // sert à rien mais doit être implémentée


    @Override
    public void onClick(View v) {
        int res = (int) v.getTag(); //la vue correspond au bouton cliqué grâce à getTag, res contient donc l'index du bouton cliqué par l'user
        if (this.mCurrentQuestion.getAnswerIndex() == res) { // on vérifie si res correspond à l'index de la bonne réponse
            Toast.makeText(this, "Bonne réponse !", Toast.LENGTH_SHORT).show(); // affichage grâce au toast
            mScore++;
        } else {
            Toast.makeText(this, "Mauvaise réponse !", Toast.LENGTH_SHORT).show();
        }

        mEnableTouchEvents = false; // on arrête les boutons tant que le toast n'a pas terminé de s'afficher

        new Handler().postDelayed(new Runnable() { // on diffère l'action pour attendre que le toast ai fini de s'afficher
            @Override
            public void run() { // code à exécuter après le temps d'attente
                mEnableTouchEvents = true;  // on relance les boutons, le toast a fini de s'afficher
                --mNumberOfQuestions; // à chaque clic, on décrémente pour arrêter le jeu quand on a épuisé le nb de Q
                if (mNumberOfQuestions == 0) {
                    endGame(); // fin de jeu
                } else {
                    mCurrentQuestion = mQuestionBank.getQuestion();
                    displayQuestion(mCurrentQuestion);
                }
            }
        }, 2000); // temps d'attente = 2 secondes = LENGTH_SHORT de Toast
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) { // appelé quand act stop, sauvegarde score & nb de Q restantes si l'act redémarre (changement orientation écran par ex)
        outState.putInt(BUNDLE_STATE_SCORE, mScore);
        outState.putInt(BUNDLE_STATE_QUESTION, mNumberOfQuestions);
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) { // sert à (des)activer les boutons temporairement
        return mEnableTouchEvents && super.dispatchTouchEvent(ev); // renvoie un booléen : true = action à faire, false = action à empêcher
    }


    public void endGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // builder "construit" la boite de dialogue

        builder.setTitle("Terminé !")
                .setMessage("Vous avez gagné " + mScore + " minions :)")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(); // création de l'intention
                        intent.putExtra(BUNDLE_EXTRA_SCORE, mScore); // association  du score et de son nom (bundle_extra_score) à l'intention
                        setResult(RESULT_OK, intent); // Activité se termine correctement, et on ajoute l'intention
                        finish(); // met fin à l'activité courante
                    }
                })
                .create()
                .show();
    }


    private void displayQuestion(final Question question) { // MAJ affichage, récupérer le texte de la question et des boutons (réponses)
        this.mQuestion.setText(question.getQuestion());
        this.mAns1.setText(question.getChoiceList().get(0));
        this.mAns2.setText(question.getChoiceList().get(1));
        this.mAns3.setText(question.getChoiceList().get(2));
        this.mAns4.setText(question.getChoiceList().get(3));
    }


    public QuestionBank generateQuestions() { // Sorte de BDD qui contient les questions, réponses, et index
        Question question1 = new Question("Qui est le créateur d'Android ?",
                Arrays.asList("Andy Rubin",
                        "Steve Wozniak",
                        "Jake Wharton",
                        "Paul Smith"),
                0);

        Question question2 = new Question("En quelle année le premier homme à avoir marché sur la lune l'a-t-il fait ?",
                Arrays.asList("1958",
                        "1962",
                        "1967",
                        "1969"),
                3);

        Question question3 = new Question("Quel est le numéro de la maison des Simpsons ?",
                Arrays.asList("42",
                        "101",
                        "666",
                        "742"),
                3);

        Question question4 = new Question("Quel organe est affecté lorsque quelqu'un souffre d'une hépatite?",
                Arrays.asList("Coeur",
                        "Poumon",
                        "Foie",
                        "Rein"),
                2);

        Question question5 = new Question("Quelle planète est la plus proche du soleil ?",
                Arrays.asList("Venus",
                        "Mercure",
                        "Terre",
                        "Saturne"),
                1);

        Question question6 = new Question("Quelles sont les 3 couleurs primaires ?",
                Arrays.asList("Bleu, jaune, vert",
                        "Orange, rouge, violet",
                        "Rouge, orange, jaune",
                        "Bleu, jaune, rouge"),
                3);

        Question question7 = new Question("Quel animal possède 3 cœurs ?",
                Arrays.asList("Cheval",
                        "Pieuvre",
                        "Calamar",
                        "Zèbre"),
                1);

        Question question8 = new Question("Quel animal est le plus grand prédateur ?",
                Arrays.asList("Ours polaire",
                        "Baleine",
                        "Lion",
                        "Panthère"),
                0);

        Question question9 = new Question("En quelle année Google a été lancé sur le web ?",
                Arrays.asList("1995",
                        "1998",
                        "2000",
                        "2002"),
                1);

        Question question10 = new Question("A quoi correspond l'acronyme RAM ?",
                Arrays.asList("Real Access Memory",
                        "Remote Authorization Mechanism",
                        "Random Access Memory",
                        "Readily Accessed Mailer"),
                2);

        Question question11 = new Question("Combien d'étoiles le drapeau américain possède-t-il ?",
                Arrays.asList("40",
                        "45",
                        "50",
                        "55"),
                2);

        Question question12 = new Question("Quelle couleur obtient-on en mélangeant du blanc et du rouge ?",
                Arrays.asList("Rose",
                        "Violet",
                        "Orange",
                        "Magenta"),
                0);

        Question question13 = new Question("Quel chanteur pop a épousé Debbie Rowe ?",
                Arrays.asList("Kurt Cobain",
                        "Freddy Mercury",
                        "Prince",
                        "Michael Jackson"),
                3);

        Question question14 = new Question("Quel groupe célèbre fut autrefois appelé 'The Quarrymen' ?",
                Arrays.asList("Franz Ferdinand",
                        "Muse",
                        "The Beatles",
                        "Queen"),
                2);

        Question question15 = new Question("Quel est le premier livre de l'Ancien Testament ?",
                Arrays.asList("Exode",
                        "Genèse",
                        "Deutéronome",
                        "Lévitique"),
                1);

        Question question16 = new Question("Qui a peint Guernica ?",
                Arrays.asList("Picasso",
                        "Dali",
                        "Van Gogh",
                        "Warhol"),
                0);

        return new QuestionBank(Arrays.asList(question1,
                question2,
                question3,
                question4,
                question5,
                question6,
                question7,
                question8,
                question9,
                question10,
                question11,
                question12,
                question13,
                question14,
                question15,
                question16));
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("GameActivity :: onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("GameActivity :: onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("GameActivity :: onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("GameActivity :: onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("GameActivity :: onDestroy()");
    }

}