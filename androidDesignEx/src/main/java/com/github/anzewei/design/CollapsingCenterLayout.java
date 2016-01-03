package com.github.anzewei.design;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.AttributeSet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by zewei on 2015-12-23.
 */
public class CollapsingCenterLayout extends CollapsingToolbarLayout {
    private CollapsingTextHelper mHelper;
    private Rect mRect;
    public CollapsingCenterLayout(Context context) {
        super(context);
        findRect();
        mHelper = new CollapsingTextHelper(this);
    }

    public CollapsingCenterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHelper = new CollapsingTextHelper(this);
        findRect();
    }

    public CollapsingCenterLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHelper = new CollapsingTextHelper(this);
        findRect();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mHelper.setCollapsedBounds(0, bottom - mRect.height(),
                getWidth(), bottom);
        mHelper.recalculate();
    }

    private void findRect(){

        try {
            Field personNameField = CollapsingToolbarLayout.class.getDeclaredField("mTmpRect");
            personNameField.setAccessible(true);
            mRect = (Rect) personNameField.get(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private class CollapsingTextHelper{
        private Object mCollapsingTextHelper;
        private Method methodsetCollapsedBounds;
        private Method methodsetrecalculate;

        CollapsingTextHelper(CollapsingToolbarLayout layout){
            try {
                Class class1 = Class.forName("android.support.design.widget.CollapsingTextHelper");
                Field personNameField = CollapsingToolbarLayout.class.getDeclaredField("mCollapsingTextHelper");
                personNameField.setAccessible(true);
                mCollapsingTextHelper = personNameField.get(layout);
               methodsetCollapsedBounds= class1.getDeclaredMethod("setCollapsedBounds",int.class,int.class,int.class,int.class);
                methodsetCollapsedBounds.setAccessible(true);
                methodsetrecalculate= class1.getMethod("recalculate");
            } catch (Exception e) {
            }
        }

        private void setCollapsedBounds(int left, int top, int right, int bottom){
            try {
                methodsetCollapsedBounds.invoke(mCollapsingTextHelper,left,top,right,bottom);
            } catch (Exception e) {
            }
        }
        private void recalculate(){
            try {
                methodsetrecalculate.invoke(mCollapsingTextHelper);
            } catch (Exception e) {
            }
        }
    }
}
