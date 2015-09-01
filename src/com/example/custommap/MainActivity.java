package com.example.custommap;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.example.custommap.CommonData.SplitData;

public class MainActivity extends Activity {
	
	private MapImageView imageView;
	private CommonData commonData;
	
	private List<SplitData> tmpSplitDatas = new ArrayList<SplitData>();
	
	  static final int NONE = 0;// 初始状态
      static final int DRAG = 1;// 拖动
      static final int ZOOM = 2;// 缩放
      int mode = NONE;
      Point prePT = new Point();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		imageView = (MapImageView)findViewById(R.id.iv);
		
		commonData = new CommonData();
		commonData.screenWidth = getResources().getDisplayMetrics().widthPixels;
		commonData.screenHeight = getResources().getDisplayMetrics().heightPixels;
		initCommonData();
		
//		imageView.setCommonData(commonData);
	}
	
	private void initCommonData() {
		commonData.colStartID = (commonData.mapWidth-commonData.screenWidth)/2/commonData.splitWidth;
		commonData.rowStartID = (commonData.mapHeight-commonData.screenHeight)/2/commonData.splitHeight;
		
		commonData.colNum = commonData.screenWidth%commonData.splitWidth == 0 ? 
				commonData.screenWidth/commonData.splitWidth+1:
				commonData.screenWidth/commonData.splitWidth+2;
		commonData.rowNum = commonData.screenHeight%commonData.splitHeight == 0 ? 
				commonData.screenHeight/commonData.splitHeight+1 :
				commonData.screenHeight/commonData.splitHeight+2;
		
		for (int i = 0; i < commonData.rowNum; i++) {
			for (int j = 0; j < commonData.colNum; j++) {
				commonData.addSpliteData(getApplicationContext(), commonData.colStartID+j, commonData.rowStartID+i,
						commonData.splitWidth*j, commonData.splitHeight*i);
				
//				SplitData splitData = new SplitData();
//				
//				splitData.rowID = commonData.rowStartID+i;
//				splitData.colID = commonData.colStartID+j;
//				
//				splitData.xPos = j*commonData.splitWidth;
//				splitData.yPos = i*commonData.splitHeight;
//				
//				splitData.loadBitmap(getApplicationContext(), getResources().getIdentifier(
//						"map"+splitData.colID+"_"+splitData.rowID, "raw", getPackageName()));
//				
//				commonData.splitList.add(splitData);
			}
		}
		
		imageView.InitMapView(commonData);
		
//		imageView.setLayoutParams(new RelativeLayout.LayoutParams(colNum*commonData.splitWidth, 
//				rowNum*commonData.splitHeight));
		
//		imageView.setOnTouchListener(onTouchListener);
	}
	
	private OnTouchListener onTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				prePT.set((int)(event.getX()), (int)(event.getY()));
				break;
				
			case MotionEvent.ACTION_MOVE: {
                int dx = (int) event.getX() - prePT.x; 
                int dy = (int) event.getY() - prePT.y; 
                int left = v.getLeft() + dx; 
                int right = v.getRight() + dx; 
                int top = v.getTop() + dy;
                int bottom = v.getBottom() + dy;
                
                if (left > 0) { 
                    left = 0; 
                    right = left + v.getWidth(); 
                } 
 
                if (right < commonData.screenWidth) { 
                    right = commonData.screenWidth; 
                    left = right - v.getWidth(); 
                } 
                
                if (top > 0) { 
                    top = 0; 
                    bottom = top + v.getHeight(); 
                } 
 
                if (bottom < commonData.screenHeight) { 
                	bottom = commonData.screenHeight; 
                	top = bottom - v.getHeight(); 
                } 
                
                v.layout(left, top, right, bottom); 
                prePT.set((int)(event.getX()), (int)(event.getY()));
			}
				break;

			default:
				break;
			} 

			return true;
		}
	};
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
        // 主点按下
        case MotionEvent.ACTION_DOWN:
//            Log.d("System.out", "ACTION_DOWN");
            prePT.set((int)(event.getX()), (int)(event.getY()));
            mode = DRAG;
            break; // 副点按下
        case MotionEvent.ACTION_POINTER_DOWN:
//            Log.d("System.out", "ACTION_POINTER_DOWN");
//            dist = spacing(event); // 如果连续两点距离大于10，则判定为多点模式
//            if (spacing(event) > 10f) {
//                savedMatrix.set(matrix);
//                midPoint(mid, event);
//                mode = ZOOM;
//            }
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
            mode = NONE;
            break;
        case MotionEvent.ACTION_MOVE:
