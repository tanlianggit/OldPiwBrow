package com.jd.ui;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class Help extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(com.tl.pic.brow.R.layout.help);
		
		TextView txtHelp=(TextView)findViewById(com.tl.pic.brow.R.id.txtHelp);
		txtHelp.setText(Html.fromHtml(
				"<h3 style='color:red'>【注】：如果曾使用过【相册】的1.0版，需要用原版本的【相册】将相片解密后再在新版相册中进行管理</h3> "+
				"<h3>文件管理</h3>"+
				"1：提供手机内存卡与电脑之间的文件拷贝、剪切、粘贴功能。<br/>" +
				"2：提供文件的加密、解密功能。<br/>" +
				"1>密码可以任意输入，都可以用来加密文件。<br/>2>每个密码只能解密用相同密码加密的文件。<br/>3>每个密码都能显示所有未加密的文件夹和文件。<br/>"+
				"3：由于Android操作系统限制，不能向SdCard上拷贝文件，也不能修改和删除SdCard卡上的文件和文件夹。但可以将SdCard卡上的文件拷贝到手机内存卡以及电脑。"+
				"<br/><h3>图片浏览：</h3>"+
				"1：图片手动切换。<br/>"+
				"2：图片自动顺序播放。<br/>"+
				"3：图片随机播放。<br/>"+
				"4：图片的手动放大、缩小。<br/>" +
				"5：图片自动放大、缩小。<br/>"+
				"6：将指定图片设为手机壁纸。<br/>"+
				"<br/><h3>拼图：</h3>"+
				"1：提供不同难度的拼图（3*3,4*4,5*5)。<br/>"+
				"2：提供两种拼图模式：<br/>1>任意两个图块交换。<br/>2>相邻图块交换。<br/>"+
				"3：可选择在拼图时是否播放音乐。<br/>"+
				"4：记录拼图的时间以及步数。<br/>"+
				"<h3>收藏功能：</h3>"+
				"可以将经常浏览的文件夹收藏起来，以方便快速访问。<br/>"+
				"<h3>基本操作介绍</h3>"+
				"1：双击文件夹会进入该文件夹，显示该文件夹的下级文件夹和文件。<br/>"+
				"2：双击图片会进入图片浏览模式。不支持对其它类型文件的双击操作。<br/>"+
				"3：长按图片会进入拼图模式。</br>"+
				"4：可设置多长时间不操作时自动锁住应用（需重新输入密码）。</br/>"+
				"5：在登录时，可输入任意的密码。不同的密码只能解密用该密码加密的文件。通过输入任意的密码可防止私密图片泄露。</br>"+
				"<h3>网上邻居操作：</h3>"+
				"1：在服务器中输入计算机的盘符，或者共享的文件夹，如：" +
				"192.168.1.100/d$（d$是指定计算机的d盘）或者192.168.1.100/我的相册（'我的相册'是计算机上共享的文件夹的名称）。<br/>"+
				"2：输入访问计算机所需的账号和密码<br/>"+
				"3：可以在重命名中给网上邻居一个友好的名字，如可以将'192.168.1.100/我的相册'重命名为'我的相册'。"
				
				
				));
				
	}

}
