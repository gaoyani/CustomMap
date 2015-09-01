package com.example.custommap;

import java.util.Iterator;

import android.R.integer;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.ImageView;

import com.example.custommap.CommonData.SplitData;

public class MapImageView extends ImageView {

	float x_down = 0;
	float y_down = 0;
	PointF start = new PointF();
	PointF mid = new PointF();
	PointF offset = new PointF();
	float oldDist = 1f;
	float oldRotation = 0;
	float curScale = 1;
	float oldScale = 1;
	Matrix orgMatrix = new Matrix();
	Matrix matrix = new Matrix();
	Matrix matrix1 = new Matrix();
	Matrix savedMatrix = new Matrix();

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	
	private static final int BG_SPACE = 32;
	int mode = NONE;

//	Bitmap gintama;
	private int bmpWidth, bmpHeight;
	private CommonData commonData;

	public MapImageView(Context context) {
		super(context);
	}
	
	public MapImageView(Context context, AttributeSet attr) {
		super(context, attr);
	}
	
	public void InitMapView(CommonData data) {
//		gintama = bmpMap;//BitmapFactory.decodeResource(getResources(), R.drawable.map);
		
		commonData = data;
		bmpWidth = commonData.splitWidth*commonData.colNum;
		bmpHeight = commonData.splitHeight*commonData.rowNum;
		//DisplayMetrics dm = new DisplayMetrics();
		//activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
//		matrix.postTranslate(-(gintama.getWidth()-widthScreen)/2, -(gintama.getHeight()-heightScreen)/2);// Æ½ÒÆ
		invalidate();
	}

