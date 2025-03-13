package com.example.final_project_prm392.Repository;

import androidx.annotation.NonNull;

import com.example.final_project_prm392.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository {
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private final String COLLECTION_NAME = "users";

    public UserRepository() {
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }

    public interface UserCallback {
        void onSuccess(User user);

        void onFailure(Exception e);
    }

    public interface OperationCallback {
        void onSuccess();

        void onFailure(Exception e);
    }

    public void registerUser(String email, String password, String name, String phone, UserCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();
                                User user = new User(userId, name, email, phone, "", "");

                                firestore.collection(COLLECTION_NAME).document(userId)
                                        .set(user)
                                        .addOnSuccessListener(aVoid -> callback.onSuccess(user))
                                        .addOnFailureListener(callback::onFailure);
                            } else {
                                callback.onFailure(new Exception("Failed to get user ID"));
                            }
                        } else {
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    public void loginUser(String email, String password, UserCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();

                                firestore.collection(COLLECTION_NAME).document(userId)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        User user = document.toObject(User.class);
                                                        callback.onSuccess(user);
                                                    } else {
                                                        callback.onFailure(new Exception("User data not found"));
                                                    }
                                                } else {
                                                    callback.onFailure(task.getException());
                                                }
                                            }
                                        });
                            } else {
                                callback.onFailure(new Exception("Failed to get user ID"));
                            }
                        } else {
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    public void getCurrentUser(UserCallback callback) {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            firestore.collection(COLLECTION_NAME).document(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    User user = document.toObject(User.class);
                                    callback.onSuccess(user);
                                } else {
                                    callback.onSuccess(null);
                                }
                            } else {
                                callback.onFailure(task.getException());
                            }
                        }
                    });
        } else {
            callback.onSuccess(null);
        }
    }

    public void updateUserProfile(User user, OperationCallback callback) {
        firestore.collection(COLLECTION_NAME).document(user.getId())
                .set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void signOut() {
        auth.signOut();
    }
}

