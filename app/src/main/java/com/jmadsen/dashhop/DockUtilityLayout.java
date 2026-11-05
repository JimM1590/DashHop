package com.jmadsen.dashhop;
import android.content.Context;import android.view.*;
/** Fixed utilities reflow on constrained screens; only the app strip scrolls. */
final class DockUtilityLayout extends ViewGroup {
 final boolean vertical;final int budget,cross;boolean grid;int rows,columns,cellW,cellH;
 DockUtilityLayout(Context c,boolean v,int maxAxis,int crossAxis){super(c);vertical=v;budget=Math.max(1,maxAxis);cross=crossAxis;}
 protected void onMeasure(int ws,int hs){int count=getChildCount(),total=0;for(int i=0;i<count;i++){LayoutParams p=getChildAt(i).getLayoutParams();total+=Math.max(1,vertical?p.height:p.width);}grid=total>budget;rows=columns=1;int w,h;if(!grid){w=vertical?cross:total;h=vertical?total:cross;for(int i=0;i<count;i++){View child=getChildAt(i);LayoutParams p=child.getLayoutParams();child.measure(MeasureSpec.makeMeasureSpec(vertical?w:p.width,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(vertical?p.height:h,MeasureSpec.EXACTLY));}}
 else{if(vertical){columns=Math.max(1,Math.min(2,cross/DockController.px(getContext(),56)));rows=(count+columns-1)/columns;w=cross;h=Math.min(budget,rows*DockController.px(getContext(),80));}else{rows=Math.max(1,Math.min(2,cross/DockController.px(getContext(),48)));columns=(count+rows-1)/rows;w=budget;h=cross;}cellW=w/Math.max(1,columns);cellH=h/Math.max(1,rows);for(int i=0;i<count;i++)getChildAt(i).measure(MeasureSpec.makeMeasureSpec(i==count-1?cellW*(columns-i%columns):cellW,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(cellH,MeasureSpec.EXACTLY));}setMeasuredDimension(Math.max(0,w),Math.max(0,h));}
 protected void onLayout(boolean changed,int l,int t,int r,int b){int offset=0;for(int i=0;i<getChildCount();i++){View child=getChildAt(i);if(grid){int x=i%columns*cellW,y=i/columns*cellH;child.layout(x,y,x+child.getMeasuredWidth(),y+child.getMeasuredHeight());}else if(vertical){child.layout(0,offset,child.getMeasuredWidth(),offset+child.getMeasuredHeight());offset+=child.getMeasuredHeight();}else{child.layout(offset,0,offset+child.getMeasuredWidth(),child.getMeasuredHeight());offset+=child.getMeasuredWidth();}}}
 protected LayoutParams generateDefaultLayoutParams(){return new LayoutParams(-2,-2);}
}
