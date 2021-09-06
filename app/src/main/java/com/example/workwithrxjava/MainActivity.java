package com.example.workwithrxjava;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Observable<String> animalObservable = Observable.fromArray(
                "Ant", "Ape",
                "Bat", "Bee", "Bear", "Butterfly",
                "Cat", "Crab", "Cod",
                "Dog", "Dove",
                "Fox", "Frog");

        DisposableObserver<String> animalDisposableObserver = getAnimalObserver();
        DisposableObserver<String> animalsObserverAllCaps = getAnimalsObserverAllCaps();

        // make Observer a member of the Observable till it can receive the data
        compositeDisposable.add(
                animalObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .filter(s -> s.toLowerCase().startsWith("b"))
                        .subscribeWith(animalDisposableObserver));

        compositeDisposable.add(
                animalObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                .filter(s -> s.toLowerCase().startsWith("c"))
                .map(s -> s.toUpperCase())
                .subscribeWith(animalsObserverAllCaps)
        );
    }


    // observe the data which is emitted from DisposableObservable
    private DisposableObserver<String> getAnimalObserver() {
        return new DisposableObserver<String>() {
            @Override
            public void onNext(@NonNull String s) {
                Log.d(TAG, "Name: " + s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All items are emitted!");
            }
        };
    }

    private DisposableObserver<String> getAnimalsObserverAllCaps() {
        return new DisposableObserver<String>() {
            @Override
            public void onNext(@io.reactivex.annotations.NonNull String s) {
                Log.d(TAG, "Name: " + s);
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All items are emitted!");
            }
        };
    }

    @Override
    protected void onDestroy() {
        // don't send events once the activity is destroyed
        compositeDisposable.clear();
        super.onDestroy();
    }
}