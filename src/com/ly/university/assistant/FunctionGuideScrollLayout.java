package com.ly.university.assistant;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class FunctionGuideScrollLayout extends ViewGroup{

	public static interface OnViewChangeListener {
		public void OnViewChange(int view);
	}

    private static final String TAG = "�Զ���ScrollLayout";   
    
    private VelocityTracker mVelocityTracker;  			// 用于判断甩动手势
    
    private static final int SNAP_VELOCITY = 600;    
    
    private Scroller  mScroller;						// 滑动控制�?
	
    private int mCurScreen;    						
    
	private int mDefaultScreen = 0;    					
	 
    private float mLastMotionX;    
    
 //   private int mTouchSlop;							
    
//    private static final int TOUCH_STATE_REST = 0;
//    private static final int TOUCH_STATE_SCROLLING = 1;
//    private int mTouchState = TOUCH_STATE_REST;
    
    private OnViewChangeListener mOnViewChangeListener;
	 
	public FunctionGuideScrollLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	public FunctionGuideScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	public FunctionGuideScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		
		init(context);
	}
	
	private void init(Context context)
	{
		mCurScreen = mDefaultScreen;    
	  
	 //   mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();    
	        
	    mScroller = new Scroller(context); 
	    
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onLayout" +",  " + changed+", "+ l +", "+t+","+ r +", "+b);
		
		 if (changed) {    
	            int childLeft = 0;    
	            final int childCount = getChildCount();    
	                
	            for (int i=0; i<childCount; i++) {    
	                final View childView = getChildAt(i);    
	                if (childView.getVisibility() != View.GONE) {    
	                    final int childWidth = childView.getMeasuredWidth();    
	                    childView.layout(childLeft, 0,     
	                            childLeft+childWidth, childView.getMeasuredHeight());    
	                    childLeft += childWidth;    
	                }    
	            }    
	        }    
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		Log.v(TAG, "onMeasure"+widthMeasureSpec+", "+heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int width = MeasureSpec.getSize(widthMeasureSpec);       
       final int widthMode = MeasureSpec.getMode(widthMeasureSpec);      
    	Log.v(TAG, "Width"+width);
    	Log.v(TAG, "widthMode"+widthMode);
    	Log.v(TAG, "Height"+MeasureSpec.getSize(heightMeasureSpec));
    	Log.v(TAG, "HeightMode"+MeasureSpec.getMode(heightMeasureSpec));
    	
    	
		final int count = getChildCount();       
        for (int i = 0; i < count; i++) {       
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);       
        }         
        
        scrollTo(mCurScreen * width, 0);
        
	}


	 public void snapToDestination() {    
			Log.v(TAG, "snapToDestination");
	        final int screenWidth = getWidth();    
	        final int destScreen = (getScrollX()+ screenWidth/2)/screenWidth;    
	        snapToScreen(destScreen);    
	 }  
	
	 public void snapToScreen(int whichScreen) {    
	
	        // get the valid layout page    
	        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount()-1));    
	        if (getScrollX() != (whichScreen*getWidth())) {    
	                
	            final int delta = whichScreen*getWidth()-getScrollX();    
	        
	            mScroller.startScroll(getScrollX(), 0,     
	                    delta, 0, Math.abs(delta)*2);
 
	            
	            mCurScreen = whichScreen;    
	            invalidate();       // Redraw the layout    
	            
	            if (mOnViewChangeListener != null)
	            {
	            	mOnViewChangeListener.OnViewChange(mCurScreen);
	            }
	        }    
	    }    


	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {    
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());  
            postInvalidate();    
        }   
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub           
	            
	        final int action = event.getAction();    
	        final float x = event.getX();    
	      //  final float y = event.getY();    

	            
	        switch (action) {    
	        case MotionEvent.ACTION_DOWN: 
	        	
	        	  Log.v(TAG, "onTouchEvent  ACTION_DOWN");
	        	  
	        	if (mVelocityTracker == null) {    
			            mVelocityTracker = VelocityTracker.obtain();    
			            mVelocityTracker.addMovement(event); 
			    }
	        	 
	            if (!mScroller.isFinished()){    
	                mScroller.abortAnimation();    
	            }    
	            
	            mLastMotionX = x;	           
	            break;    
	                
	        case MotionEvent.ACTION_MOVE:  
	        	  Log.v(TAG, "onTouchEvent  ACTION_MOVE");
	           int deltaX = (int)(mLastMotionX - x);
	           
        	   if (IsCanMove(deltaX))
        	   {
        		 if (mVelocityTracker != null)
  		         {
  		            	mVelocityTracker.addMovement(event); 
  		         }   

  	            mLastMotionX = x;    
 
  	            scrollBy(deltaX, 0);	
        	   }
         
	           break;    
	                
	        case MotionEvent.ACTION_UP:       
	        	
	        	int velocityX = 0;
	            if (mVelocityTracker != null)
	            {
	            	mVelocityTracker.addMovement(event); 
	            	mVelocityTracker.computeCurrentVelocity(1000);  
	            	velocityX = (int) mVelocityTracker.getXVelocity();
	            }
	                    
	                
	            if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {       
	                // Fling enough to move left       
	                Log.v(TAG, "snap left");    
	                snapToScreen(mCurScreen - 1);       
	            } else if (velocityX < -SNAP_VELOCITY       
	                    && mCurScreen < getChildCount() - 1) {       
	                // Fling enough to move right       
	                Log.v(TAG, "snap right");    
	                snapToScreen(mCurScreen + 1);       
	            } else {       
	                snapToDestination();       
	            }      
	            
	           
	            
	            if (mVelocityTracker != null) {       
	                mVelocityTracker.recycle();       
	                mVelocityTracker = null;       
	            }       
	            
	      //      mTouchState = TOUCH_STATE_REST;
	            break;      
	        }    
	            
	        return true;    
	}
