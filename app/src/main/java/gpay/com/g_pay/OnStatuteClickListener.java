package gpay.com.g_pay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by aKI on 02/02/2017.
 */

public class OnStatuteClickListener implements RecyclerView.OnItemTouchListener
{
    OnClickStatute onClickStatute;
    Context context;
    RecyclerView recyclerView;
    GestureDetector gestureDetector;

    public OnStatuteClickListener(Context c, final RecyclerView recycler, OnClickStatute onClick)
    {
        context = c;
        recyclerView = recycler;
        onClickStatute = onClick;

        gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent)
            {
                View view = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());
                if(view != null && onClickStatute != null)
                {
                    onClickStatute.onLongClick(view,recyclerView.getChildLayoutPosition(view));
                }
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }
        });
    }


    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e)
    {
        View view = recyclerView.findChildViewUnder(e.getX(),e.getY());
        if(view != null && onClickStatute != null && gestureDetector.onTouchEvent(e))
        {
            onClickStatute.onClick(view,recyclerView.getChildLayoutPosition(view));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
