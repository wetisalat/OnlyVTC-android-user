package com.tranxit.stripepayment.activity.add_card;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.tranxit.stripepayment.R;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class StripeAddCardActivity extends AppCompatActivity {

    private static final String TAG = "StripePaymentActivity";
    private CardForm cardForm;

    private String stripe_token;
    public static final int MY_SCAN_REQUEST_CODE = 10238;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        cardForm = findViewById(R.id.card_form);
        stripe_token = getIntent().getStringExtra("stripe_token");

        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .actionLabel(getString(R.string.add_card_details))
                .setup(this);

        findViewById(R.id.submit).setOnClickListener(v -> onSubmit());
        findViewById(R.id.scan_card).setOnClickListener(v -> onClickScanCard());

    }

    @Override
    public boolean onSupportNavigateUp() {
//        onBackPressed();
        return false;
    }

    public void onSubmit() {

        if (cardForm.getCardNumber().isEmpty()) {
            Toast.makeText(this, getString(R.string.please_enter_card_number), Toast.LENGTH_SHORT).show();
            return;
        }
        if (cardForm.getExpirationMonth().isEmpty()) {
            Toast.makeText(this, getString(R.string.please_enter_card_expiration_details), Toast.LENGTH_SHORT).show();
            return;
        }
        if (cardForm.getCvv().isEmpty()) {
            Toast.makeText(this, getString(R.string.please_enter_card_cvv), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.isDigitsOnly(cardForm.getExpirationMonth()) || !TextUtils.isDigitsOnly(cardForm.getExpirationYear())) {
            Toast.makeText(this, getString(R.string.please_enter_card_expiration_details), Toast.LENGTH_SHORT).show();
            return;
        }

        String cardNumber = cardForm.getCardNumber();
        int cardMonth = Integer.parseInt(cardForm.getExpirationMonth());
        int cardYear = Integer.parseInt(cardForm.getExpirationYear());
        String cardCvv = cardForm.getCvv();
        Log.d("CARD", "CardDetails Number: " + cardNumber + "Month: " + cardMonth + " Year: " + cardYear + " Cvv " + cardCvv);
        Card card = new Card(cardNumber, cardMonth, cardYear, cardCvv);
        addCard(card);

    }

    public void onClickScanCard() {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard creditCard = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                Card card = new com.stripe.android.model.Card(creditCard.cardNumber, creditCard.expiryMonth, creditCard.expiryYear, creditCard.cvv);
                card.setCurrency("usd");
                addCard(card);
            } else {
                resultDisplayStr = "Scan was canceled.";
            }
        }
    }

    private void addCard(Card card) {
        Stripe stripe = new Stripe(this, stripe_token);
        stripe.createToken(card, new TokenCallback() {
                    public void onSuccess(Token token) {
                        Log.d("CARD:", " " + token.getId());
                        Log.d("CARD:", " " + token.getCard().getLast4());
                        String stripeToken = token.getId();

                        Intent intent = new Intent();
                        intent.putExtra("stripetoken", stripeToken);
                        setResult(100, intent);
                        finish();
                    }

                    public void onError(Exception error) {
                        try {
                            Log.d(TAG, "onError: ");
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
