https://github.com/rmtheis/tess-two
直接在工程中引入依赖 
compile 'com.rmtheis:tess-two:7.0.0'
或者下载工程:
git clone https://github.com/rmtheis/tess-two.git
tess-two模块,拿jni集成
编译tess-two时候出现找不到插件的错误,这个插件是打包library并且上传到远程仓库的插件,删除掉就可以.


http://opencv.org/
下载opencv-android-sdk
可以使用jni方式开发也可以直接使用java方式


ocr:
1、从图片中截取身份证区域
2、从身份证中截取身份证号码区域
3、识别


opencv对图像预处理
1、灰度化处理 (降燥、排除干扰，识别对是否彩色无意义)
  图片灰度化处理就是将指定图片每个像素点的RGB三个分量通过一定的算法计算出该像素点的灰度值，使图像只含亮度而不含色彩信息。
2、二值化
  二值化处理就是将经过灰度化处理的图片转换为只包含黑色和白色两种颜色的图像，他们之间没有其他灰度的变化。在二值图中用255便是白色，0表示黑色。
3、轮廓检测，获得图片中最大轮廓(这个轮廓就是身份证)
4、腐蚀
  图片的腐蚀就是将得到的二值图中的黑色块进行放大。即连接图片中相邻黑色像素点的元素。通过腐蚀可以把身份证上的身份证号码连接在一起形成一个矩形区域。
5、轮廊检测
   图片经过腐蚀操作后相邻点会连接在一起形成一个大的区域，这个时候通过轮廊检测就可以把每个大的区域找出来，这样就可以定位到身份证上面号码的区域。



准备训练
tesseract工具
从源码编译:https://github.com/tesseract-ocr/tesseract/wiki/Compiling
windows可以直接前往https://github.com/tesseract-ocr/tesseract/wiki/Downloads下载tesseract-ocr-setup-3.02.02.exe


编译脚本见 build.sh

训练过程见training.MD




