package com.talkable.sdk;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.util.Log;

import com.talkable.sdk.TalkableOfferFragment.TalkableOfferFragmentListener;

public class TalkableActivity extends FragmentActivity implements TalkableOfferFragmentListener {
    private static String FRAGMENT_TAG = "OfferFragment";

    private TalkableOfferFragment mTalkableOfferFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talkable);

        mTalkableOfferFragment = getFragment();
    }

    protected TalkableOfferFragment getFragment() {
        FragmentManager manager = getSupportFragmentManager();
        TalkableOfferFragment fragment = (TalkableOfferFragment) manager.findFragmentByTag(FRAGMENT_TAG);

        if (fragment == null) {
            String offerCode = getIntent().getStringExtra(Talkable.ARG_OFFER_CODE);
            if (offerCode == null) {
                throw new IllegalStateException("Offer should be provided. Make sure " +
                        "you pass an offer to the activity as Talkable.ARG_OFFER extra via intent");
            }
            Bundle arguments = new Bundle();
            arguments.putString(TalkableOfferFragment.OFFER_CODE_PARAM, offerCode);

            String className = getIntent().getStringExtra(Talkable.ARG_OFFER_FRAGMENT_CLASS);
            fragment = createFragmentInstance(className);
            fragment.setArguments(arguments);
            fragment.setRetainInstance(true);

            manager.beginTransaction()
                    .add(R.id.fragment_talkable, fragment, FRAGMENT_TAG)
                    .commit();
        }
        return fragment;
    }

    @Override
    public void onBackPressed() {
        if (!mTalkableOfferFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onOfferClosed() {
        finish();
    }

    private TalkableOfferFragment createFragmentInstance(String className) {
        if (className == null) {
            return new TalkableOfferFragment();
        }

        TalkableOfferFragment fragment = null;
        try {
            fragment = (TalkableOfferFragment) Class.forName(className).newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InstantiationException e) {
            Log.d(Talkable.TAG, "Offer instantiation error: " + e.getMessage());
        } catch (IllegalAccessException e) {
            Log.d(Talkable.TAG, "Offer instantiation error: " + e.getMessage());
        }

        return fragment;
    }
}