	protected void onDraw(Canvas canvas) {
		canvas.save();
		Paint paint = new Paint();
		paint.setColor(Color.GRAY);
		paint.setStrokeWidth(2);
		for (int i = 0; i < commonData.screenWidth/BG_SPACE+1; i++) {
			canvas.drawLine(i*BG_SPACE, 0, i*BG_SPACE, commonData.screenHeight, paint);
		}
		
		for (int i = 0; i < commonData.screenHeight/BG_SPACE+1; i++) {
			canvas.drawLine(0, i*BG_SPACE, commonData.screenWidth, i*BG_SPACE, paint);
		}
		
//		if (gintama != null) 
//			canvas.drawBitmap(gintama, matrix, null);
		if (commonData != null) {
			for (SplitData splitData : commonData.splitList) {
//				Rect src = new Rect();// picture
//				Rect dst = new Rect();// screen
//				
//				if (splitData.xPos < 0) {
//					src.left = -splitData.xPos;
//					dst.left = 0;
////					Log.d("System.out", "" + splitData.xPos);
//				} else {
//					src.left = 0;
//					dst.left = splitData.xPos;
//				}
//				
//				if (splitData.yPos < 0) {
//					src.top = -splitData.yPos;
//					dst.top = 0;
////					Log.d("System.out", "" + splitData.yPos);
//				} else {
//					src.top = 0;
//					dst.top = splitData.yPos;
//				}
//				
//				src.right = commonData.splitWidth;
//				src.bottom = commonData.splitHeight;
//				dst.right = commonData.splitWidth+splitData.xPos;
//				dst.bottom = commonData.splitHeight+splitData.yPos;
//				
//				if (splitData.xPos < 0 || splitData.yPos < 0) {
//					Log.d("System.out", "src:" + src.left+","+src.top+","+src.right+","+src.bottom);
//					Log.d("System.out", "dst:" + dst.left+","+dst.top+","+dst.right+","+dst.bottom);
//				}
				
				if (splitData.bitmap != null) {
					float[] values = new float[9]; 
					matrix.getValues(values);
					
					Matrix tempMatrix = new Matrix();
					tempMatrix.set(matrix);
//					if (resetRes) {
//						tempMatrix.postTranslate(splitData.xPos, splitData.yPos);
//					} else {
						tempMatrix.postTranslate(splitData.xPos*values[0], splitData.yPos*values[4]);
//					}
					
					canvas.drawBitmap(splitData.bitmap, tempMatrix, null);
				}
			}
		}
		canvas.restore();
	}

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mode = DRAG;
			x_down = event.getX();
			y_down = event.getY();
			savedMatrix.set(matrix);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mode = ZOOM;
			oldDist = spacing(event);
			oldRotation = rotation(event);
			savedMatrix.set(matrix);
			midPoint(mid, event);
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == ZOOM) {
				matrix1.set(savedMatrix);
				float rotation = rotation(event) - oldRotation;
				float newDist = spacing(event);
				float scale = newDist / oldDist;
				float tempScale = oldScale*scale;
				if (tempScale >= commonData.minScale && tempScale <= commonData.maxScale) {
					Log.i("scale", "scale="+tempScale+", curScale="+curScale);
					Log.i("scale", "scale="+(int)tempScale+", curScale="+(int)curScale);
					
					if (((int)(curScale+1) == (int)tempScale && (int)tempScale%2 == 0) 
							|| ((int)curScale == (int)(tempScale+1) && (int)curScale%2 == 0)) {
						commonData.curScale = (int) tempScale;
						
						float values[] = new float[9];
						matrix1.getValues(values);
						matrix1.postScale(1/values[0], 1/values[4], mid.x, mid.y);
						
						if (curScale < tempScale) {
							scaleUp(mid.x, mid.y);
//							matrix1.setScale(1, 1, mid.x, mid.y);
						} else {
							scaleDown(mid.x, mid.y);
//							matrix1.setScale(commonData.scaleSpace, commonData.scaleSpace, mid.x, mid.y);
							matrix1.postScale(commonData.scaleSpace, commonData.scaleSpace, mid.x, mid.y);
						}
						
						matrix.set(matrix1);
						oldDist = spacing(event);
						oldScale = tempScale;
						savedMatrix.set(matrix);
						midPoint(mid, event);
						invalidate();
					} else {
						matrix1.postScale(scale, scale, mid.x, mid.y);
						// matrix1.postRotate(rotation, mid.x, mid.y);
						if (matrixCheck() == false) {
							matrix.set(matrix1);
							invalidate();
						}
					}
					
					curScale = tempScale;
				}
			} else if (mode == DRAG) {
				matrix1.set(savedMatrix);
				matrix1.postTranslate(event.getX() - x_down, event.getY()
						- y_down);
				offset.x = event.getX();
				offset.y = event.getY();
				matrix.set(matrix1);
				invalidate();
				matrixCheck();
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP: {
			mode = NONE;
			oldScale = curScale;
		}
			
			break;
		}
		return true;
	}

	private boolean matrixCheck() {
		float[] f = new float[9];
		matrix1.getValues(f);
		
		// 4 point condition
		float x1 = f[0] * 0 + f[1] * 0 + f[2];
		float y1 = f[3] * 0 + f[4] * 0 + f[5];
		float x2 = f[0] * bmpWidth + f[1] * 0 + f[2];
		float y2 = f[3] * bmpWidth + f[4] * 0 + f[5];
		float x3 = f[0] * 0 + f[1] * bmpHeight + f[2];
		float y3 = f[3] * 0 + f[4] * bmpHeight + f[5];
		float x4 = f[0] * bmpWidth + f[1] * bmpHeight + f[2];
		float y4 = f[3] * bmpWidth + f[4] * bmpHeight + f[5];
	
		double width = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
//		double height = Math.sqrt((x1 - x3) * (x1 - x3) + (y1 - y3) * (y1 - y3));
		
//		Log.i("Scale", ""+width/bmpWidth);
//		Log.i("Point", ""+x1+","+y1+","+x2+","+y2+","+x3+","+y3+","+x4+","+y4);
	
//		if (width < commonData.screenWidth || height > commonData.screenHeight) {
//			return true;
//		}
		
		//³ö½çÅÐ¶Ï
//		Log.i("Point", "x1="+x1+",y1="+y1+",x2="+x2+",y3="+y3);
		if (x1 >= 0 || x2 <= commonData.screenWidth || y1 >= 0 || y3 <= commonData.screenHeight) {
			if (x1 >= 0) {
				if (commonData.isOutLeftBorder()) {
					matrix1.postTranslate(0 - x1, 0);
				} else {
					outLeftBorder();
					savedMatrix.postTranslate(-commonData.splitWidth*f[0], 0);
					matrix1.postTranslate(x1-commonData.splitWidth*f[0], 0);
				}
						
				matrix.set(matrix1);
				invalidate();
			}
			
			if (y1 >= 0) {
				if (commonData.isOutTopBorder()) {
					matrix1.postTranslate(0, 0 - y1);
				} else {
					outTopBorder();
					savedMatrix.postTranslate(0, -commonData.splitHeight*f[4]);
					matrix1.postTranslate(0, y1-commonData.splitHeight*f[4]);
				}
				
				matrix.set(matrix1);
				invalidate();
			}
			
			if (x2 <= commonData.screenWidth) {
				if (commonData.isOutRightBorder()) {
					matrix1.postTranslate(commonData.screenWidth-x2, 0);
				} else {
					outRightBorder();
					savedMatrix.postTranslate(commonData.splitWidth*f[0], 0);
					matrix1.postTranslate(commonData.splitWidth*f[0]-(commonData.screenWidth-x2), 0);
				}
				
				matrix.set(matrix1);
				invalidate();
			}
			
			if (y3 <= commonData.screenHeight) {
				if (commonData.isOutBottomBorder()) {
					matrix1.postTranslate(0, commonData.screenHeight - y3);
				} else {
					outBottomBorder();
					savedMatrix.postTranslate(0, commonData.splitHeight*f[4]);
					matrix1.postTranslate(0, commonData.splitHeight*f[4]-(commonData.screenHeight-y3));
				}
				
				matrix.set(matrix1);
				invalidate();
			}
			
		}
		
		return false;
	}
	
	private void outLeftBorder() {
		Iterator<SplitData> stuIter = commonData.splitList.iterator();  
		while (stuIter.hasNext()) {
			SplitData splitData = stuIter.next();
			if (splitData.colID == commonData.colStartID + commonData.colNum) {
				stuIter.remove();
				splitData.recycleBitmap();
				splitData = null;
			} else {
				splitData.xPos += commonData.splitWidth;
			}
		}  
		
		commonData.colStartID -= 1;
		for (int i = 0; i < commonData.rowNum; i++) {
			commonData.addSpliteData(getContext(), commonData.colStartID, commonData.rowStartID+i,
					0, commonData.splitHeight*i);
		}
	}
	
	private void outRightBorder() {
		Iterator<SplitData> stuIter = commonData.splitList.iterator();  
		while (stuIter.hasNext()) {
			SplitData splitData = stuIter.next();
			if (splitData.colID == commonData.colStartID) {
				stuIter.remove();
				splitData.recycleBitmap();
				splitData = null;
			} else {
				splitData.xPos -= commonData.splitWidth;
			}
		} 
		
		for (int i = 0; i < commonData.rowNum; i++) {
			commonData.addSpliteData(getContext(), commonData.colStartID+commonData.colNum, commonData.rowStartID+i,
					commonData.splitWidth*(commonData.colNum-1), commonData.splitHeight*i);
		}
		
		commonData.colStartID += 1;
	}
	
	private void outTopBorder() {
		Iterator<SplitData> stuIter = commonData.splitList.iterator();  
		while (stuIter.hasNext()) {
			SplitData splitData = stuIter.next();
			if (splitData.rowID == commonData.rowStartID + commonData.rowNum) {
				stuIter.remove();
				splitData.recycleBitmap();
				splitData = null;
			} else {
				splitData.yPos += commonData.splitHeight;
			}
		}  
		
		commonData.rowStartID -= 1;
		for (int i = 0; i < commonData.colNum; i++) {
			commonData.addSpliteData(getContext(), commonData.colStartID+i, commonData.rowStartID,
					commonData.splitWidth*i, 0);
		}
	}
	
	private void outBottomBorder() {
		Iterator<SplitData> stuIter = commonData.splitList.iterator();  
		while (stuIter.hasNext()) {
			SplitData splitData = stuIter.next();
			if (splitData.rowID == commonData.rowStartID) {
				stuIter.remove();
				splitData.recycleBitmap();
				splitData = null;
			} else {
				splitData.yPos -= commonData.splitHeight;
			}
		} 
		
		for (int i = 0; i < commonData.colNum; i++) {
			commonData.addSpliteData(getContext(), commonData.colStartID+i, commonData.rowStartID+commonData.rowNum,
					commonData.splitWidth*i, commonData.splitHeight*(commonData.rowNum-1));
		}
		
		commonData.rowStartID += 1;
	}
	
	private void scaleUp(float px, float py) {
		float values[] = new float[9];
		matrix.getValues(values);
		float offsetX = px-values[2];
		float offsetY = py-values[5];
		int offsetColID = (int) (offsetX/(commonData.splitWidth*commonData.scaleSpace));
		int offsetRowID = (int) (offsetY/(commonData.splitHeight*commonData.scaleSpace));
		int scaleColID = offsetColID + commonData.colStartID;
		int scaleRowID = offsetRowID + commonData.rowStartID;
		SplitData scaleSplitData = commonData.findSplitData(scaleColID, scaleRowID);
		commonData.colStartID = scaleSplitData.colID*commonData.scaleSpace+
				((offsetX/commonData.scaleSpace-scaleSplitData.xPos) > commonData.splitWidth/2 ? 1 : 0) - offsetColID;
		commonData.rowStartID = scaleSplitData.rowID*commonData.scaleSpace+
		((offsetX/commonData.scaleSpace-scaleSplitData.yPos) > commonData.splitHeight/2 ? 1 : 0) - offsetRowID;
		
		Iterator<SplitData> stuIter = commonData.splitList.iterator();  
		while (stuIter.hasNext()) {
			SplitData splitData = stuIter.next();
			stuIter.remove();
			splitData.recycleBitmap();
			splitData = null;
		} 
		commonData.splitList.clear();
		
		for (int i = 0; i < commonData.rowNum; i++) {
			for (int j = 0; j < commonData.colNum; j++) {
				commonData.addSpliteData(getContext(), commonData.colStartID+j, commonData.rowStartID+i,
						commonData.splitWidth*j, commonData.splitHeight*i);
			}
		}
	}
	
	private void scaleDown(float px, float py) {
		float values[] = new float[9];
		matrix.getValues(values);
		float offsetX = px-values[2];
		float offsetY = py-values[5];
		int offsetColID = (int) (offsetX/(commonData.splitWidth));
		int offsetRowID = (int) (offsetY/(commonData.splitHeight));
		int scaleColID = offsetColID + commonData.colStartID;
		int scaleRowID = offsetRowID + commonData.rowStartID;
		SplitData scaleSplitData = commonData.findSplitData(scaleColID, scaleRowID);
		commonData.colStartID = scaleSplitData.colID/commonData.scaleSpace - offsetColID;
		commonData.rowStartID = scaleSplitData.rowID/commonData.scaleSpace - offsetRowID;
		
		Iterator<SplitData> stuIter = commonData.splitList.iterator();  
		while (stuIter.hasNext()) {
			SplitData splitData = stuIter.next();
			stuIter.remove();
			splitData.recycleBitmap();
			splitData = null;
		} 
		commonData.splitList.clear();
		
		for (int i = 0; i < commonData.rowNum; i++) {
			for (int j = 0; j < commonData.colNum; j++) {
				commonData.addSpliteData(getContext(), commonData.colStartID+j, commonData.rowStartID+i,
						commonData.splitWidth*j, commonData.splitHeight*i);
			}
		}
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}
	
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	private float rotation(MotionEvent event) {
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}
}
