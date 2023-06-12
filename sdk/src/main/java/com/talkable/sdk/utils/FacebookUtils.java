package com.talkable.sdk.utils;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;
import com.talkable.sdk.Talkable;
import com.talkable.sdk.TalkableOfferFragment;
import com.talkable.sdk.models.SharingChannel;

public class FacebookUtils {
    private static CallbackManager callbackManager;

    public static void initialize() {
        if (callbackManager != null) {
            return;
        }

        callbackManager = CallbackManager.Factory.create();
    }

    public static void share(final TalkableOfferFragment fragment, String url) {
        ShareDialog shareDialog = new ShareDialog(fragment);
        registerCallback(shareDialog, fragment, SharingChannel.FACEBOOK.toString());
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            showDialog(shareDialog, url);
        }
    }

    public static void shareViaMessenger(final TalkableOfferFragment fragment, String url) {
        MessageDialog messageDialog = new MessageDialog(fragment);
        registerCallback(messageDialog, fragment, SharingChannel.FACEBOOK_MESSAGE.toString());
        if (MessageDialog.canShow(ShareLinkContent.class)) {
            showDialog(messageDialog, url);
        }
    }

    public static boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return callbackManager != null && callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private static ShareLinkContent shareContent(String url) {
        if (url == null) {
            return null;
        }
        return new ShareLinkContent.Builder().setContentUrl(Uri.parse(url)).build();
    }

    private static void registerCallback(ShareDialog dialog, final TalkableOfferFragment fragment, final String sharingChannel) {
        if (callbackManager == null) {
            return;
        }

        dialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                fragment.shareSucceeded(sharingChannel);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(Talkable.TAG, error.toString());
            }
        });
    }

    private static void showDialog(ShareDialog dialog, String url) {
        dialog.show(shareContent(url));
    }
}
