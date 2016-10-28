package com.szeba.wyv.utilities;

import java.util.Random;

public final class MathUtilities {

	private static Random rand = new Random();
	
	private MathUtilities() {}
	
	/** 
	 * Edit a variables value bounded by a minimum and maximum value. 
	 */
	public static int boundedVariable(int var, int add, int min, int max) {
		int result = var + add;
		if (result < min) { result = min; }
		if (result > max) { result = max; }
		return result;
	}
	
	public static double boundedVariable(double var, double add, double min, double max) {
		double result = var + add;
		if (result < min) { result = min; }
		if (result > max) { result = max; }
		return result;
	}
	
	public static float boundedVariable(float var, float add, float min, float max) {
		float result = var + add;
		if (result < min) { result = min; }
		if (result > max) { result = max; }
		return result;
	}
	
	/**
	 * Return the bigger value
	 */
	public static int getBigger(int a, int b) {
		if (a > b) {
			return a;
		} else if (a == b) {
			return a;
		} else {
			return b;
		}
	}
	
	public static int getSmaller(int a, int b) {
		if (a < b) {
			return a;
		} else if (a == b) {
			return a;
		} else {
			return b;
		}
	}
	
	/**
	 * Divides two numbers. If the divided number was negative, then reduces
	 * the result with one. 
	 */
	public static int divCorrect(int dividend, int divisor) {
		int result = dividend / divisor;
		if (dividend < 0) { result--; }
		return result;
	}
	
	public static int random(int min, int max) {
		return rand.nextInt((max - min) + 1) + min;
	}
	
}
