package com.yds.grouphelper.common.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import com.yds.grouphelper.R;
import com.yds.grouphelper.Session;

import java.util.List;

public final class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final int TYPE_HEADER = -100;
    private static final int TYPE_BODY = TYPE_HEADER + 1;
    private static final int TYPE_FOOTER = TYPE_HEADER + 2;
    private static final int TYPE_EMPTY=TYPE_HEADER + 3;

    public static final int ACTION_ONEND = 1;
    public static final int ACTION_CLICK = 2;
    public static final int ACTION_LONG_CLICK = 3;
    public static final int ACTION_OTHERS = 4;

    private Session mSession;

    private boolean endEnable;
    private boolean clickEnable;
    private boolean longClickEnable;

    private Context mContext;
    private List mList;
    private int mLayoutID;
    private LayoutInflater mInflater;
    private AdapterListener mListener;

    private View mEmptyView;
    private View mHeaderView;
    private View mFooterView;

    private int imgWidth=Integer.MIN_VALUE;

    public RecyclerAdapter(Context context, List list, int layoutID) {
        init(context, list, layoutID);
    }

    private synchronized void init(Context context, List list, int layoutID) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mList = list;
        mLayoutID = layoutID;
        mSession = Session.getInstance(mContext);
    }

    public void setAdapterListener(AdapterListener listener, boolean endEnable, boolean clickEnable, boolean longClickEnable) {
        mListener = listener;
        this.endEnable = endEnable;
        this.clickEnable = clickEnable;
        this.longClickEnable = longClickEnable;
    }


    //=========================header and footer===========================================
    public synchronized void setHeaderView(View headerView) {
        removeEmptyView();
        if (null == mHeaderView) {
            mHeaderView = headerView;
            notifyItemInserted(0);
        }
    }

    public synchronized void setFooterView(View footerView) {
        removeEmptyView();
        if (null == mFooterView) {
            mFooterView = footerView;
            addData(mList.size(), null);
        }
    }

    public View getHeader() {
        return mHeaderView;
    }

    public synchronized void setEmptyView(View emptyView) {
        mEmptyView=emptyView;
        notifyItemInserted(0);
    }

    public synchronized void removeEmptyView() {
        mEmptyView=null;
    }


    public void insertView(int position, View view) {
        addData(position, view);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == TYPE_HEADER ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        View itemView=holder.itemView;
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (holder.getItemViewType() >= 0) {
            //插入的控件
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            itemView.setLayoutParams(lp);

        }else{
            int position=holder.getLayoutPosition();
            if(null!=mEmptyView && position==0){
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = mSession.getScreenHeight() * 4 / 5;
                itemView.setLayoutParams(lp);

            }else if (null != mHeaderView && position == 0) {
                if (null != lp && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                    ((StaggeredGridLayoutManager.LayoutParams) lp).setFullSpan(true);
                }

                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                itemView.setLayoutParams(lp);

            }else if (null != mFooterView && position == mList.size() - 1) {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                itemView.setLayoutParams(lp);

            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        if(null!=mEmptyView && position==0){
            return TYPE_EMPTY;
        }else if (null != mHeaderView && position == 0) {
            return TYPE_HEADER;
        } else if (null != mFooterView && position == mList.size() - 1) {
            return TYPE_FOOTER;
        } else if (position < mList.size() && mList.get(position) instanceof View) {
            //插入的控件
            return position;
        }
        return TYPE_BODY;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType >= 0) {
            return new MyViewHolder((View) mList.get(viewType));
        } else {
            if(null!=mEmptyView && viewType==TYPE_EMPTY){
                return new MyViewHolder(mEmptyView);

            }else if (null != mHeaderView && viewType == TYPE_HEADER) {
                return new MyViewHolder(mHeaderView);

            } else if (null != mFooterView && viewType == TYPE_FOOTER) {
                return new MyViewHolder(mFooterView);

            } else{
                return new MyViewHolder(mInflater.inflate(mLayoutID, parent, false));
            }
        }
    }

    @Override
    public int getItemCount() {
        if(null!=mEmptyView) return 1;
        if (mList != null) {
            return null == mHeaderView ? mList.size() : mList.size() + 1;
        } else {
            return 0;
        }
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (getItemViewType(position) != TYPE_BODY) return;
        if (mHeaderView != null) --position;
        if (mList == null || position >= mList.size()) return;

        if (mLayoutID == R.layout.item_group) {


        }

        // 如果设置了回调，则设置点击事件
        setCallBack(holder);
    }

    private void setCallBack(final MyViewHolder holder) {
        if (null != mListener) {
            int position = holder.getLayoutPosition();
            if (null != mHeaderView) --position;
            if (endEnable && position == mList.size() - 1) {
                mListener.adapterListener(ACTION_ONEND, position, holder.itemView, null);
            }

            if (clickEnable) {
                holder.itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        if (null != mHeaderView) --position;
                        mListener.adapterListener(ACTION_CLICK, position, holder.itemView, null);
                    }
                });
            }

            if (longClickEnable) {
                holder.itemView.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = holder.getLayoutPosition();
                        if (null != mHeaderView) --position;
                        mListener.adapterListener(ACTION_LONG_CLICK, position, holder.itemView, null);
                        return false;
                    }
                });
            }
        }

    }

    public synchronized void addData(int position, Object object) {
        removeEmptyView();
        if (mList.size() == 0) {
            mList.add(position, object);
            notifyDataSetChanged();
        } else {
            mList.add(position, object);
            notifyItemInserted(mList.size());
        }
    }

    public synchronized void removeData(int position) {
        if (position < mList.size()) {
            mList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public synchronized void clear() {
        mList.clear();
        notifyDataSetChanged();
    }


    public Object getItemData(int position) {
        if (mList != null && position < mList.size()) {
            return mList.get(position);
        } else {
            return null;
        }
    }


    //=================================================================================
    private final class ViewClick implements OnClickListener {
        int position;

        ViewClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (null != mListener) {
                mListener.adapterListener(ACTION_OTHERS, position, v, null);
            }
        }
    }

}