package com.yds.grouphelper.common.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.yds.grouphelper.R;

public final class RecyclerDivider extends RecyclerView.ItemDecoration {

	private Drawable mDivider;
    private int mDividerHeight;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RecyclerDivider(Context context, int draID, int dividerheight) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDivider = context.getResources().getDrawable(draID,null);
        }else{
            mDivider = context.getResources().getDrawable(draID);
        }
        mDividerHeight= dividerheight;
    }

    public RecyclerDivider(Context context, int draID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDivider = context.getResources().getDrawable(draID,null);
        }else{
            mDivider = context.getResources().getDrawable(draID);
        }
        mDividerHeight= (int) context.getResources().getDimension(R.dimen.line_height);
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //super.onDraw(c, parent, state);
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

	private int getSpanCount(RecyclerView parent) {// 列数
		int spanCount = -1;
		LayoutManager layoutManager = parent.getLayoutManager();
		if (layoutManager instanceof GridLayoutManager) {
			spanCount = ((GridLayoutManager) layoutManager).getSpanCount();

		} else if (layoutManager instanceof StaggeredGridLayoutManager) {
			spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
		}
		return spanCount;
	}

	public void drawHorizontal(Canvas c, RecyclerView parent) {
		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = parent.getChildAt(i);
			RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
			int left = child.getLeft() - params.leftMargin;
			int right = child.getRight() + params.rightMargin + mDividerHeight;
			int top = child.getBottom() + params.bottomMargin;
			int bottom = top + mDividerHeight;
			mDivider.setBounds(left, top, right, bottom);
			mDivider.draw(c);
		}
	}

	public void drawVertical(Canvas c, RecyclerView parent) {
		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = parent.getChildAt(i);
			RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
			int top = child.getTop() - params.topMargin;
			int bottom = child.getBottom() + params.bottomMargin;
			int left = child.getRight() + params.rightMargin;
			int right = left + mDividerHeight;
			mDivider.setBounds(left, top, right, bottom);
			mDivider.draw(c);
		}
	}

	private boolean isLastColum(RecyclerView parent, int pos, int spanCount,int childCount) {
		LayoutManager layoutManager = parent.getLayoutManager();
        boolean hasHeader=false;
        if(parent.getAdapter() instanceof RecyclerAdapter){
            RecyclerAdapter adapter= (RecyclerAdapter) parent.getAdapter();
            if(adapter.getHeader()!=null){
                hasHeader=true;
            }
        }
		if (layoutManager instanceof GridLayoutManager) {
            // 如果是最后一列，则不需要绘制右边
            if(hasHeader){
                if (pos % spanCount == 0) return true;
            }else{
                if ((pos + 1) % spanCount == 0) return true;
            }
		} else if (layoutManager instanceof StaggeredGridLayoutManager) {
			int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
			if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // 如果是最后一列，则不需要绘制右边
				if ((pos + 1) % spanCount == 0) return true;

			} else {
				childCount = childCount - childCount % spanCount;
                // 如果是最后一列，则不需要绘制右边
				if (pos >= childCount) return true;
			}

		}else if(layoutManager instanceof LinearLayoutManager){
            return true;

        }
		return false;
	}

	private boolean isLastRaw(RecyclerView parent, int pos, int spanCount,int childCount) {
		LayoutManager layoutManager = parent.getLayoutManager();
		if (layoutManager instanceof GridLayoutManager) {
			childCount = childCount - childCount % spanCount;
            // 如果是最后一行，则不需要绘制底部
			if (pos >= childCount) return true;

		} else if (layoutManager instanceof StaggeredGridLayoutManager) {
			int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
			// StaggeredGridLayoutManager 且纵向滚动
			if (orientation == StaggeredGridLayoutManager.VERTICAL) {
				childCount = childCount - childCount % spanCount;
				// 如果是最后一行，则不需要绘制底部
				if (pos >= childCount) return true;
			} else{// StaggeredGridLayoutManager 且横向滚动
				// 如果是最后一行，则不需要绘制底部
				if ((pos + 1) % spanCount == 0) return true;
			}
		}
		return false;
	}

	@Override
	public void getItemOffsets(Rect outRect, int itemPosition,RecyclerView parent) {
		int spanCount = getSpanCount(parent);
		int childCount = parent.getAdapter().getItemCount();
		if (isLastRaw(parent, itemPosition, spanCount, childCount)){
			// 如果是最后一行，则不需要绘制底部
            outRect.set(0, 0, mDividerHeight, 0);

		} else if (isLastColum(parent, itemPosition, spanCount, childCount)){
			// 如果是最后一列，则不需要绘制右边
            outRect.set(0, 0, 0, mDividerHeight);

		} else {
            outRect.set(0, 0, mDividerHeight,mDividerHeight);

		}
	}
}
