package com.ihank.badgestringview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * 从页面左上角开始绘制，高度固定，宽度根据内容动态调整
 */
public class BadgeStringView extends View {
    private Paint mBadgePaint;
    // 标记文字
    private String mBadgeString = "";
    // 标记文字大小
    private int mBadgeStringSize;
    // 标记文字颜色
    private int mBadgeStringColor;
    // 椭圆背景颜色
    private int mBadgeOvalColor;
    // 椭圆左右间距
    private int mBadgeOvalPadding;
    // 椭圆高度
    private int mBadgeOvalHeight;
    private Paint mOvalPaint;
    private RectF mOvalRectF;

    public BadgeStringView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.BadgeStringView);
        mBadgeString = array.getString(R.styleable.BadgeStringView_badge_string);
        mBadgeStringSize = array.getDimensionPixelSize(R.styleable.BadgeStringView_badge_string_size, getResources().getDimensionPixelSize(R.dimen.badge_text_size));
        mBadgeStringColor = array.getColor(R.styleable.BadgeStringView_badge_string_color, ContextCompat.getColor(context, R.color.badge_white));
        mBadgeOvalColor = array.getColor(R.styleable.BadgeStringView_badge_oval_color, ContextCompat.getColor(context, R.color.badge_red));
        mBadgeOvalPadding = array.getDimensionPixelSize(R.styleable.BadgeStringView_badge_oval_padding, getResources().getDimensionPixelSize(R.dimen.badge_string_padding));
        mBadgeOvalHeight = array.getDimensionPixelSize(R.styleable.BadgeStringView_badge_oval_height, getResources().getDimensionPixelSize(R.dimen.badge_oval_height));
        initPaint();
        array.recycle();
        setWillNotDraw(false);
    }

    public BadgeStringView(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public BadgeStringView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
    }

    public void setBadgeString(String badgeString) {
        mBadgeString = badgeString;
        this.requestLayout();
        this.invalidate();
    }

    private void initPaint() {
        // 初始化文字绘制Paint
        mBadgePaint = new Paint();
        mBadgePaint.setAntiAlias(true);
        mBadgePaint.setColor(mBadgeStringColor);
        mBadgePaint.setTextSize(mBadgeStringSize);

        // 初始化椭圆绘制Paint
        mOvalPaint = new Paint();
        mOvalPaint.setStyle(Style.FILL);
        mOvalPaint.setColor(mBadgeOvalColor);
        mOvalPaint.setAntiAlias(true);
        mOvalRectF = new RectF();
        mOvalRectF.left = 0;
        mOvalRectF.top = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        // 计算新消息数量文字宽度
        float badgeStringWidth = mBadgePaint.measureText(mBadgeString + "");
        float ovalWidth = Math.max(mBadgeOvalHeight, badgeStringWidth + mBadgeOvalPadding * 2);

        int width;
        int height;
        // Measure Width
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            // Must be this size
            width = widthSpecSize;
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            // Can't be bigger than...
            width = Math.min((int) ovalWidth, widthSpecSize);
        } else {
            // Be whatever you want
            width = (int) ovalWidth;
        }

        // Measure Height
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            // Must be this size
            height = heightSpecSize;
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            // Can't be bigger than...
            height = Math.min(mBadgeOvalHeight, heightSpecSize);
        } else {
            // Be whatever you want
            height = mBadgeOvalHeight;
        }

        setMeasuredDimension(width, height);
    }

    // 从页面左上角开始绘制，高度固定，宽度可延长
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBadgeString == null || "".equals(mBadgeString)) {
            return;
        }

        // 计算新消息数量文字宽度
        float badgeStringWidth = mBadgePaint.measureText(mBadgeString + "");
        // 取与测量宽度的最小值
        badgeStringWidth = Math.min(badgeStringWidth, getMeasuredWidth() - mBadgeOvalPadding * 2);
        // 计算新消息数量文字高度，因文字全是数字，以下计算方法最为接近
        float badgeStringHeight = -mBadgePaint.getFontMetrics().ascent
                - mBadgePaint.getFontMetrics().leading
                - mBadgePaint.getFontMetrics().descent;

        float ovalWidth;
        if (badgeStringWidth + mBadgeOvalPadding * 2 <= mBadgeOvalHeight) {
            // 标记文字加文字间距不超出范围
            ovalWidth = mBadgeOvalHeight;
            canvas.drawCircle(mBadgeOvalHeight / 2.0f,
                    mBadgeOvalHeight / 2.0f, mBadgeOvalHeight / 2.0f, mOvalPaint);
        } else {
            // 标记文字宽度加间距超出范围
            ovalWidth = badgeStringWidth + mBadgeOvalPadding * 2;
            mOvalRectF.right = badgeStringWidth + mBadgeOvalPadding * 2;
            mOvalRectF.bottom = mBadgeOvalHeight;
            canvas.drawRoundRect(mOvalRectF, mBadgeOvalHeight / 2.0f, mBadgeOvalHeight / 2.0f, mOvalPaint);
        }

        float[] measuredWidth = {0};
        int measuredCount = mBadgePaint.breakText(mBadgeString, 0, mBadgeString.length(), true, badgeStringWidth, measuredWidth);

        // 绘制文字
        canvas.drawText(mBadgeString + "", 0, measuredCount, (ovalWidth - badgeStringWidth) / 2.0f, (mBadgeOvalHeight + badgeStringHeight) / 2.0f, mBadgePaint);
    }
}
