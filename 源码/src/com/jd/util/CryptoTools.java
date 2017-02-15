package com.jd.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import android.util.Log;

/**
 * 
 * @author Administrator
 */

public class CryptoTools {
	private static AlgorithmParameterSpec iv = null;// 加密算法的参数接口，IvParameterSpec是它的一个实现
	private static DESKeySpec keySpec = null;
	private static Key key = null;
	private static SecretKeyFactory keyFactory = null;
	private static Cipher enCipher = null;
	private static Cipher deCipher = null;
	private static String encryptStr = "!@#$%^&*";
	private static int bufLen = 102400;
	private static String password = null;

	public CryptoTools() throws Exception {

	}

	public static void InitCrypt(String pass) {
		try {
			password = pass;
			String keyStr = getEncKey();
			keySpec = new DESKeySpec(keyStr.getBytes());// 设置密钥参数
			iv = new IvParameterSpec(keyStr.getBytes());// 设置向量
			keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂
			key = keyFactory.generateSecret(keySpec);// 得到密钥对象
			// enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");//
			// 得到加密对象Cipher
			enCipher = Cipher.getInstance("DES/ECB/NoPadding");// 得到加密对象Cipher
			enCipher.init(Cipher.ENCRYPT_MODE, key);// 设置工作模式为加密模式，给出密钥和向量
			deCipher = Cipher.getInstance("DES/ECB/NoPadding");
			deCipher.init(Cipher.DECRYPT_MODE, key);
		} catch (Exception e) {
			// TODO: handle exception
			com.jd.util.AppHelper.showInfoDlg(null, "初始化加密函数错误！");
		}
	}

	public static String getEncKey() {
		return (AppHelper.password + encryptStr).substring(0, 8);
	}

	public static byte[] encode(byte[] data) throws Exception {
		byte[] pasByte = enCipher.doFinal(data);
		return pasByte;
	}

	public static byte[] decode(byte[] data) throws Exception {
		byte[] pasByte = deCipher.doFinal(data);
		return pasByte;
	}

	/**
	 * 对指定路径下的所有文件进行加密
	 * 
	 * @param path
	 */
	public static void encryptFilesOfFolder(String path) {
		File dir = new File(path);
		String[] files = dir.list();

		for (int i = 0; i < files.length; i++) {
			String fileName = path + "/" + files[i];
			doCrypt(fileName, true);
		}
	}

	/**
	 * 加密或者解密指定的文件
	 * 
	 * @param fileName
	 *            要加密或者解密的文件
	 * @param encrypt
	 *            true:加密 false：解密
	 */
	private static void doCrypt(String fileName, boolean encrypt) {
		try {
			String tmpFileName = fileName + "tmp";
			InputStream fi = null;
			OutputStream fo = null;

			if (fileName.startsWith("smb://")) {
				fi = new SmbFileInputStream(fileName);
				fo = new SmbFileOutputStream(tmpFileName);
			} else {
				fi = new FileInputStream(fileName);
				fo = new FileOutputStream(tmpFileName);
			}

			byte[] data = new byte[bufLen];

			int len = fi.read(data, 0, bufLen);
			while (len != -1) {
				byte[] realData = new byte[len];
				for (int j = 0; j < len; j++) {
					realData[j] = data[j];
				}

				if (encrypt) {
					byte[] dataEnc = encode(realData);
					fo.write(dataEnc);
				} else {
					byte[] dataEnc = decode(realData);
					fo.write(dataEnc);
				}

				len = fi.read(data, 0, bufLen);
			}

			fi.close();
			fo.flush();
			fo.close();

			// 删除原文件，重命名临时文件
			File file = new File(fileName);
			file.delete();
			file = new File(tmpFileName);
			file.renameTo(new File(fileName));
		} catch (Exception e) {

		}
	}

	/**
	 * 对指定路径下的所有文件进行解密
	 * 
	 * @param path
	 */
	public static void decryptFilesOfFolder(String path) {
		File dir = new File(path);
		String[] files = dir.list();
		for (int i = 0; i < files.length; i++) {
			String fileName = path + "/" + files[i];
			doCrypt(fileName, false);
		}
	}

	/**
	 * 解密单个的文件
	 * 
	 * @param path
	 */
	public static void decryptFile(String path) {
		doCrypt(path, false);
	}

