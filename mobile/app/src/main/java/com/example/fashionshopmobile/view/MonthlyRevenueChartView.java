package com.example.fashionshopmobile.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.fashionshopmobile.model.MonthlyRevenue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class MonthlyRevenueChartView extends View {

    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private List<MonthlyRevenue> monthlyRevenue = new ArrayList<>();

    public MonthlyRevenueChartView(Context context) {
        super(context);
        init();
    }

    public MonthlyRevenueChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MonthlyRevenueChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        axisPaint.setColor(Color.rgb(160, 160, 160));
        axisPaint.setStrokeWidth(2f);

        gridPaint.setColor(Color.rgb(230, 230, 230));
        gridPaint.setStrokeWidth(1f);

        barPaint.setColor(Color.rgb(238, 77, 45));

        textPaint.setColor(Color.rgb(90, 90, 90));
        textPaint.setTextSize(24f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        emptyPaint.setColor(Color.rgb(120, 120, 120));
        emptyPaint.setTextSize(28f);
        emptyPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setMonthlyRevenue(List<MonthlyRevenue> monthlyRevenue) {
        if (monthlyRevenue == null) {
            this.monthlyRevenue = new ArrayList<>();
        } else {
            this.monthlyRevenue = monthlyRevenue;
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        if (monthlyRevenue == null || monthlyRevenue.isEmpty()) {
            canvas.drawText("Chưa có dữ liệu doanh thu", width / 2f, height / 2f, emptyPaint);
            return;
        }

        float left = 50f;
        float top = 35f;
        float right = width - 20f;
        float bottom = height - 45f;

        float chartWidth = right - left;
        float chartHeight = bottom - top;

        canvas.drawLine(left, bottom, right, bottom, axisPaint);
        canvas.drawLine(left, top, left, bottom, axisPaint);

        for (int i = 1; i <= 3; i++) {
            float y = bottom - chartHeight * i / 4f;
            canvas.drawLine(left, y, right, y, gridPaint);
        }

        BigDecimal maxRevenue = BigDecimal.ZERO;

        for (MonthlyRevenue item : monthlyRevenue) {
            if (item != null
                    && item.getRevenue() != null
                    && item.getRevenue().compareTo(maxRevenue) > 0) {
                maxRevenue = item.getRevenue();
            }
        }

        int count = 12;
        float slotWidth = chartWidth / count;
        float barWidth = slotWidth * 0.55f;

        for (int i = 0; i < count; i++) {
            MonthlyRevenue item = i < monthlyRevenue.size() ? monthlyRevenue.get(i) : null;

            BigDecimal revenue = item != null && item.getRevenue() != null
                    ? item.getRevenue()
                    : BigDecimal.ZERO;

            float ratio = 0f;

            if (maxRevenue.compareTo(BigDecimal.ZERO) > 0) {
                ratio = revenue.divide(maxRevenue, 4, RoundingMode.HALF_UP).floatValue();
            }

            float barHeight = chartHeight * ratio;
            float centerX = left + slotWidth * i + slotWidth / 2f;

            float barLeft = centerX - barWidth / 2f;
            float barRight = centerX + barWidth / 2f;
            float barTop = bottom - barHeight;

            if (barHeight < 2f) {
                barTop = bottom - 2f;
            }

            RectF rect = new RectF(barLeft, barTop, barRight, bottom);
            canvas.drawRoundRect(rect, 8f, 8f, barPaint);

            canvas.drawText(String.valueOf(i + 1), centerX, height - 12f, textPaint);
        }
    }
}