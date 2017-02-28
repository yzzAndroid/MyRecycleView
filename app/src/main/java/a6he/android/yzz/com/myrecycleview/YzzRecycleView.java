package a6he.android.yzz.com.myrecycleview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by yzz on 2017/2/26 0026.
 */
public class YzzRecycleView extends RecyclerView {

    private View head;
    private View foot;
    private boolean isNeedHeadFresh = false;
    private boolean isNeedFootFresh = false;
    //刷新的状态
    public static final int STATE_NOMAL = 0;
    public static final int STATE_FRESHING = 1;
    private int mState = STATE_NOMAL;
    private RecyclerView.Adapter<ViewHolder> mAdapter;
    private boolean opPenHead = false;
    private boolean opFoot = false;
    private float mFy = 0;
    private OnLoadMoreListener mOnLoadMore;
    private Adapter mInnerAdapter;
    public static final int HEAD = 0;
    public static final int BOTTOM = 1;
    private int mRefreshPosition = HEAD - 1;
    //刷新界面的高度
    public static final int STATIC_HEIGHT = 200;

    public YzzRecycleView(Context context) {
        super(context);
        init();
    }

    public YzzRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public YzzRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setMAdapter(RecyclerView.Adapter mAdapter) {
        this.mAdapter = mAdapter;
        setAdapter(mInnerAdapter);
    }

    private void init() {
        mInnerAdapter = new Adapter();
        head = LayoutInflater.from(getContext()).inflate(R.layout.refresh, null);
        foot = LayoutInflater.from(getContext()).inflate(R.layout.refresh, null);
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //这里要判断是否是到达顶部和尾部
                //当在刷新的时候就不做任何处理
                if (mState == STATE_FRESHING) {
                    return;
                }
                if (mAdapter == null) {
                    return;
                }
                int visibleCount = getLayoutManager().getChildCount();
                if (visibleCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE && isNeedFresh()) {
                    //判断是否到达顶部
                    int first = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
                    if (first == 0 && isNeedHeadFresh) {
                        opPenHead = true;
                    }
                    int last = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

                    if (last == mAdapter.getItemCount() - 1) {
                        opFoot = true;
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });

    }


    public void setLayoutManger(final LayoutManager layout, final int spanCount) {
        if (layout instanceof GridLayoutManager) {
            final GridLayoutManager m = (GridLayoutManager) layout;
            m.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (mState == STATE_FRESHING) {
                        if (position == 0 && mRefreshPosition == HEAD) {
                            return spanCount;
                        } else if (mRefreshPosition == BOTTOM && position == getAdapter().getItemCount() - 1) {
                            return spanCount;
                        }
                    }
                    return 1;
                }
            });
            setLayoutManager(layout);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mFy = ev.getY();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFy = e.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                float y = e.getY() - mFy;
                loadMore(y);
                opPenHead = false;
                opFoot = false;
                break;
        }
        return super.onTouchEvent(e);
    }


    private void loadMore(float x) {
        if (x > 50 && opPenHead) {
            mState = STATE_FRESHING;
            if (mOnLoadMore != null) {
                mRefreshPosition = HEAD;
                mOnLoadMore.onLoadMore();
                notifyData();
            }
        }

        if (x < -50 && opFoot) {
            mState = STATE_FRESHING;
            if (mOnLoadMore != null) {
                mRefreshPosition = BOTTOM;
                mOnLoadMore.onLoadMore();
                notifyData();
                scrollBy(0, STATIC_HEIGHT);
            }
        }

    }

    public void setNeedHeadFresh(boolean needHeadFresh) {
        isNeedHeadFresh = needHeadFresh;
    }

    public void setNeedFootFresh(boolean needFootFresh) {
        isNeedFootFresh = needFootFresh;
    }

    public int getHeadCount() {
        return mRefreshPosition == HEAD ? 1 : 0;
    }

    public int getFootCount() {
        return mRefreshPosition == BOTTOM ? 1 : 0;
    }

    public boolean isNeedFresh() {
        return isNeedFootFresh || isNeedHeadFresh ? true : false;
    }

    //玩成的监听
    public void complete() {
        mState = STATE_NOMAL;
        mRefreshPosition = HEAD - 1;
        //如果是
//        if (getLayoutManager() instanceof GridLayoutManager) {
//            setLayoutManager(getLayoutManager());
//        }
        notifyData();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMore) {
        this.mOnLoadMore = mOnLoadMore;
    }

    public void notifyData() {
        mInnerAdapter.notifyDataSetChanged();
    }


    /**
     * BaseAdapter，控制头尾的刷新视图
     * <p/>
     * RecyclerView.INVALID_TYPE:是-1,这里作为头部视图
     */

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (mState == STATE_NOMAL) {
                return mAdapter.onCreateViewHolder(parent, viewType);
            }
            if (viewType == INVALID_TYPE && isNeedHeadFresh && mRefreshPosition == HEAD) {

                return new ViewHolder(head);
            } else if (viewType == INVALID_TYPE - 1 && isNeedFootFresh && mRefreshPosition == BOTTOM) {
                return new ViewHolder(foot);
            }
            return mAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (mState == STATE_NOMAL) {
                mAdapter.onBindViewHolder(holder, position);
                return;
            }
            if (position >= 0 && position < getHeadCount()) {
                return;
            }
            if (mAdapter != null) {
                int real = position - getHeadCount();
                if (real < mAdapter.getItemCount()) {
                    mAdapter.onBindViewHolder(holder, real);
                }
            }
        }

        @Override
        public int getItemCount() {
            if (mState == STATE_NOMAL) {
                return mAdapter.getItemCount();
            }
            return mAdapter == null ? getHeadCount() + getFootCount() : +getHeadCount() + getFootCount() + mAdapter.getItemCount();
        }

        @Override
        public int getItemViewType(int position) {
            if (mState == STATE_NOMAL) {
                return mAdapter.getItemViewType(position);
            }
            if (getHeadCount() > 0 && position < 1) {
                return INVALID_TYPE;
            }
            int real = position - getHeadCount();
            if (real < mAdapter.getItemCount()) {
                return mAdapter.getItemViewType(real);
            }
            return INVALID_TYPE - 1;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);

        }
    }
}