	/**
	 * 加密单个的文件
	 * 
	 * @param path
	 */
	public static void encryptFile(String path) {
		doCrypt(path, true);
	}

	// /**
	// * 返回图像文件的字节数据
	// * @param path
	// * @return
	// */
	// public static byte[] getImageFileBytes(String path)
	// {
	// try{
	// FileInputStream fi=new FileInputStream(path);
	// byte[] data=new byte[fi.available()];
	// fi.read(data);
	// fi.close();
	// byte[] dataEnc=decode(data);
	// return dataEnc;
	// } catch(Exception e)
	// {
	// Log.v("decryptFile", e.getMessage());
	// return null;
	// }
	// }

	/**
	 * 返回文件的字节数据，文件可能是被加密了，也可能没被加密
	 * 
	 * @param path
	 * @return
	 */
	public static byte[] getFileBytes(String path) {
		int pos = path.lastIndexOf("/");

		String fileName = null;
		if (pos > 0) {
			fileName = path.substring(pos + 1, path.length());
		}

		String realFileName = com.jd.util.StringUtil.decrypt(fileName);

		if (realFileName != null) {
			// 文件被加密，返回解密后的文件数据
			return getFileBytes(path, true);
		} else {
			// 文件未被加密，直接返回文件数据
			return getFileBytes(path, false);
		}
	}

	/**
	 * 返回加密文件解密以后的字节数据
	 * 
	 * @param path
	 * @return
	 */
	private static byte[] getFileBytes(String path, boolean decry) {
		try {
			java.io.InputStream fis = null;
			int fileSize = 0;
			
			if (decry) {
				// 图片被加密
				
				if (path.startsWith("smb://")) {
					SmbFile file=new SmbFile(path);
					fis = new SmbFileInputStream(path);
					//先取出文件的长度
					fis.skip(file.length()-4);
					byte lenData[] = new byte[4];
					fis.read(lenData);
					fileSize=com.jd.util.CryptoTools.byte4ToInt(lenData, 0);
					fis.close();
					fis=new SmbFileInputStream(path);
				} else {
					File file=new File(path);
					fis = new FileInputStream(path);
					//先取出文件的长度
					fis.skip(file.length()-4);
					byte lenData[] = new byte[4];
					fis.read(lenData);
					fileSize=com.jd.util.CryptoTools.byte4ToInt(lenData, 0);
					fis.close();
					fis = new FileInputStream(path);
				}
				
				byte[] data = new byte[com.jd.util.AppHelper.deBufLen];
				byte[] retBytes = new byte[fileSize];
				int count = 0;
				int len = fis.read(data);
				int totalBytes=0;
				
				while (len != -1) {
					data = com.jd.util.CryptoTools.decode(data);
					totalBytes+=len;
					
					if(totalBytes>=fileSize){
						//最后一段数据
						for (int i = 0; i < fileSize%com.jd.util.AppHelper.deBufLen; i++) {
							retBytes[count] = data[i];
							count++;
						}					
						break;
					}else{
						for (int i = 0; i < len; i++) {
							retBytes[count] = data[i];
							count++;
						}
					}

					len = fis.read(data);
				}

				fis.close();
				System.gc();
				return Arrays.copyOfRange(retBytes, 0, fileSize);
			} else {
				// 图片未加密
				if (path.startsWith("smb://")) {
					fis = new SmbFileInputStream(path);
					SmbFile file=new SmbFile(path);
					fileSize=(int)file.length();
				} else {
					fis = new FileInputStream(path);
					File file=new File(path);
					fileSize=(int)file.length();
				}
				
				byte[] retBytes = new byte[fileSize];
				fis.read(retBytes);
				fis.close();
				System.gc();
				return retBytes;
			}

		} catch (Exception e) {
			System.gc();
			return null;
		}

	}

	public static byte[] intToByte4(int i) {
		byte[] targets = new byte[4];
		targets[3] = (byte) (i & 0xFF);
		targets[2] = (byte) (i >> 8 & 0xFF);
		targets[1] = (byte) (i >> 16 & 0xFF);
		targets[0] = (byte) (i >> 24 & 0xFF);
		return targets;
	}

	public static int byte4ToInt(byte[] bytes, int off) {
		int b0 = bytes[off] & 0xFF;
		int b1 = bytes[off + 1] & 0xFF;
		int b2 = bytes[off + 2] & 0xFF;
		int b3 = bytes[off + 3] & 0xFF;
		return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
	}

}
