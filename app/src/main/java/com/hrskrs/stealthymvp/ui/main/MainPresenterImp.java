package com.hrskrs.stealthymvp.ui.main;

import com.hrskrs.stealthymvp.data.DataManger;
import com.hrskrs.stealthymvp.model.Profile;
import com.hrskrs.stealthymvp.ui.base.BasePresenterImp;
import com.hrskrs.stealthymvp.util.rx.scheduler.SchedulerProvider;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by hrskrs on 5/24/2017.
 */

public class MainPresenterImp<V extends MainView>
    extends BasePresenterImp<V> implements MainPresenter<V> {

  @Inject
  protected MainPresenterImp(DataManger dataManger,
                             SchedulerProvider schedulerProvider,
                             CompositeDisposable compositeDisposable) {
    super(dataManger, schedulerProvider, compositeDisposable);
  }

  @Override
  public void getProfiles() {
    if (getView().isNetworkConnected()) {
      getView().showLoading();
      Disposable disposable = getDataManger().getProfiles()
          .subscribeOn(getSchedulerProvider().io())
          .observeOn(getSchedulerProvider().ui())
          .subscribeWith(new DisposableObserver<ArrayList<Profile>>() {
            @Override
            public void onNext(ArrayList<Profile> profiles) {
              if (!isViewAttached()) {
                return;
              }
              getView().onProfilesLoaded(profiles);
              getView().hideLoading();
            }

            @Override
            public void onError(Throwable e) {
              getView().hideLoading();
              if (!isViewAttached()) {
                return;
              }
              getView().onUnknownError(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
          });
      getCompositeDisposable().add(disposable);
    } else {
      getView().onConnectionError();
    }
  }
}
