package com.sgc.tictactoe.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sgc.tictactoe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sgc.tictactoe.util.ConverterExceptionToErrorMessage.errorEntryProcessing;
import static com.sgc.tictactoe.util.ValidateUtil.validateEditTexts;

public class EntryActivity extends BaseActivity implements
        View.OnClickListener {

    @BindView(R.id.emailSignInButton)
    Button emailSignInButton;
    @BindView(R.id.create)
    TextView create;

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        ButterKnife.bind(this);
        emailSignInButton.setOnClickListener(this);
        create.setOnClickListener(this);
        errorInfo.setOnClickListener(v -> errorInfo.setVisibility(View.GONE));
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signIn(String email, String password) {
        if (!validateEditTexts(new EditText[]{fieldEmail,fieldPassword})) {
            return;
        }
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        showNetworkError(errorEntryProcessing(task));
                        updateUI(null);
                    }
                    hideProgressDialog();
                });
    }


    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emailSignInButton) {
            signIn(fieldEmail.getText().toString(), fieldPassword.getText().toString());
        } else if (i == R.id.create) {
            startActivity(new Intent(this, RegistrationActivity.class));
        }
    }
}
