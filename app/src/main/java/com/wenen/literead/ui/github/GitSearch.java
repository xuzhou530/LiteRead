package com.wenen.literead.ui.github;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.wenen.literead.R;
import com.wenen.literead.api.APIUrl;
import com.wenen.literead.http.HttpClient;
import com.wenen.literead.http.HttpSubscriber;
import com.wenen.literead.model.github.GithubLoginModel;
import com.wenen.literead.ui.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.Subscriber;

/**
 * Created by Wen_en on 16/9/6.
 */
public class GitSearch extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.indeterminate_horizontal_progress_toolbar)
    MaterialProgressBar indeterminateHorizontalProgressToolbar;
    @Bind(R.id.et_username)
    AppCompatEditText etUsername;
    @Bind(R.id.btn_login)
    AppCompatButton btnLogin;
    private String title;
    private String username;
    private Subscriber subscriber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_git_login);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            title = getIntent().getStringExtra("title");
        } else
            title = savedInstanceState.getString("title");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title", title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.mipmap.ic_action_arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @OnClick(R.id.btn_login)
    public void gitLogin() {
        username = etUsername.getText().toString();
        if ("".equals(username))
            showSnackBar(indeterminateHorizontalProgressToolbar, "账户名为空!", null);
        else
            githubLogin();
    }

    private void githubLogin() {
        subscriber = new HttpSubscriber<GithubLoginModel>(indeterminateHorizontalProgressToolbar) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                showSnackBar(indeterminateHorizontalProgressToolbar, e.toString(), null);
            }

            @Override
            public void onCompleted() {
                super.onCompleted();

            }

            @Override
            public void onNext(GithubLoginModel githubLoginModel) {
                super.onNext(githubLoginModel);
                updateGithubUserData(githubLoginModel);
                githubUser.setName(username);
                startActivity(new Intent(GitSearch.this, UserDetail.class));
                finish();
            }
        };
        HttpClient.getSingle(APIUrl.GITHUB_BASE_URL).GithubLogin(username, subscriber);
    }
}
