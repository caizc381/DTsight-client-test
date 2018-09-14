package com.tijiantest.model.card;

import com.tijiantest.model.account.AccountRelationInCrm;
import com.tijiantest.model.account.Examiner;

public class CardRecordDto {
    private Card card;
    private Examiner account;
    private CardSetting cardSetting;

    public CardRecordDto() {
    }

    public Card getCard() {
        return this.card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Examiner getAccount() {
        return account;
    }

    public void setAccount(Examiner account) {
        this.account = account;
    }

    public CardSetting getCardSetting() {
        return this.cardSetting;
    }

    public void setCardSetting(CardSetting cardSetting) {
        this.cardSetting = cardSetting;
    }
}