/**
 * 该方法事件流动时从父--->子控�?  可以再父控件中拦截处�?其他的处理例�?onTrochEvent() �?�?---->�?
 * @param deltaX
 * @return
 */
//	  public boolean onInterceptTouchEvent(MotionEvent ev) {
//          // TODO Auto-generated method stub
//          final int action = ev.getAction();
//          if ((action == MotionEvent.ACTION_MOVE)
//                          && (mTouchState != TOUCH_STATE_REST)) {
//        	  Log.i("", "onInterceptTouchEvent  return true");
//                  return true;
//          }
//          final float x = ev.getX();
//          final float y = ev.getY();
//          switch (action) {
//          case MotionEvent.ACTION_MOVE:
//                  final int xDiff = (int) Math.abs(mLastMotionX - x);
//                  if (xDiff > mTouchSlop) {
//                          mTouchState = TOUCH_STATE_SCROLLING;
//                  }
//                  break;
//
//          case MotionEvent.ACTION_DOWN:
//                  mLastMotionX = x;
//
//                  mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
//                                  : TOUCH_STATE_SCROLLING;
//                  break;
//
//          case MotionEvent.ACTION_CANCEL:
//          case MotionEvent.ACTION_UP:
//                  mTouchState = TOUCH_STATE_REST;
//                  break;
//          }
//          
//          if (mTouchState != TOUCH_STATE_REST)
//          {
//        	  Log.i("", "mTouchState != TOUCH_STATE_REST  return true");
//          }
//
//    
//          return mTouchState != TOUCH_STATE_REST;
//  }
	
	private boolean IsCanMove(int deltaX)
	{
	
		if (getScrollX() <= 0 && deltaX < 0 )
		{
			return false;
		}
		
		if  (getScrollX() >=  (getChildCount() - 1) * getWidth() && deltaX > 0)
		{
			return false;
		}
			
		
		return true;
	}
	
	public void SetOnViewChangeListener(OnViewChangeListener listener)
	{
		mOnViewChangeListener = listener;
	}

}
