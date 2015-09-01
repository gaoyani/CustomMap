package com.example.custommap;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class CommonData {
	
	public int minScale = 1;
	public int maxScale = 4;
	public int scaleSpace = 2;
	public int curScale = 1;	
	
	public int mapWidth = 4096;
	public int mapHeight = 2560;
	
	public int colNum;
	public int rowNum;
	
	public int colStartID;
	public int rowStartID;
	
	public int screenWidth;
	public int screenHeight;
	
	public int splitWidth = 256;
	public int splitHeight = 256;
	
	public List<SplitData> splitList = new ArrayList<SplitData>();
	
	public static class SplitData {
		public Bitmap bitmap;
		public int xPos;
		public int yPos;
		
		public int rowID;
		public int colID;
		
//		public void loadBitmap(final Context context, final int id) {
//			new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(id));
//				}
//			}).start();
//		}
		
		public void loadBitmap(final AssetManager manager, final String fileName) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						InputStream is = manager.open(fileName);
						bitmap = BitmapFactory.decodeStream(is);
						is.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
		
		public void recycleBitmap() {
			if (bitmap != null) {
				bitmap.recycle();
				bitmap = null;
			}
		}
		
		public boolean isInvalid() {
			return bitmap == null;
		}
	}
	
	public boolean isOutLeftBorder() {
		for (SplitData splitData : splitList) {
			if (splitData.colID == 0) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isOutRightBorder() {
		for (SplitData splitData : splitList) {
			if (splitData.colID == mapWidth*curScale/splitWidth-1) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isOutTopBorder() {
		for (SplitData splitData : splitList) {
			if (splitData.rowID == 0) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isOutBottomBorder() {
		for (SplitData splitData : splitList) {
			if (splitData.rowID == mapHeight*curScale/splitHeight-1) {
				return true;
			}
		}
		
		return false;
	}
	
	public SplitData findSplitData(int colID, int rowID) {
		for (SplitData splitData : splitList) {
			if (splitData.rowID == rowID && splitData.colID == colID) {
				return splitData;
			}
		}
		
		return null;
	}
	
	public void addSpliteData(Context context, int colID, int rowID, int xPos, int yPos) {
		SplitData splitData = new SplitData();
		splitData.colID = colID;
		splitData.rowID = rowID;
		splitData.xPos = xPos;
		splitData.yPos = yPos;
		splitData.loadBitmap(context.getAssets(), "scale_"+curScale+"/map"+colID+"_"+rowID+".png");
		Log.i("png", "scale_"+curScale+"/map"+colID+"_"+rowID+".png");
		splitList.add(splitData);
	}
	
	public int getResourceID(Context context, int colID, int rowID) {
		return context.getResources().getIdentifier(
				"scale_"+curScale+"/map"+colID+"_"+rowID, "raw", context.getPackageName());
	}
}
