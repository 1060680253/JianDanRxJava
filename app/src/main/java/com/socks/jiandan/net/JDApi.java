package com.socks.jiandan.net;

import com.socks.jiandan.callback.LoadFinishCallBack;
import com.socks.jiandan.model.Comment4FreshNews;
import com.socks.jiandan.model.Commentator;
import com.socks.jiandan.model.FreshNews;
import com.socks.jiandan.net.parser.CommentListParser;
import com.socks.jiandan.net.parser.FreshNewsCommentParser;
import com.socks.jiandan.net.parser.FreshNewsDetailParser;
import com.socks.jiandan.net.parser.FreshNewsParser;
import com.socks.jiandan.net.parser.Push4FreshCommentParser;
import com.socks.jiandan.net.parser.PushCommentParser;
import com.socks.okhttp.plus.OkHttpProxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhaokaiqiang on 15/12/23.
 */
public class JDApi {

    public static Observable<Boolean> pushComment4DuoShuo(Map<String, String> params) {

        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    subscriber.onNext(new PushCommentParser().parse(OkHttpProxy.post().url(Commentator.URL_PUSH_COMMENT).setParams(params).execute()));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).compose(applySchedulers());
    }


    public static Observable<Boolean> pushComment4FreshNews(String url) {

        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    subscriber.onNext(new Push4FreshCommentParser().parse(OkHttpProxy.get().url(url).execute()));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).compose(applySchedulers());
    }


    public static Observable<ArrayList<Comment4FreshNews>> getCommentator4FreshNews(final String thread_key, LoadFinishCallBack callBack) {

        return Observable.create(new Observable.OnSubscribe<ArrayList<Comment4FreshNews>>() {

            @Override
            public void call(Subscriber<? super ArrayList<Comment4FreshNews>> subscriber) {
                String url = Comment4FreshNews.getUrlComments(thread_key);
                try {
                    subscriber.onNext(new FreshNewsCommentParser(callBack).parse(OkHttpProxy.get()
                            .url(url)
                            .execute()));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).compose(applySchedulers());
    }


    public static Observable<ArrayList<Commentator>> getCommentator(final String thread_key, LoadFinishCallBack callBack) {

        return Observable.create(new Observable.OnSubscribe<ArrayList<Commentator>>() {

            @Override
            public void call(Subscriber<? super ArrayList<Commentator>> subscriber) {
                String url = Commentator.getUrlCommentList(thread_key);
                try {
                    subscriber.onNext(new CommentListParser(callBack).parse(OkHttpProxy.get()
                            .url(url)
                            .execute()));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).compose(applySchedulers());
    }


    public static Observable<ArrayList<FreshNews>> getFreshNews(final int page) {

        return Observable.create(new Observable.OnSubscribe<ArrayList<FreshNews>>() {

            @Override
            public void call(Subscriber<? super ArrayList<FreshNews>> subscriber) {
                String url = FreshNews.getUrlFreshNews(page);
                try {
                    subscriber.onNext(new FreshNewsParser().parse(OkHttpProxy.get()
                            .url(url)
                            .execute()));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).compose(JDApi.<ArrayList<FreshNews>>applySchedulers());
    }


    public static Observable<String> getFreshDetail(String id) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String url = FreshNews.getUrlFreshNewsDetail(id);
                try {
                    subscriber.onNext(new FreshNewsDetailParser().parse(OkHttpProxy.get().url(url).execute()));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).compose(applySchedulers());
    }

    private static <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}