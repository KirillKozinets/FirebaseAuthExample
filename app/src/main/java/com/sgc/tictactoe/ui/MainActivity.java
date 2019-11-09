package com.sgc.tictactoe.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sgc.tictactoe.R;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.sgc.tictactoe.util.ConverterExceptionToErrorMessage.errorChangeEmailProcessing;
import static com.sgc.tictactoe.util.ConverterExceptionToErrorMessage.errorChangePasswordProcessing;
import static com.sgc.tictactoe.util.ConverterExceptionToErrorMessage.errorEntryProcessing;
import static com.sgc.tictactoe.util.ValidateUtil.validateEditTexts;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @BindView(R.id.verEmailOk)
    ConstraintLayout verEmailOk;
    @BindView(R.id.verEmailCancel)
    ConstraintLayout verEmailCancel;
    @BindView(R.id.verEmailButton)
    Button verEmailButton;
    @BindView(R.id.changeLogin)
    Button changeLogin;
    @BindView(R.id.userLogin)
    TextView userLogin;
    @BindView(R.id.changeEmail)
    Button changeEmail;
    @BindView(R.id.changePassword)
    Button changePassword;

    @BindView(R.id.userEmail)
    TextView userEmail;

    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    FirebaseUser user;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        findViewById(R.id.signOutButton).setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        googleSignInClient = getGoogleSignInClient();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        showEmailInfo();
        showUserLogin();
    }

    private GoogleSignInClient getGoogleSignInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        return GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onBackPressed() {
        //Stub
    }


    private void showUserLogin() {
        Query mQueryRef = databaseReference.child("users").child(user.getUid());
        mQueryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String login = dataSnapshot.child("login").getValue(String.class);
                userLogin.setText(login);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void signOut() {
        auth.signOut();
        googleSignInClient.signOut();
        startActivity(new Intent(this, EntryActivity.class));
    }

    private void showEmailInfo() {
        user.reload();
        userEmail.setText(user.getEmail());

        if (!user.isEmailVerified()) {
            verEmailCancel.setVisibility(View.VISIBLE);
            verEmailOk.setVisibility(View.GONE);
        } else {
            verEmailCancel.setVisibility(View.GONE);
            verEmailOk.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.signOutButton) {
            signOut();
        }
    }

    @OnClick(R.id.verEmailButton)
    void verificationEmail() {
        showEmailInfo();

        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !user.isEmailVerified()) {
                Toast.makeText(this, "На вашу электронную почту " +
                        "было отправлено письмо, для подтвержэения " +
                        "перейдите по ссылке указанной в письме", Toast.LENGTH_LONG).show();
            }
        });
    }

    EditText editText;

    private AlertDialog.Builder createEditTextDialog(String title, String hint) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        View view = View.inflate(this, R.layout.edit_text_dialog, null);
        editText = view.findViewById(R.id.edit);
        editText.setHint(hint);
        alert.setView(view);
        return alert;
    }

    EditText oldEmail;
    EditText password;
    EditText newValue;
    TextView error;

    private AlertDialog.Builder createReauthenticateTextDialog(String title, String hintNewValue) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setPositiveButton("OK", null);
        alert.setTitle(title);
        View view = View.inflate(this, R.layout.reauthenticate_dialog, null);

        oldEmail = view.findViewById(R.id.oldEmail);
        password = view.findViewById(R.id.password);
        newValue = view.findViewById(R.id.newValue);
        error = view.findViewById(R.id.errorInfo);

        newValue.setHint(hintNewValue);
        alert.setView(view);
        return alert;
    }

    @OnClick(R.id.changeLogin)
    public void changeLogin() {
        AlertDialog.Builder alert = createEditTextDialog("Смена логина", "login");
        alert.setPositiveButton("Ok", (dialog, whichButton) -> {
            if (editText != null) {
                String newLogin = editText.getText().toString();
                showNewLogin(newLogin);
            }
        });
        alert.show();
    }

    private void showNewLogin(String login) {
        if (!login.equals("")) {
            databaseReference.child("users").child(user.getUid()).child("login").setValue(login);
            user.reload();
            showUserLogin();
        }
    }

    @OnClick(R.id.changeEmail)
    public void changeEmail() {
        AlertDialog.Builder alert = createReauthenticateTextDialog("Смена электронной почты", "новая почта");
        AlertDialog alertDialog = alert.create();

        alertDialog.setOnShowListener(dialog -> {
            Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(view -> {
                if (oldEmail != null && password != null && newValue != null) {
                    String newEmail = newValue.getText().toString();
                    reauthenticateUser(oldEmail.getText().toString(), password.getText().toString());
                    updateEmail(newEmail, alertDialog);
                }
            });
        });

        alertDialog.show();
    }

    private void updateEmail(String newEmail, AlertDialog alertDialog) {
        if (validateEditTexts(new EditText[]{oldEmail, password, newValue})) {
            user.updateEmail(newEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showEmailInfo();
                    alertDialog.cancel();
                } else {
                    if (error.getText().toString().equals(""))
                        error.setText(errorChangeEmailProcessing(task));
                    error.setVisibility(View.VISIBLE);
                }
            });
        }
    }


    @OnClick(R.id.changePassword)
    public void changePassword() {
        AlertDialog.Builder alert = createReauthenticateTextDialog("Смена пароля", "новый пароль");
        AlertDialog alertDialog = alert.create();

        alertDialog.setOnShowListener(dialog -> {
            Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(view -> {
                if (oldEmail != null && password != null && newValue != null) {
                    String newPassword = newValue.getText().toString();
                    updatePassword(newPassword, alertDialog);
                }
            });
        });

        alertDialog.show();
    }

    private void updatePassword(String newPassword, AlertDialog alertDialog) {
        if (validateEditTexts(new EditText[]{oldEmail, password, newValue})) {
            reauthenticateUser(oldEmail.getText().toString(), password.getText().toString());
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful() && error.getText().toString().equals("")) {
                    alertDialog.cancel();
                } else {
                    if (error.getText().toString().equals(""))
                        error.setText(errorChangePasswordProcessing(task));
                    error.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void reauthenticateUser(String password, String email) {
        if (!password.equals("") && !email.equals("")) {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(password, email);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    error.setVisibility(View.GONE);
                    error.setText(errorEntryProcessing(task));
                }
            });
        }
    }

}
