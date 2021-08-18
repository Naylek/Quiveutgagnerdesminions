package model;

import java.util.Collections;
import java.util.List;

public class QuestionBank {
    private List<Question> mQuestionList; // liste de questions
    private int mNextQuestionIndex;       // index de question

    public QuestionBank(List<Question> questionList) { // constructeur, mélange la liste de question quand il la récupère
        mQuestionList = questionList;
        Collections.shuffle(mQuestionList);
        mNextQuestionIndex = 0; // on se positionne sur la 1ère question de la liste
    }

    public Question getQuestion() { // prendre une question au hasard
        if (mNextQuestionIndex == mQuestionList.size()) { // On s'assure qu'une fois arrivé à la fin de la liste, on revient au début
            mNextQuestionIndex = 0;
        }

        return mQuestionList.get(mNextQuestionIndex++);
    }
}
