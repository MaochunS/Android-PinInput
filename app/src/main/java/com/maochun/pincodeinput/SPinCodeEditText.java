package com.maochun.pincodeinput;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

public class SPinCodeEditText extends AppCompatEditText implements TextWatcher {

    private final String BORDERED = "PINCODE_BORDERED";
    private final String UNDERLINED = "PINCODE_UNDERLINE";

    private String mBoxStyle = UNDERLINED;
    private String mMaskCharacter = "*";

    private float mEdgeStroke = 1;
    private float mPinSpace = 8;
    private float mCharSize;

    private int mNumChars = 6;
    private float mLineSpacing = 10;

    private boolean mMaskInput = true;

    private Paint mBackgroundPaint;
    private Paint mEdgePaint;
    private Paint mTextPaint;

    private int mPrimaryTextColor;
    private int mSecondaryTextColor;

    private int mPrimaryColor;
    private int mSecondaryColor;

    private int mEdgePrimaryColor;
    private int mEdgeSecondaryColor;

    public SPinCodeEditText(Context context) {
        super(context);

    }

    public SPinCodeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SPinCodeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //this.defStyleAttr = defStyleAttr;
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {

        TypedArray a;
        if (attrs != null) {
            a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.PinCodeEditText,
                    0, 0);
        } else {
            throw new IllegalArgumentException("Must have to pass the attributes");
        }

/*
        mTextColor = ContextCompat.getColor(context, R.color.colorPurple);
        mPrimaryColor = ContextCompat.getColor(context, R.color.colorPrimary);
        mSecondaryColor = ContextCompat.getColor(context, R.color.colorAccent);
        mEdgePrimaryColor = ContextCompat.getColor(context, R.color.colorYellow);
        mEdgeSecondaryColor = ContextCompat.getColor(context, R.color.colorYellowLight);
*/


        mPrimaryTextColor = a.getColor(R.styleable.PinCodeEditText_primaryTextColor, ContextCompat.getColor(context, R.color.colorWhite));
        mSecondaryTextColor = a.getColor(R.styleable.PinCodeEditText_primaryTextColor, ContextCompat.getColor(context, R.color.colorPurple));
        mPrimaryColor = a.getColor(R.styleable.PinCodeEditText_primaryBgColor, ContextCompat.getColor(context, R.color.colorYellow));
        mSecondaryColor = a.getColor(R.styleable.PinCodeEditText_secondaryBgColor, ContextCompat.getColor(context, R.color.colorYellowLight));
        mEdgePrimaryColor = a.getColor(R.styleable.PinCodeEditText_primaryEdgeColor, ContextCompat.getColor(context, R.color.colorBlack));
        mEdgeSecondaryColor = a.getColor(R.styleable.PinCodeEditText_secondaryEdgeColor, ContextCompat.getColor(context, R.color.colorWhite));




        mTextPaint = getPaint();
        mTextPaint.setColor(mSecondaryTextColor);

        float multi = context.getResources().getDisplayMetrics().density;
        mBackgroundPaint = new Paint(getPaint());
        mBackgroundPaint.setStrokeWidth(mEdgeStroke * multi);

        mEdgePaint = new Paint(getPaint());
        mEdgePaint.setStrokeWidth(8);
        mEdgePaint.setStyle(Paint.Style.STROKE);


        this.setFilters(new InputFilter[] {new InputFilter.LengthFilter(mNumChars)});

        setBackgroundResource(0);

        this.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        this.setLongClickable(false);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (getText().length() > mNumChars){
            triggerErrorAnimation();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int availableWidth = getWidth() - getPaddingRight() - getPaddingLeft();
        if (mPinSpace < 0) {
            mCharSize = (availableWidth / (mNumChars * 2 - 1));
        } else {
            mCharSize = (availableWidth - (mPinSpace * (mNumChars - 1))) / mNumChars;
        }

        mLineSpacing = (float) (getHeight() * .6);

        int startX = getPaddingLeft();
        int bottom = getHeight() - getPaddingBottom();
        int top = getPaddingTop();

        //Text Width
        Editable text = getText();
        int textLength = text.length();
        float[] textWidths = new float[textLength];
        getPaint().getTextWidths(getText(), 0, textLength, textWidths);

        for (int i = 0; i < mNumChars; i++) {
            updateColorForLines(i == textLength);

            switch (mBoxStyle) {
                case UNDERLINED:
                    try {
                        canvas.drawRoundRect(startX, bottom * .95f, startX + mCharSize, bottom, 16, 16, mBackgroundPaint);
                    } catch (NoSuchMethodError err) {
                        canvas.drawRect(startX, bottom * .95f, startX + mCharSize, bottom, mBackgroundPaint);
                    }
                    break;

                case BORDERED:
                    try {
                        canvas.drawRoundRect(startX, top, startX + mCharSize, bottom, 8, 8, mBackgroundPaint);
                        canvas.drawRoundRect(startX, top, startX + mCharSize, bottom, 8, 8, mEdgePaint);
                    } catch (NoSuchMethodError err) {
                        canvas.drawRect(startX, top, startX + mCharSize, bottom, mBackgroundPaint);
                        canvas.drawRect(startX, top, startX + mCharSize, bottom, mEdgePaint);
                    }
                    break;

            }


            if (getText().length() > i) {
                float middle = startX + mCharSize / 2;
                if (mMaskInput) {
                    canvas.drawText(getMaskText(), i, i + 1, middle - textWidths[i] / 2, mLineSpacing, mTextPaint);
                } else {
                    canvas.drawText(text, i, i + 1, middle - textWidths[i] / 2, mLineSpacing, mTextPaint);
                }
            }

            if (mPinSpace < 0) {
                startX += mCharSize * 2;
            } else {
                startX += mCharSize + mPinSpace;
            }

        }
    }

    private String getMaskText() {
        int length = String.valueOf(getText()).length();

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < length; i++) {
            out.append(mMaskCharacter);
        }

        Log.i("test", "getMaskText " + length);
        return out.toString();
    }

    private void updateColorForLines(boolean current) {

        if (current) {
            mBackgroundPaint.setColor(mEdgePrimaryColor);
            mEdgePaint.setColor(mPrimaryColor);
            mTextPaint.setColor(mPrimaryTextColor);
        }else{
            mBackgroundPaint.setColor(mSecondaryColor);
            mEdgePaint.setColor(mEdgeSecondaryColor);
            mTextPaint.setColor(mSecondaryTextColor);
        }
    }

    public void triggerErrorAnimation() {
        this.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake));
    }
}
