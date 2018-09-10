package com.example.cloverexamplego.fragments;

import com.clover.remote.client.Constants;
import com.clover.remote.client.clovergo.ICloverGoConnector;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import com.example.cloverexamplego.R;
import com.example.cloverexamplego.utils.PreferenceUtil;

public class MiscFragment extends BaseFragment {
    public static final String TAG = "MiscFragment";
    public static final String PREF_450 = "PREF_450";

    private ICloverGoConnector mCloverConnector;

    private Switch mManualSwitch;
    private Switch mSwipeSwitch;
    private Switch mChipSwitch;
    private Switch mContactlessSwitch;

    public static MiscFragment newInstance() {
        return new MiscFragment();
    }

    public static MiscFragment newInstance(ICloverGoConnector connector) {
        MiscFragment fragment = new MiscFragment();
        fragment.mCloverConnector = connector;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_misc, container, false);

        String preferred450 = PreferenceUtil.getStringValue(getPOSActivity(), PREF_450);
        EditText pref450ReaderEditTxt = view.findViewById(R.id.reader450PrefEditTxt);
        pref450ReaderEditTxt.setText(preferred450 == null ? "" : preferred450);
        pref450ReaderEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                PreferenceUtil.saveString(getPOSActivity(), PREF_450, editable.toString());
            }
        });


        view.findViewById(R.id.connectReader450Btn).setOnClickListener(view1 -> getPOSActivity().connect450Click());
        view.findViewById(R.id.disconnectReaderBtn).setOnClickListener(view1 -> getPOSActivity().disconnect450());
        view.findViewById(R.id.connectReader350Btn).setOnClickListener(view1 -> getPOSActivity().connect350Click());
        view.findViewById(R.id.getLogsBtn).setOnClickListener(v -> getPOSActivity().getLogs());

        mManualSwitch = view.findViewById(R.id.ManualSwitch);
        mSwipeSwitch = view.findViewById(R.id.SwipeSwitch);
        mChipSwitch = view.findViewById(R.id.ChipSwitch);
        mContactlessSwitch = view.findViewById(R.id.ContactlessSwitch);

        mManualSwitch.setTag(Constants.CARD_ENTRY_METHOD_MANUAL);
        mSwipeSwitch.setTag(Constants.CARD_ENTRY_METHOD_MAG_STRIPE);
        mChipSwitch.setTag(Constants.CARD_ENTRY_METHOD_ICC_CONTACT);
        mContactlessSwitch.setTag(Constants.CARD_ENTRY_METHOD_NFC_CONTACTLESS);

        CompoundButton.OnCheckedChangeListener changeListener = (buttonView, isChecked) -> getPOSActivity().setCloverGoCardEntryMethodState(getCardEntryMethodStates());

        mManualSwitch.setOnCheckedChangeListener(changeListener);
        mSwipeSwitch.setOnCheckedChangeListener(changeListener);
        mChipSwitch.setOnCheckedChangeListener(changeListener);
        mContactlessSwitch.setOnCheckedChangeListener(changeListener);

        updateTransactionTypeSwitches();

        return view;
    }

    private int getCardEntryMethodStates() {
        int val = 0;
        val |= mManualSwitch.isChecked() ? (Integer) mManualSwitch.getTag() : 0;
        val |= mSwipeSwitch.isChecked() ? (Integer) mSwipeSwitch.getTag() : 0;
        val |= mChipSwitch.isChecked() ? (Integer) mChipSwitch.getTag() : 0;
        val |= mContactlessSwitch.isChecked() ? (Integer) mContactlessSwitch.getTag() : 0;

        return val;
    }

    private void updateTransactionTypeSwitches() {
        int cardEntryMethod = getPOSActivity().getCloverGoCardEntryMethodState();

        boolean manualSetting = (cardEntryMethod & Constants.CARD_ENTRY_METHOD_MANUAL) == Constants.CARD_ENTRY_METHOD_MANUAL;
        boolean contactlessSetting = (cardEntryMethod & Constants.CARD_ENTRY_METHOD_NFC_CONTACTLESS) == Constants.CARD_ENTRY_METHOD_NFC_CONTACTLESS;
        boolean contactSetting = (cardEntryMethod & Constants.CARD_ENTRY_METHOD_ICC_CONTACT) == Constants.CARD_ENTRY_METHOD_ICC_CONTACT;
        boolean swipeSetting = (cardEntryMethod & Constants.CARD_ENTRY_METHOD_MAG_STRIPE) == Constants.CARD_ENTRY_METHOD_MAG_STRIPE;

        mManualSwitch.setChecked(manualSetting);
        mContactlessSwitch.setChecked(contactlessSetting);
        mChipSwitch.setChecked(contactSetting);
        mSwipeSwitch.setChecked(swipeSetting);
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}