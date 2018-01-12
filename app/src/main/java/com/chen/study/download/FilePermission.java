package com.chen.study.download;

import java.lang.reflect.Method;

/**
 * 文件操作工具�?
 * 
 * @author Yichou
 * 
 * @date 2013-08-31
 */
public final class FilePermission
{

	public static final int User_RWX = 00700;
	public static final int S_IRUSR = 00400;
	public static final int S_IWUSR = 00200;
	public static final int S_IXUSR = 00100;

	public static final int Group_RWX = 00070;
	public static final int Group_R = 00040;
	public static final int Group_W = 00020;
	public static final int Group_X = 00010;

	public static final int Other_RWX = 00007;
	public static final int Other_R = 00004;
	public static final int Other_W = 00002;
	public static final int Other_X = 00001;

	// public static native int setPermissions(String file, int mode, int uid,
	// int gid);
	public static int set(String file, int mode) {
		return set(file, mode, -1, -1);
	}

	private static final Class<?>[] SIG_SET_PERMISSION = new Class<?>[] {
			String.class, int.class, int.class, int.class };

	public static int set(String file, int mode, int uid, int gid) {
		try {
			Class<?> clazz = Class.forName("android.os.FileUtils");
			Method method = clazz.getDeclaredMethod("setPermissions",
					SIG_SET_PERMISSION);
			method.setAccessible(true);
			return (Integer) method.invoke(null, file, mode, uid, gid);
		} catch (Exception e) {
		}

		return -1;
	}


}
