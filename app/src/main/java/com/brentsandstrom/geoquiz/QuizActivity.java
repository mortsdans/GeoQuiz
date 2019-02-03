package com.brentsandstrom.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    //Views
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mCheatButton;
    private Button mPrevButton;
    private TextView mQuestionTextView;

    //Constants
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_CHEATS_USED = "cheats_used";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };

    //Other
    private int mCurrentIndex = 0;
    private boolean mIsCheater;
    private int mCheatsAllowed = 3;
    private int mCheatsUsed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Test commit
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        //Get views before loading saved state
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);
        mCheatButton = (Button) findViewById(R.id.cheat_button);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mCheatsUsed = savedInstanceState.getInt(KEY_CHEATS_USED, 0);
            if(mCheatsUsed>=mCheatsAllowed) mCheatButton.setEnabled(false);
        }

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentIndex == 0) {
                    mCurrentIndex = mQuestionBank.length;
                }
                mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(QuizActivity.this, CheatActivity.class);
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT); 
            }
        });

        updateQuestion();
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        if ( resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
            if(mIsCheater) {
                mCheatsUsed++;
                if(mCheatsUsed >= mCheatsAllowed){
                    mCheatButton.setEnabled(false);
                    Toast.makeText(this, R.string.no_more_cheats_toast, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        //set the answer for selected question
        mQuestionBank[mCurrentIndex].setUserAnswer(userPressedTrue);

        int messageResId;

        if(mIsCheater) {
            messageResId = R.string.judgement_toast;
        }
        else {
            if (mQuestionBank[mCurrentIndex].getUserAnswerCorrect()) {
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        updateQuestion();
        checkScore();
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

        mTrueButton.setEnabled(!mQuestionBank[mCurrentIndex].getHasAnswer());
        mFalseButton.setEnabled(!mQuestionBank[mCurrentIndex].getHasAnswer());
    }

    private void checkScore() {
        int mCorrectAnswers = 0;
        for(Question q : mQuestionBank){
            if(!q.getHasAnswer()){
                break;
            }
            else {
                if(q.getUserAnswerCorrect()){
                    mCorrectAnswers++;
                }
            }
        }
        Toast.makeText(QuizActivity.this, "Your score is " + mCorrectAnswers + " out of " + mQuestionBank.length, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putInt(KEY_CHEATS_USED, mCheatsUsed);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

}