//        	Log.d("System.out", "ACTION_MOVE");
            if (mode == DRAG) {
//                matrix.set(savedMatrix);
//                matrix.postTranslate(event.getX() - prev.x, event.getY()
//                        - prev.y);
//            	Log.d("System.out", "mode == DRAG");
//            	move(event);
//            	prePT.set((int)(event.getX()), (int)(event.getY()));

            } else if (mode == ZOOM) {
//                float newDist = spacing(event);
//                if (newDist > 10f) {
//                    matrix.set(savedMatrix);
//                    float tScale = newDist / dist;
//                    matrix.postScale(tScale, tScale, mid.x, mid.y);
//                }
            }
            break;
        }

        return false;
	}

//	private void move(MotionEvent event) {  
//		int xDiff = (int) (prePT.x - event.getX());
//		int yDiff = (int) (prePT.y - event.getY());
//           
//           tmpSplitDatas.clear();
//           for (SplitData splitData : commonData.splitList) {
//        	   int resultX = splitData.xPos - xDiff;
//        	   int resultY = splitData.yPos - yDiff;
//        	   
//        	   if (resultX <= -commonData.splitWidth || resultX >= commonData.screenWidth ||
//        			   resultY <= -commonData.splitHeight || resultY >= commonData.screenHeight ) {
//        		   splitData.recycleBitmap();
//        		   continue;
//        	   } 
//
//        	   if (splitData.xPos+commonData.splitWidth >= commonData.screenWidth && 
//        			   resultX+commonData.splitWidth < commonData.screenWidth) {
//        		   if (splitData.colID < commonData.mapWidth/commonData.splitWidth-1) {
//        			   SplitData newSplitData = new SplitData();
//       				
//        			   newSplitData.rowID = splitData.rowID;
//        			   newSplitData.colID = splitData.colID+1;
//       				
//        			   newSplitData.xPos = resultX+commonData.splitWidth;
//        			   newSplitData.yPos = resultY;
//       				
//        			   newSplitData.loadBitmap(getApplicationContext(), getResources().getIdentifier(
//       						"map"+newSplitData.colID+"_"+newSplitData.rowID, "raw", getPackageName()));
//       				
//        			   tmpSplitDatas.add(newSplitData);
//        		   }
//        	   } else if (splitData.xPos <= 0 && resultX < 0) {
//        		   if (splitData.colID > 0) {
//        			   SplitData newSplitData = new SplitData();
//       				
//        			   newSplitData.rowID = splitData.rowID;
//        			   newSplitData.colID = splitData.colID-1;
//       				
//        			   newSplitData.xPos = resultX-commonData.splitWidth;
//        			   newSplitData.yPos = resultY;
//       				
//        			   newSplitData.loadBitmap(getApplicationContext(), getResources().getIdentifier(
//       						"map"+newSplitData.colID+"_"+newSplitData.rowID, "raw", getPackageName()));
//       				
//        			   tmpSplitDatas.add(newSplitData);
//        		   }
//        	   } 
//
//        	   if (splitData.yPos+commonData.splitHeight >= commonData.screenHeight && 
//        			   resultY+commonData.splitHeight < commonData.screenHeight) {
//        		   if (splitData.rowID < commonData.mapHeight/commonData.splitHeight-1) {
//        			   SplitData newsplitdata = new SplitData();
//       				
//        			   newsplitdata.rowID = splitData.rowID+1;
//        			   newsplitdata.colID = splitData.colID;
//       				
//        			   newsplitdata.xPos = resultX;
//        			   newsplitdata.yPos = resultY+commonData.splitHeight;
//       				
//        			   newsplitdata.loadBitmap(getApplicationContext(), getResources().getIdentifier(
//       						"map"+newsplitdata.colID+"_"+newsplitdata.rowID, "raw", getPackageName()));
//       				
//        			   tmpSplitDatas.add(newsplitdata);
//        		   }
//        	   } else if (splitData.yPos <= 0 && resultY < 0) {
//        		   if (splitData.rowID > 0) {
//        			   SplitData newsplitdata = new SplitData();
//       				
//        			   newsplitdata.rowID = splitData.rowID-1;
//        			   newsplitdata.colID = splitData.colID;
//       				
//        			   newsplitdata.xPos = resultX;
//        			   newsplitdata.yPos = resultY-commonData.splitHeight;
//       				
//        			   newsplitdata.loadBitmap(getApplicationContext(), getResources().getIdentifier(
//       						"map"+newsplitdata.colID+"_"+newsplitdata.rowID, "raw", getPackageName()));
//       				
//        			   tmpSplitDatas.add(newsplitdata);
//        		   }
//        	   } 
//        	   
//        	   splitData.xPos = resultX;
//        	   splitData.yPos = resultY;
//        	   tmpSplitDatas.add(splitData);
//           }
//           
//    	   commonData.splitList.clear();
//    	   commonData.splitList.addAll(tmpSplitDatas);
//    	   imageView.invalidate();
//        }  
    
}
