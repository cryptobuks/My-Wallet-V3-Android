package piuk.blockchain.android.ui.contacts;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import javax.inject.Inject;

import piuk.blockchain.android.R;
import piuk.blockchain.android.data.datamanagers.MetaDataManager;
import piuk.blockchain.android.injection.Injector;
import piuk.blockchain.android.ui.base.BaseViewModel;
import piuk.blockchain.android.ui.customviews.ToastCustom;
import piuk.blockchain.android.util.AppUtil;

import static piuk.blockchain.android.ui.contacts.ContactPairingMethodActivity.INTENT_KEY_CONTACT_NAME;

public class ContactPairingMethodViewModel extends BaseViewModel {

    private DataListener dataListener;
    private String contactName;
    @Inject AppUtil appUtil;
    @Inject MetaDataManager metaDataManager;

    interface DataListener {

        Intent getPageIntent();

        void onShowToast(@StringRes int message, @ToastCustom.ToastType String toastType);

        void finishActivityWithResult(int resultCode);

    }

    ContactPairingMethodViewModel(DataListener dataListener) {
        Injector.getInstance().getDataManagerComponent().inject(this);
        this.dataListener = dataListener;
    }

    @Override
    public void onViewReady() {
        Intent pageIntent = dataListener.getPageIntent();
        if (pageIntent != null && pageIntent.hasExtra(INTENT_KEY_CONTACT_NAME)) {
            // Don't need contact name in all flows to this page
            contactName = pageIntent.getStringExtra(INTENT_KEY_CONTACT_NAME);
        }
    }

    void handleScanInput(@NonNull String extra) {
        // TODO: 15/11/2016 Input validation?

        // TODO: 30/11/2016 We're supposed to do something here with a contact name, but I have no idea what

        compositeDisposable.add(
                metaDataManager.acceptInvitation(extra)
                        .flatMap(share -> metaDataManager.putTrusted(share.getMdid()))
                        .subscribe(
                                success -> {
                                    dataListener.onShowToast(R.string.remote_save_ok, ToastCustom.TYPE_OK);
                                    dataListener.finishActivityWithResult(Activity.RESULT_OK);
                                }, throwable -> dataListener.onShowToast(R.string.pairing_failed, ToastCustom.TYPE_ERROR)));
    }

    void onSendLinkClicked() {
        // TODO: 14/11/2016 Generate link and share
    }

    void onNfcClicked() {
        // TODO: 14/11/2016 Generate link and share via NFC
        /**
         * Although to be honest, using a URI for sharing will allow NFC use anyway, but it might
         * be good to make the option explicit?
         */
    }

    boolean isCameraOpen() {
        return appUtil.isCameraOpen();
    }

}