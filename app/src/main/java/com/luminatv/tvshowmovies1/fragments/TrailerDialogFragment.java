package com.luminatv.tvshowmovies1.fragments;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import com.luminatv.tvshowmovies1.R;

/* JADX INFO: loaded from: classes.dex */
public class TrailerDialogFragment extends DialogFragment {
    private static final String ARG_VIDEO_KEY = "video_key";
    private static final String ARG_VIDEO_NAME = "video_name";
    private WebView webView;

    public static TrailerDialogFragment newInstance(String str, String str2) {
        TrailerDialogFragment trailerDialogFragment = new TrailerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_VIDEO_KEY, str);
        bundle.putString(ARG_VIDEO_NAME, str2);
        trailerDialogFragment.setArguments(bundle);
        return trailerDialogFragment;
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStyle(0, R.style.Theme_HDHub4U_FullScreenDialog);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Dialog dialogOnCreateDialog = super.onCreateDialog(bundle);
        Window window = dialogOnCreateDialog.getWindow();
        if (window != null) {
            window.setFlags(1024, 1024);
            window.setBackgroundDrawable(new ColorDrawable(ViewCompat.MEASURED_STATE_MASK));
            window.addFlags(128);
        }
        return dialogOnCreateDialog;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.dialog_trailer, viewGroup, false);
        this.webView = (WebView) viewInflate.findViewById(R.id.webViewTrailer);
        ImageView imageView = (ImageView) viewInflate.findViewById(R.id.btnCloseTrailer);
        String string = getArguments() != null ? getArguments().getString(ARG_VIDEO_KEY, "") : "";
        if (string == null || string.isEmpty()) {
            Toast.makeText(requireContext(), "No trailer available", Toast.LENGTH_SHORT).show();
            dismiss();
            return viewInflate;
        }
        WebSettings settings = this.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(-1);
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");
        this.webView.setWebChromeClient(new WebChromeClient());
        this.webView.setWebViewClient(new WebViewClient() { // from class: com.freewatching.magistv4.fragments.TrailerDialogFragment.1
            @Override // android.webkit.WebViewClient
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                if (str == null) {
                    return false;
                }
                if (!str.contains("youtube.com") && !str.contains("youtu.be") && !str.contains("google.com")) {
                    return false;
                }
                webView.loadUrl(str);
                return true;
            }
        });
        this.webView.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        this.webView.loadUrl("https://www.youtube.com/watch?v=" + string);
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.freewatching.magistv4.fragments.TrailerDialogFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TrailerDialogFragment.this.m335x48b90990(view);
            }
        });
        return viewInflate;
    }

    /* JADX INFO: renamed from: lambda$onCreateView$0$com-freewatching-magistv4-fragments-TrailerDialogFragment, reason: not valid java name */
    /* synthetic */ void m335x48b90990(View view) {
        dismiss();
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog == null || dialog.getWindow() == null) {
            return;
        }
        dialog.getWindow().setLayout(-1, -1);
        dialog.getWindow().setWindowAnimations(android.R.style.Animation_Dialog);
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        WebView webView = this.webView;
        if (webView != null) {
            webView.loadUrl("about:blank");
            this.webView.destroy();
            this.webView = null;
        }
        super.onDestroyView();
    }
}
