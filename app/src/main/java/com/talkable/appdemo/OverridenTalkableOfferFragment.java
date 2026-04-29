package com.talkable.appdemo;


import android.widget.Toast;

import com.talkable.sdk.TalkableOfferFragment;

public class OverridenTalkableOfferFragment extends TalkableOfferFragment {

    @Override
    public void copyToClipboard(String string) {
        super.copyToClipboard(string);
        Toast.makeText(getActivity(), "Text copied!", Toast.LENGTH_LONG).show();
    }
}
